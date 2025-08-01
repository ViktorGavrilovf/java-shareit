package ru.practicum.shareit.item;

import ru.practicum.shareit.dto.items.ItemDto;
import ru.practicum.shareit.dto.items.ItemWithBookingDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long ownerId, ItemDto itemDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    ItemWithBookingDto getItem(Long itemId, Long userId);

    List<ItemWithBookingDto> getItemsByOwner(Long ownerId);

    List<ItemDto> searchItems(String text);
}
