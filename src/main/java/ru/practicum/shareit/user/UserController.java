package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    public static final String HTTP_USER_ID_HEADER = "X-Sharer-User-Id";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        log.info("Find all Users");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findOne(@PathVariable("id") Long userId) {
        log.info("Find user {}", userId);
        return userService.findOne(userId);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Create user {}", userDto.toString());
        return userService.add(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long userId, @RequestBody UserDto userDto) {
        log.info("Update user {} - {}", userId, userDto.toString());
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long userId) {
        log.info("Delete user {}", userId);
        userService.delete(userId);
    }
}
