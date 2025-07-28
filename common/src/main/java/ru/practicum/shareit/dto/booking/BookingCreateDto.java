package ru.practicum.shareit.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingCreateDto {
    private Long itemId;

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;
}
