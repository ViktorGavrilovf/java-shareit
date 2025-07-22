package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.dto.booking.BookingDto;
import ru.practicum.shareit.dto.booking.BookingStatus;
import ru.practicum.shareit.dto.items.ItemDto;
import ru.practicum.shareit.dto.user.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> jacksonTester;

    @Test
    void testSerialize() throws IOException {
        BookingDto dto = BookingDto.builder()
                .id(10L)
                .start(LocalDateTime.of(2025, 1, 1, 12, 0))
                .end(LocalDateTime.of(2025, 1, 2, 12, 0))
                .item(ItemDto.builder().id(5L).name("Дрель").description("Мощная").available(true).build())
                .booker(UserDto.builder().id(3L).name("Вася").email("vasya@mail.com").build())
                .status(BookingStatus.APPROVED)
                .build();

        String json = jacksonTester.write(dto).getJson();

        assertThat(json).contains("\"id\":10");
        assertThat(json).contains("\"status\":\"APPROVED\"");
        assertThat(json).contains("\"start\":\"2025-01-01T12:00:00\"");
        assertThat(json).contains("\"end\":\"2025-01-02T12:00:00\"");
        assertThat(json).contains("\"item\":{\"id\":5,\"name\":\"Дрель\"");
        assertThat(json).contains("\"booker\":{\"id\":3,\"name\":\"Вася\"");
    }

    @Test
    void testDeserialize() throws IOException {
        String json = "{"
                + "\"id\":20,"
                + "\"start\":\"2025-02-10T10:30:00\","
                + "\"end\":\"2025-02-11T10:30:00\","
                + "\"status\":\"WAITING\","
                + "\"item\":{"
                +     "\"id\":7,"
                +     "\"name\":\"Пылесос\","
                +     "\"description\":\"Сильный\","
                +     "\"available\":true"
                + "},"
                + "\"booker\":{"
                +     "\"id\":4,"
                +     "\"name\":\"Катя\","
                +     "\"email\":\"katya@example.com\""
                + "}"
                + "}";


        BookingDto dto = jacksonTester.parseObject(json);

        assertThat(dto.getId()).isEqualTo(20L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2025, 2, 10, 10, 30));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2025, 2, 11, 10, 30));
        assertThat(dto.getItem().getId()).isEqualTo(7L);
        assertThat(dto.getItem().getName()).isEqualTo("Пылесос");
        assertThat(dto.getBooker().getEmail()).isEqualTo("katya@example.com");
    }
}
