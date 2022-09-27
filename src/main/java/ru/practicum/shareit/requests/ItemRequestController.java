package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserController;

import java.util.Collection;

@RestController
@RequestMapping("/requests")
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {
    @Autowired
    private final ItemRequestService itemRequestService;

    @GetMapping
    public Collection<ItemRequestDto> findAllByUser(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId
    ) {
        log.info("Find all items");
        return itemRequestService.findAllByUser(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto findOneByUser(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long itemId
    ) {
        log.info("Find item {}", itemId);
        return itemRequestService.findOneByUser(userId, itemId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> findAllByPagination(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "20") Integer size
    ) {
        log.info("Find all items from {} size {} for user {}", from, size, userId);
        return itemRequestService.findAllByUser(userId, from, size);
    }

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestBody ItemRequestPostDto itemRequestPostDto
    ) {
        log.info("Create request {} for user {}", itemRequestPostDto.toString(), userId);
        return itemRequestService.add(userId, itemRequestPostDto);
    }
}

