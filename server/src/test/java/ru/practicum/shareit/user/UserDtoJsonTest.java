package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.dto.user.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    void testSerialize() throws IOException {
        UserDto userDto = new UserDto(1L, "Test", "test@mail.com");

        String json = jacksonTester.write(userDto).getJson();

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test\"");
        assertThat(json).contains("\"email\":\"test@mail.com\"");
    }

    @Test
    void testDeserialize() throws IOException {
        String json = "{\"id\":5,\"name\":\"Alex\",\"email\":\"alex@example.com\"}";

        UserDto userDto = jacksonTester.parseObject(json);

        assertThat(userDto.getId()).isEqualTo(5L);
        assertThat(userDto.getName()).isEqualTo("Alex");
        assertThat(userDto.getEmail()).isEqualTo("alex@example.com");
    }
}
