package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item saveItem(Item item);

    Item updateItem(Item item);

    Optional<Item> findById(Long id);

    List<Item> getAllItems(Long ownerId);

    List<Item> searchItem(String text);
}
