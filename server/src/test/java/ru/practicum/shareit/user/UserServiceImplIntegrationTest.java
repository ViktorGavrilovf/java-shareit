package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.practicum.shareit.dto.user.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserServiceImplIntegrationTest {
    @Autowired
    private UserRepository repository;

    @Autowired
    private UserService service;

    @Test
    void createAndDeleteUser_shouldCreateAndDeleteUser() {
        UserDto dto = new UserDto(null, "Test User", "test@example.com");
        UserDto saved = service.createUser(dto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@example.com");

        User userFromDb = repository.findById(saved.getId()).orElseThrow();
        assertThat(userFromDb.getName()).isEqualTo("Test User");

        service.deleteUser(saved.getId());
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void getAll_shouldReturnAllUsers() {
        service.createUser(new UserDto(null, "Alice", "a@mail.com"));
        service.createUser(new UserDto(null, "Bob", "b@mail.com"));

        List<UserDto> users = service.getAllUsers();

        assertThat(users).hasSize(2);
    }

    @Test
    void updateUser_shouldModifyFields() {
        UserDto userDto = service.createUser(new UserDto(null, "Old", "old@mail.com"));

        UserDto updatedUser = new UserDto(null, "New", "new@mail.com");
        UserDto result = service.updateUser(userDto.getId(), updatedUser);

        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getEmail()).isEqualTo("new@mail.com");
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService service(UserRepository repository) {
            return new UserServiceImpl(repository);
        }
    }
}
