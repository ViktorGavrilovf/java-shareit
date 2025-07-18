package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingCreateDto dto);

    BookingDto approveBooking(Long ownerId, Long bookingId, boolean approve);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getBookingsForUser(Long userId, String state);

    List<BookingDto> getBookingsForOwner(Long ownerId, String state);
}
