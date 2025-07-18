package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return dto;
    }

    public static Item toItem(ItemDto dto, User owner) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);

        if (dto.getRequestId() != null) {
            ItemRequest request = new ItemRequest();
            request.setId(dto.getRequestId());
            item.setRequest(request);
        }

        return item;
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
