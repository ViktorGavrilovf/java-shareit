package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.dto.items.CommentDto;
import ru.practicum.shareit.dto.items.ItemDto;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createItem(long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItem(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getUserItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> search(String text, long userId) {
        return get("/search", userId, Map.of("text", text));
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
