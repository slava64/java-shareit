package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Collection<ItemDto> findAllByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Find all items");
        return itemService.findAllByUser(userId);
    }

    @GetMapping("/{id}")
    public ItemDto findOneByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("id") Long itemId
    ) {
        log.info("Find item {}", itemId);
        return itemService.findOneByUser(userId, itemId);
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("Create item {} for user {}", itemDto.toString(), userId);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("id") Long itemId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("Update item {} - {} for user {}", itemId, itemDto.toString(), userId);
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long itemId) {
        log.info("Delete item {}", itemId);
        itemService.delete(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam("text") String text) {
        log.info("Search items by '{}'", text);
        return itemService.search(userId, text);
    }
}
