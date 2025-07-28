package ru.practicum.shareit.booking;

import ru.practicum.shareit.dto.booking.BookingCreateDto;
import ru.practicum.shareit.dto.booking.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingCreateDto dto);

    BookingDto approveBooking(Long ownerId, Long bookingId, boolean approve);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getBookingsForUser(Long userId, String state);

    List<BookingDto> getBookingsForOwner(Long ownerId, String state);
}
