package ru.practicum.shareit.dto.items;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    @NotBlank
    @Size(max = 255, message = "Имя не должно превышать 255 символов")
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;

    private List<CommentDto> comments;

    private Long requestId;
}
