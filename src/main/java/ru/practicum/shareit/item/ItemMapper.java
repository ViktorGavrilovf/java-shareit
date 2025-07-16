package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                List.of()
        );
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        return new Item(
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                null);
    }

    public static void updateItemFromDto(ItemDto itemDto, Item target) {
        if (itemDto.getName() != null) {
            target.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            target.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            target.setAvailable(itemDto.getAvailable());
        }
    }

    public static ItemWithBookingDto toItemWithBookingDto(Item item) {
        return new ItemWithBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                List.of()
        );
    }

}
