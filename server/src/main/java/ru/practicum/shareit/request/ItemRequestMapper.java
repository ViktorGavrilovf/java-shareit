package ru.practicum.shareit.request;

import ru.practicum.shareit.dto.items.ItemDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.dto.request.ItemRequestWithAnswersDto;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest request) {
        if (request == null) {
            return null;
        }
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated()
        );
    }

    public static ItemRequestWithAnswersDto toDtoWithAnswers(ItemRequest request, List<ItemDto> items) {
        if (request == null || items == null) {
            return null;
        }
        ItemRequestWithAnswersDto dto = new ItemRequestWithAnswersDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(items);
        return dto;
    }
}
