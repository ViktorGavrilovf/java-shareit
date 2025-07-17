package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestDto addRequest(Long userId, ItemRequestDto dto) {
        User user = checkExistUser(userId);
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toDto(itemRequestRepository.save(request));
    }

    public List<ItemRequestWithAnswersDto> getOwnRequests(Long userId) {
        checkExistUser(userId);
        return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId).stream()
                .map(request -> ItemRequestMapper.toDtoWithAnswers(request,
                        itemRepository.findAllByRequestId(request.getId()).stream()
                                .map(ItemMapper::toItemDto)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
        public List<ItemRequestWithAnswersDto> getAllRequests(Long userId) {
            checkExistUser(userId);
            return itemRequestRepository.findAllByOtherRequests(userId).stream()
                    .map(request -> ItemRequestMapper.toDtoWithAnswers(request,
                            itemRepository.findAllByRequestId(request.getId()).stream()
                                    .map(ItemMapper::toItemDto)
                                    .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());
        }

        public ItemRequestWithAnswersDto getRequestById(Long userId, Long requestId) {
            checkExistUser(userId);
            ItemRequest request = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));

            List<ItemDto> items = itemRepository.findAllByRequestId(requestId).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());

            return ItemRequestMapper.toDtoWithAnswers(request, items);
        }

        private User checkExistUser(Long userId) {
            return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        }
}
