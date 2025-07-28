package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.dto.booking.BookingCreateDto;

import java.util.Map;

@Component
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createBooking(long userId, BookingCreateDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> approveBooking(long ownerId, long bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, ownerId);
    }

    public ResponseEntity<Object> getBooking(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingsForUser(long userId, String state) {
        return get("", userId, Map.of("state", state));
    }

    public ResponseEntity<Object> getBookingsForOwner(long ownerId, String state) {
        return get("/owner", ownerId, Map.of("state", state));
    }
}
