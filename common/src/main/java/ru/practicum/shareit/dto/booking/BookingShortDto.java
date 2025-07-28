package ru.practicum.shareit.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingShortDto {
    private Long id;
    private Long bookerId;
}
