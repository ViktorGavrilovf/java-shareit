package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final CommentRepository commentRepository;

    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        User owner = userStorage.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return ItemMapper.toItemDto(itemStorage.save(ItemMapper.toItem(itemDto, owner)));
    }

    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        Item existing = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Только владелец может редактировать вещь");
        }

        ItemMapper.updateItemFromDto(itemDto, existing);
        return ItemMapper.toItemDto(itemStorage.save(existing));
    }

    public ItemDto getItem(Long itemId) {
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);
        List<CommentDto> commentDtos = comments.stream().map(CommentMapper::toDto).toList();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(commentDtos);
        return itemDto;
    }

    public List<ItemDto> getItemsByOwner(Long ownerId) {
        List<ItemDto> result = new ArrayList<>();
        List<Item> items = itemStorage.findAllByOwnerId(ownerId);
        for (Item item : items) {
            List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId()).stream()
                    .map(CommentMapper::toDto)
                    .toList();
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemDto.setComments(comments);
            result.add(itemDto);
        }
        return result;
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        return itemStorage.searchAvailable(text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
