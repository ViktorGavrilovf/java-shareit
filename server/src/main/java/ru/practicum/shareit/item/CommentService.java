package ru.practicum.shareit.item;

import ru.practicum.shareit.dto.items.CommentDto;

public interface CommentService {
    CommentDto addComment(Long userId, Long itemId, CommentDto dto);
}
