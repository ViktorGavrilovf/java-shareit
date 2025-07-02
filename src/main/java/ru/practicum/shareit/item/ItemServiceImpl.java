package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        checkNameAndDescription(itemDto);

        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, owner)));
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        checkNameAndDescription(itemDto);

        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Только владелец может редактировать вещь");
        }

        ItemMapper.updateItemFromDto(itemDto, existing);
        return ItemMapper.toItemDto(itemRepository.save(existing));
    }

    @Override
    public ItemWithBookingDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .toList();

        BookingShortDto lastBookingDto = null;
        BookingShortDto nextBookingDto = null;

        if (Objects.equals(item.getOwner().getId(), userId)) {
            Booking lastBooking = bookingRepository.findByItemIdAndStatusOrderByStartDesc(
                            item.getId(), BookingStatus.APPROVED)
                    .stream()
                    .filter(b -> !b.getStart().isAfter(LocalDateTime.now()))
                    .findFirst()
                    .orElse(null);

            lastBookingDto = (lastBooking != null)
                    ? new BookingShortDto(lastBooking.getId(), lastBooking.getBooker().getId())
                    : null;

            Booking nextBooking = bookingRepository.findByItemIdAndStatusOrderByStartDesc(
                            item.getId(), BookingStatus.APPROVED)
                    .stream()
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .reduce((first, second) -> second)
                    .orElse(null);

            nextBookingDto = (nextBooking != null)
                    ? new BookingShortDto(nextBooking.getId(), nextBooking.getBooker().getId())
                    : null;
        }

        ItemWithBookingDto dto = ItemMapper.toItemWithBookingDto(item);
        dto.setComments(comments);
        dto.setLastBooking(lastBookingDto);
        dto.setNextBooking(nextBookingDto);

        return dto;
    }

    @Override
    public List<ItemWithBookingDto> getItemsByOwner(Long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<ItemWithBookingDto> result = new ArrayList<>();

        for (Item item : items) {
            List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId()).stream()
                    .map(CommentMapper::toDto)
                    .toList();

            Booking lastBooking = bookingRepository.findByItemIdAndStatusOrderByStartDesc(
                            item.getId(), BookingStatus.APPROVED)
                    .stream()
                    .filter(b -> !b.getStart().isAfter(LocalDateTime.now()))
                    .findFirst()
                    .orElse(null);

            BookingShortDto lastBookingDto = (lastBooking != null)
                    ? new BookingShortDto(lastBooking.getId(), lastBooking.getBooker().getId())
                    : null;

            Booking nextBooking = bookingRepository.findByItemIdAndStatusOrderByStartDesc(
                            item.getId(), BookingStatus.APPROVED)
                    .stream()
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .reduce((first, second) -> second)
                    .orElse(null);

            BookingShortDto nextBookingDto = (nextBooking != null)
                    ? new BookingShortDto(nextBooking.getId(), nextBooking.getBooker().getId())
                    : null;

            ItemWithBookingDto dto = ItemMapper.toItemWithBookingDto(item);
            dto.setComments(comments);
            dto.setLastBooking(lastBookingDto);
            dto.setNextBooking(nextBookingDto);

            result.add(dto);
        }
        return result;
    }


    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        return itemRepository.searchAvailable(text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkNameAndDescription(ItemDto itemDto) {
        if (itemDto.getName() != null && itemDto.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (itemDto.getDescription() != null && itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
    }
}
