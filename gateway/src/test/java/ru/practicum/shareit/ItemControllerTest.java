package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.dto.items.CommentDto;
import ru.practicum.shareit.dto.items.ItemDto;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ItemClient itemClient;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testAddItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Power Drill")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).createItem(anyLong(), any(ItemDto.class));
    }

    @Test
    void testAddItemWithBlankName() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("")
                .description("Power Drill")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, times(0)).createItem(anyLong(), any(ItemDto.class));
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("Updated")
                .description("Updated desc")
                .available(true)
                .build();

        mockMvc.perform(patch("/items/5")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
        verify(itemClient, times(1)).updateItem(eq(1L), eq(5L), any(ItemDto.class));
    }

    @Test
    void testUpdateItemWithInvalidBody() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("")
                .available(null)
                .build();

        mockMvc.perform(patch("/items/5")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).updateItem(eq(1L), eq(5L), any(ItemDto.class));
    }


    @Test
    void testGetItem() throws Exception {
        mockMvc.perform(get("/items/1")
                .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getItem(eq(2L), eq(1L));
    }

    @Test
    void testGetUserItems() throws Exception {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getUserItems(eq(1L));
    }

    @Test
    void testSearchItems() throws Exception {
        mockMvc.perform(get("/items/search")
                .header("X-Sharer-User-Id", 1L)
                .param("text", "drill"))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).search("drill", 1L);
    }

    @Test
    void testAddComment() throws Exception {
        CommentDto commentDto = CommentDto.builder().text("Nice").build();

        mockMvc.perform(post("/items/5/comment")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).addComment(eq(1L), eq(5L), any(CommentDto.class));
    }

    @Test
    void testAddCommentWithBlankText() throws Exception {
        CommentDto commentDto = CommentDto.builder().text("").build();

        mockMvc.perform(post("/items/5/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, times(0)).addComment(anyLong(), anyLong(), any(CommentDto.class));
    }

    @Test
    void testSearchWithoutText() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());

        verify(itemClient, times(0)).search(anyString(), anyLong());
    }

}
