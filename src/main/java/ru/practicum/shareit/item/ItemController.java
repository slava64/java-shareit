package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserController;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    @Autowired
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemWithBookingDto> findAllByUser(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId
    ) {
        log.info("Find all items");
        return itemService.findAllByUser(userId);
    }

    @GetMapping("/{id}")
    public ItemWithBookingDto findOneByUser(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long itemId
    ) {
        log.info("Find item {}", itemId);
        return itemService.findOneByUser(userId, itemId);
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("Create item {} for user {}", itemDto.toString(), userId);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long itemId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("Update item {} - {} for user {}", itemId, itemDto.toString(), userId);
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long itemId) {
        log.info("Delete item {}", itemId);
        itemService.delete(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestParam("text") String text) {
        log.info("Search items by '{}'", text);
        return itemService.search(userId, text);
    }

    @PostMapping("/{id}/comment")
    public CommentDto createComment(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long itemId,
            @RequestBody CommentPostDto commentDto) {
        log.info("Create comment {} for user {}, for item {}", commentDto.toString(), userId, itemId);
        return itemService.createComment(userId, itemId, commentDto);
    }


}
