package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.dto.request.ItemRequestWithAnswersDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ItemRequestService.class, ItemRequestMapper.class, ItemMapper.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requester;
    private User otherUser;
    private ItemRequest savedRequest;

    @BeforeEach
    void setup() {
        requester = userRepository.save(User.builder()
                .name("Requester")
                .email("req@mail.com")
                .build());
        otherUser = userRepository.save(User.builder()
                .name("Other")
                .email("other@mail.com")
                .build());

        savedRequest = requestRepository.save(ItemRequest.builder()
                .description("Нужна вещь")
                .requestor(requester)
                .created(LocalDateTime.now().minusDays(1))
                .build());

        itemRepository.save(Item.builder()
                .name("Отвёртка")
                .description("Крестовая")
                .available(true)
                .owner(otherUser)
                .request(savedRequest)
                .build());
    }

    @Test
    void testAddRequest() {
        ItemRequestDto dto = ItemRequestDto.builder().description("Дрель").build();

        ItemRequestDto result = requestService.addRequest(requester.getId(), dto);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Дрель");
        assertThat(result.getId()).isNotNull();

        ItemRequest fromDb = requestRepository.findById(result.getId()).orElseThrow();

        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getDescription()).isEqualTo(result.getDescription());
        assertThat(fromDb.getId()).isEqualTo(result.getId());
    }

    @Test
    void testGetOwnRequests() {
        List<ItemRequestWithAnswersDto> own = requestService.getOwnRequests(requester.getId());

        assertThat(own).hasSize(1);
        assertThat(own.get(0).getDescription()).isEqualTo("Нужна вещь");
        assertThat(own.get(0).getItems()).hasSize(1);
        assertThat(own.get(0).getItems().get(0).getName()).isEqualTo("Отвёртка");
    }

    @Test
    void testGetAllRequests() {
        List<ItemRequestWithAnswersDto> all = requestService.getAllRequests(otherUser.getId());

        assertThat(all).hasSize(1);
        assertThat(all.get(0).getDescription()).isEqualTo("Нужна вещь");
    }

    @Test
    void testGetRequestById() {
        ItemRequestWithAnswersDto dto = requestService.getRequestById(otherUser.getId(), savedRequest.getId());

        assertThat(dto).isNotNull();
        assertThat(dto.getDescription()).isEqualTo("Нужна вещь");
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Отвёртка");
    }
}
