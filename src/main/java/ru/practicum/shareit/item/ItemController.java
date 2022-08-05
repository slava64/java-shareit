package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    public static final String HTTP_USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Collection<ItemDto> findAllByUser(
            @RequestHeader(HTTP_USER_ID_HEADER) Long userId
    ) {
        log.info("Find all items");
        return itemService.findAllByUser(userId);
    }

    @GetMapping("/{id}")
    public ItemDto findOneByUser(
            @RequestHeader(HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long itemId
    ) {
        log.info("Find item {}", itemId);
        return itemService.findOneByUser(userId, itemId);
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader(HTTP_USER_ID_HEADER) Long userId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("Create item {} for user {}", itemDto.toString(), userId);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @RequestHeader(HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long itemId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("Update item {} - {} for user {}", itemId, itemDto.toString(), userId);
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @RequestHeader(HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long itemId) {
        log.info("Delete item {}", itemId);
        itemService.delete(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(
            @RequestHeader(HTTP_USER_ID_HEADER) Long userId,
            @RequestParam("text") String text) {
        log.info("Search items by '{}'", text);
        return itemService.search(userId, text);
    }
}
