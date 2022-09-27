package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemWithItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private ItemWithItemRequestDto itemWithItemRequestDto = new ItemWithItemRequestDto(
            1L,
            "name item",
            "desription item",
            true,
            1L
    );

    private ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L,
            "Description",
            LocalDateTime.now(),
            List.of(itemWithItemRequestDto)
    );

    @Test
    void testFindAllByUser() throws Exception {
        when(itemRequestService.findAllByUser(any()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created").exists())
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemWithItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemWithItemRequestDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemWithItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemWithItemRequestDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemWithItemRequestDto.getRequestId()), Long.class));
    }

    @Test
    void testFindOneByUser() throws Exception {
        when(itemRequestService.findOneByUser(any(), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(itemWithItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemWithItemRequestDto.getName())))
                .andExpect(jsonPath("$.items[0].description", is(itemWithItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemWithItemRequestDto.getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(itemWithItemRequestDto.getRequestId()), Long.class));
    }

    @Test
    void testFindAllByPagination() throws Exception {
        when(itemRequestService.findAllByUser(any(), any(), any()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all?from=0&size=20").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created").exists())
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemWithItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemWithItemRequestDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemWithItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemWithItemRequestDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemWithItemRequestDto.getRequestId()), Long.class));
    }

    @Test
    void testCreate() throws Exception {
        when(itemRequestService.add(any(), any()))
                .thenReturn(itemRequestDto);

        ItemRequestPostDto itemRequestPostDto = new ItemRequestPostDto("Description");

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestPostDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(itemWithItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemWithItemRequestDto.getName())))
                .andExpect(jsonPath("$.items[0].description", is(itemWithItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemWithItemRequestDto.getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(itemWithItemRequestDto.getRequestId()), Long.class));
    }


}
