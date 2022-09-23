package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto = new UserDto(
            1L,
            "John",
            "john.doe@mail.com"
    );

    private ItemDto itemDto = new ItemDto(
            1L,
            "name item",
            "desription item",
            true,
            1L,
            null
    );

    private BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            itemDto,
            userDto,
            BookingStatus.WAITING
    );

    @Test
    void testFindAll() throws Exception {
        when(bookingService.findAll(any(), any(), any(), any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(itemDto.getName())));
    }

    @Test
    void testFindAllByOwner() throws Exception {
        when(bookingService.findAllByOwner(any(), any(), any(), any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start").exists())
                .andExpect(jsonPath("$[0].end").exists())
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(itemDto.getName())));
    }

    @Test
    void testFindOne() throws Exception {
        when(bookingService.findOne(any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())));
    }

    @Test
    void testCreate() throws Exception {
        when(bookingService.create(any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())));
    }

    @Test
    void testUpdate() throws Exception {
        when(bookingService.setApproved(any(), any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1?approved=false")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start").exists())
                .andExpect(jsonPath("$.end").exists())
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(itemDto.getName())));
    }
}
