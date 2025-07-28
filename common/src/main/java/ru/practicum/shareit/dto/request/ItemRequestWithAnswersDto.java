package ru.practicum.shareit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.dto.items.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestWithAnswersDto {
    private Long id;
    @NotBlank
    private String description;
    @NotBlank
    private LocalDateTime created;
    private List<ItemDto> items;
}
