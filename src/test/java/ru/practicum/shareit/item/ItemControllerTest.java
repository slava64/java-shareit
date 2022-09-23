package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.requests.ItemRequestDto;
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

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private CommentDto commentDto = new CommentDto(
            1L,
            "text",
            "autor",
            LocalDateTime.now()
    );

    private UserDto userDto = new UserDto(
            1L,
            "User name",
            "email@mail.ru"
    );

    private ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "Description",
            LocalDateTime.now(),
            List.of()
    );

    private ItemWithBookingDto itemWithBookingDto = new ItemWithBookingDto(
            1L,
            "name item",
            "desription item",
            true,
            1L,
            List.of(commentDto),
            null,
            null
    );

    private ItemDto itemDto = new ItemDto(
            1L,
            "name item",
            "desription item",
            true,
            1L,
            List.of(commentDto)
    );

    @Test
    void testFindAllByUser() throws Exception {
        when(itemService.findAllByUser(any()))
                .thenReturn(List.of(itemWithBookingDto));

        mvc.perform(get("/items").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemWithBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemWithBookingDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithBookingDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemWithBookingDto.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemWithBookingDto.getRequestId()), Long.class));
    }

    @Test
    void testFindAllByUserWithComments() throws Exception {
        when(itemService.findAllByUser(any()))
                .thenReturn(List.of(itemWithBookingDto));

        mvc.perform(get("/items").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemWithBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemWithBookingDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithBookingDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemWithBookingDto.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemWithBookingDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].comments", hasSize(1)))
                .andExpect(jsonPath("$[0].comments[0].id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].text", is(commentDto.getText())))
                .andExpect(jsonPath("$[0].comments[0].authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$[0].comments[0].created").exists());
    }

    @Test
    void testFindOneByUser() throws Exception {
        when(itemService.findOneByUser(any(), any()))
                .thenReturn(itemWithBookingDto);

        mvc.perform(get("/items/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithBookingDto.getName())))
                .andExpect(jsonPath("$.description", is(itemWithBookingDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithBookingDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemWithBookingDto.getRequestId()), Long.class));
    }

    @Test
    void testFindOneByUserWithComment() throws Exception {
        when(itemService.findOneByUser(any(), any()))
                .thenReturn(itemWithBookingDto);

        mvc.perform(get("/items/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithBookingDto.getName())))
                .andExpect(jsonPath("$.description", is(itemWithBookingDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithBookingDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemWithBookingDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text", is(commentDto.getText())))
                .andExpect(jsonPath("$.comments[0].authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.comments[0].created").exists());
    }

    @Test
    void testCreate() throws Exception {
        when(itemService.add(any(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void testUpdate() throws Exception {
        when(itemService.update(any(), any(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void testDelete() throws Exception {
        when(itemService.delete(any(), any()))
                .thenReturn(true);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testSearch() throws Exception {
        when(itemService.search(any(), any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=test").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void testCreateComment() throws Exception {
        when(itemService.createComment(any(), any(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.writeValueAsString(commentDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created").exists());
    }


}
