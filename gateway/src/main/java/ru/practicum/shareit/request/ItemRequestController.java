package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.UserController;

@Controller
@RequestMapping("/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> findAllByUser(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId
    ) {
        log.info("Find all items");
        return itemRequestClient.findAllByUser(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOneByUser(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long itemId
    ) {
        log.info("Find item {}", itemId);
        return itemRequestClient.findOneByUser(userId, itemId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllByPagination(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "20") Integer size
    ) {
        if (from < 0) {
            throw new BadRequestException("Параметр from не должен быть отрицательным");
        }
        if (size <= 0) {
            throw new BadRequestException("Параметр size должен быть больше нуля");
        }
        log.info("Find all items from {} size {} for user {}", from, size, userId);
        return itemRequestClient.findAllByUser(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestBody ItemRequestPostDto itemRequestPostDto
    ) {
        validationItemRequest(itemRequestPostDto);
        log.info("Create request {} for user {}", itemRequestPostDto.toString(), userId);
        return itemRequestClient.add(userId, itemRequestPostDto);
    }

    private void validationItemRequest(ItemRequestPostDto itemRequestPostDto) {
        if (itemRequestPostDto.getDescription() == null
                || itemRequestPostDto.getDescription().isEmpty()
                || itemRequestPostDto.getDescription().isBlank()
        ) {
            throw new BadRequestException("Поле description не может быть пусты");
        }
    }
}

