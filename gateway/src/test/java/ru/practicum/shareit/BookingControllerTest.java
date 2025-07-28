package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.dto.booking.BookingCreateDto;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateBooking() throws Exception {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).createBooking(eq(1L), any(BookingCreateDto.class));
    }

    @Test
    void testApproveBooking() throws Exception {
        mockMvc.perform(patch("/bookings/5")
                        .header("X-Sharer-User-Id", 10L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).approveBooking(10L, 5L, true);
    }

    @Test
    void testGetBooking() throws Exception {
        mockMvc.perform(get("/bookings/99")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBooking(3L, 99L);
    }

    @Test
    void testGetBookingsForUser() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForUser(1L, "ALL");
    }

    @Test
    void testGetBookingsForUserWithState() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "PAST"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForUser(1L, "PAST");
    }

    @Test
    void testGetBookingsForOwnerDefaultState() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForOwner(2L, "ALL");
    }

    @Test
    void testGetBookingsForOwnerWithState() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookingsForOwner(2L, "FUTURE");
    }
}
