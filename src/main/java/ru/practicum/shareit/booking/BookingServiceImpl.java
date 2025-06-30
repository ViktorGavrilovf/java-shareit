package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public BookingDto createBooking(Long userId, BookingCreateDto dto) {
        User booker = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemStorage.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Нельзя забронировать свою собственную вещь");
        }

        if (!item.getAvailable()) {
            throw new AccessDeniedException("Вещь недоступна для бронирования");
        }

        if (dto.getStart().isAfter(dto.getEnd()) || dto.getStart().isEqual(dto.getEnd())) {
            throw new ValidationException("Дата начала бронирования должна быть раньше даты окончания");
        }

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }


    @Override
    public BookingDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Подтверждать бронирование может только владелец вещи");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ValidationException("Статус уже был изменён");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId) &&
                !Objects.equals(booking.getBooker().getId(), userId)) {
            throw new AccessDeniedException("Нет доступа к бронированию");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsForUser(Long userId, String state) {
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Sort sort = Sort.by("start").descending();

        switch (state) {
            case "ALL" -> {
                return bookingRepository.findByBookerId(userId, sort).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "CURRENT" -> {
                return bookingRepository.findCurrentBookingsByBooker(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "PAST" -> {
                return bookingRepository.findByBookerIdAndEndBefore(userId, LocalDateTime.now(), sort).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "FUTURE" -> {
                return bookingRepository.findByBookerIdAndStartAfter(userId, LocalDateTime.now(), sort).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "WAITING" -> {
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "REJECTED" -> {
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            default -> throw new ValidationException("Неизвестное состояние бронирования: " + state);
        }
    }

    @Override
    public List<BookingDto> getBookingsForOwner(Long ownerId, String state) {
        userStorage.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Sort sort = Sort.by("start").descending();

        switch (state) {
            case "ALL" -> {
                return bookingRepository.findByItemOwnerId(ownerId, sort).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "CURRENT" -> {
                return bookingRepository.findCurrentBookingsByOwner(ownerId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "PAST" -> {
                return bookingRepository.findByItemOwnerIdAndEndBefore(ownerId, LocalDateTime.now(), sort).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "FUTURE" -> {
                return bookingRepository.findByItemOwnerIdAndStartAfter(ownerId, LocalDateTime.now(), sort).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "WAITING" -> {
                return bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, sort).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            case "REJECTED" -> {
                return bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, sort).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            default -> throw new ValidationException("Неизвестное состояние бронирования: " + state);
        }
    }
}
