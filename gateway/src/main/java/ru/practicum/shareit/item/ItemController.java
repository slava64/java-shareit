package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.UserController;

@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAllByUser(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId
    ) {
        log.info("Find all items");
        return itemClient.findAllByUser(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOneByUser(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long itemId
    ) {
        log.info("Find item {}", itemId);
        return itemClient.findOneByUser(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestBody ItemPostDto itemRequestDto
    ) {
        validationItem(itemRequestDto);
        log.info("Create item {} for user {}", itemRequestDto.toString(), userId);
        return itemClient.add(userId, itemRequestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long itemId,
            @RequestBody ItemPostDto itemRequestDto
    ) {
        log.info("Update item {} - {} for user {}", itemId, itemRequestDto.toString(), userId);
        return itemClient.update(userId, itemId, itemRequestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long itemId) {
        log.info("Delete item {}", itemId);
        return itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestParam("text") String text) {
        log.info("Search items by '{}'", text);
        return itemClient.search(userId, text);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long itemId,
            @RequestBody CommentRequestDto commentRequestDto) {
        validationComment(commentRequestDto);
        log.info("Create comment {} for user {}, for item {}", commentRequestDto.toString(), userId, itemId);
        return itemClient.createComment(userId, itemId, commentRequestDto);
    }

    private void validationItem(ItemPostDto itemRequestDto) {
        if (itemRequestDto.getAvailable() == null) {
            throw new BadRequestException("Поле Available не может быть пустым");
        }
        if (itemRequestDto.getName() == null
                || itemRequestDto.getName().isEmpty()
                || itemRequestDto.getName().isBlank()) {
            throw new BadRequestException("Поле Name не может быть пустым");
        }
        if (itemRequestDto.getDescription() == null
                || itemRequestDto.getDescription().isEmpty()
                || itemRequestDto.getDescription().isBlank()
        ) {
            throw new BadRequestException("Поле Description не может быть пустым");
        }
    }

    private void validationComment(CommentRequestDto commentRequestDto) {
        if (commentRequestDto.getText() == null
                || commentRequestDto.getText().isEmpty()
                || commentRequestDto.getText().isBlank()
        ) {
            throw new BadRequestException("Поле Text не может быть пустым");
        }
    }
}
