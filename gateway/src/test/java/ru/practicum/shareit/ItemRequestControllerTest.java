package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient client;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddRequest_Valid() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description("Нужен пылесос")
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(client, times(1)).addRequest(eq(1L), any(ItemRequestDto.class));
    }

    @Test
    void testAddRequest_BlankDescription() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description("")
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(client, times(0)).addRequest(anyLong(), any());
    }

    @Test
    void testGetOwnRequests() throws Exception {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(client, times(1)).getOwnRequests(1L);
    }

    @Test
    void testGetAllRequests() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk());

        verify(client, times(1)).getAllRequests(2L);
    }

    @Test
    void testGetRequestById() throws Exception {
        mockMvc.perform(get("/requests/77")
                        .header("X-Sharer-User-Id", 5L))
                .andExpect(status().isOk());

        verify(client, times(1)).getRequestById(5L, 77L);
    }
}
