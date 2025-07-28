package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.dto.request.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    @Test
    void testSerialize() throws IOException {
        LocalDateTime created = LocalDateTime.of(2024, 12, 31, 23, 59);
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(created)
                .build();


        String json = jacksonTester.write(dto).getJson();

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Нужна дрель\"");
        assertThat(json).contains("\"created\":\"2024-12-31T23:59:00\"");
    }

    @Test
    void testDeserialize() throws IOException {
        String jsonContent = "{"
                + "\"id\": 5,"
                + "\"description\": \"Пылесос\","
                + "\"created\": \"2025-01-01T10:00:00\""
                + "}";

        ItemRequestDto dto = jacksonTester.parseObject(jsonContent);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getDescription()).isEqualTo("Пылесос");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0));
    }
}
