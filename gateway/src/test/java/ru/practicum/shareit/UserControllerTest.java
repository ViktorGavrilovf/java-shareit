package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testCreateUser() throws Exception {
        UserDto dto = new UserDto(null, "Test", "test@mail.com");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userClient, times(1)).createUser(Mockito.any(UserDto.class));
    }

    @Test
    void testInvalidEmail() throws Exception {
        UserDto dto = new UserDto(null, "Test", "not-an-email");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(userClient, times(0)).createUser(Mockito.any(UserDto.class));
    }

    @Test
    void testGetUser() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());

        verify(userClient, times(1)).getUser(1L);
    }

    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
        verify(userClient, times(1)).getUsers();
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto dto = new UserDto(null, "Updated", "updated@mail.com");

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userClient, times(1)).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userClient, times(1)).deleteUser(1L);
    }
}
