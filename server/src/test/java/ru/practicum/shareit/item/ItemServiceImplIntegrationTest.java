package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.dto.booking.BookingStatus;
import ru.practicum.shareit.dto.items.CommentDto;
import ru.practicum.shareit.dto.items.ItemDto;
import ru.practicum.shareit.dto.items.ItemWithBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ItemServiceImpl.class, ItemMapper.class, CommentServiceImpl.class, CommentMapper.class})
public class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    private User owner;
    private ItemRequest request;
    private Item item;
    private User booker;
    private Booking pastBooking;
    private Booking futureBooking;

    @BeforeEach
    void setup() {
        bookingRepository.deleteAll();
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();


        owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@mail.com")
                .build());

        request = requestRepository.save(ItemRequest.builder()
                .description("Need item")
                .requestor(owner)
                .created(LocalDateTime.now())
                .build());


        item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Power Drill")
                .available(true)
                .owner(owner)
                .request(request)
                .build());

        booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@mail.com")
                .build());

        pastBooking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build());

        futureBooking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .status(BookingStatus.APPROVED)
                .build());
    }

    @Test
    void testGetItem() {
        ItemWithBookingDto result = itemService.getItem(item.getId(), owner.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getLastBooking().getId()).isEqualTo(pastBooking.getId());
        assertThat(result.getNextBooking()).isNotNull();
        assertThat(result.getNextBooking().getId()).isEqualTo(futureBooking.getId());
    }

    @Test
    void testCreateItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .requestId(request.getId())
                .build();
        ItemDto created = itemService.addItem(owner.getId(), itemDto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Drill");
        assertThat(created.getDescription()).isEqualTo("Powerful drill");
        assertThat(created.getAvailable()).isTrue();
        assertThat(created.getRequestId()).isEqualTo(request.getId());

        Item itemFromDb = itemRepository.findById(created.getId()).orElseThrow();
        assertThat(itemFromDb.getId()).isEqualTo(created.getId());
    }

    @Test
    void testUpdateItem() {
        ItemDto updateDto = ItemDto.builder()
                .name("New Name")
                .description("New Description")
                .available(true)
                .build();

        ItemDto updated = itemService.updateItem(owner.getId(), item.getId(), updateDto);

        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getDescription()).isEqualTo("New Description");
        assertThat(updated.getAvailable()).isTrue();

        Item itemFromDb = itemRepository.findById(item.getId()).orElseThrow(
                () -> new NotFoundException("Вещь не найдена"));

        assertThat(itemFromDb.getName()).isEqualTo("New Name");
        assertThat(itemFromDb.getDescription()).isEqualTo("New Description");
        assertThat(itemFromDb.getAvailable()).isTrue();
    }

    @Test
    void testAddComment() {
        CommentDto commentDto = CommentDto.builder()
                .text("Great Text")
                .build();
        CommentDto saved = commentService.addComment(booker.getId(), item.getId(), commentDto);

        assertThat(saved.getText()).isNotNull();
        assertThat(saved.getText()).isEqualTo("Great Text");
        assertThat(saved.getAuthorName()).isEqualTo(booker.getName());

        Comment commentFromDb = commentRepository.findById(saved.getId()).orElseThrow(
                () -> new NotFoundException("Комментарий не найден"));
        assertThat(commentFromDb.getText()).isEqualTo("Great Text");
        assertThat(commentFromDb.getAuthor().getId()).isEqualTo(booker.getId());
    }

    @Test
    void testSearchAvailableItems() {
        List<ItemDto> items = itemService.searchItems("drill");

        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0).getId()).isEqualTo(item.getId());
        assertThat(items.get(0).getName()).isEqualTo(item.getName());
    }
}
