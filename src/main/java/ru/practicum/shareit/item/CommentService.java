package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;

public interface CommentService {
    CommentDto addComment(Long userId, Long itemId, CommentDto dto);
}
