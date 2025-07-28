package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.dto.items.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Super")
                .available(true)
                .requestId(123L)
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Super");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(123);
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonContent = "{\"id\":2,\"name\":\"Name\",\"description\":\"Desc\",\"available\":false,\"requestId\":456}";

        ItemDto dto = json.parseObject(jsonContent);

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getName()).isEqualTo("Name");
        assertThat(dto.getDescription()).isEqualTo("Desc");
        assertThat(dto.getAvailable()).isFalse();
        assertThat(dto.getRequestId()).isEqualTo(456);
    }
}
