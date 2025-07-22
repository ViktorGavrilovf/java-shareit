package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.dto.items.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("Комментарий может оставить только тот, кто уже арендовал вещь");
        }

        Comment comment = CommentMapper.fromDto(dto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toDto(commentRepository.save(comment));
    }
}
