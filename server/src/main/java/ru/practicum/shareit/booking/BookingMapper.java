package ru.practicum.shareit.booking;

import ru.practicum.shareit.dto.booking.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setItem(ItemMapper.toItemDto(booking.getItem()));
        dto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        return dto;
    }
}
