package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestService {
    private final ItemRequestStorage itemRequestStorage;
    private final UserStorage userStorage;

    public ItemRequestDto addRequest(Long userId, ItemRequestDto dto) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toDto(itemRequestStorage.save(request));
    }

    public List<ItemRequestDto> getOwnRequests(Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return itemRequestStorage.findAllByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllRequests(Long userId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return itemRequestStorage.findAllByOtherRequests(userId).stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        return ItemRequestMapper.toDto(itemRequest);
    }
}
