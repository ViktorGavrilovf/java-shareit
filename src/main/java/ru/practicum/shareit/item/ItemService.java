package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        User owner = userStorage.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return ItemMapper.toItemDto(itemStorage.saveItem(ItemMapper.toItem(itemDto, owner)));
    }

    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        Item existing = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Только владелец может редактировать вещь");
        }

        ItemMapper.updateItemFromDto(itemDto, existing);
        return ItemMapper.toItemDto(itemStorage.updateItem(existing));
    }

    public ItemDto getItem(Long itemId) {
        return itemStorage.findById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemStorage.getAllItems(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        // Фильтрация по available уже была реализована в storage, или правильнее делать её в сервисе?
        return itemStorage.searchItem(text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
