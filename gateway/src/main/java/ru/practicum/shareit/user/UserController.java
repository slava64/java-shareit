package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;
    public static final String HTTP_USER_ID_HEADER = "X-Sharer-User-Id";
    public static final Pattern pattern = Pattern.compile("^(.+)@([^@]+[^.])$");

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Find all Users");
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable("id") Long userId) {
        log.info("Find user {}", userId);
        return userClient.findOne(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody UserPostDto userRequestDto) {
        validateEmail(userRequestDto.getEmail());
        log.info("Create user {}", userRequestDto.toString());
        return userClient.add(userRequestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Long userId, @RequestBody UserPostDto userRequestDto) {
        if (userRequestDto.getEmail() != null) {
            validateEmail(userRequestDto.getEmail());
        }
        log.info("Update user {} - {}", userId, userRequestDto.toString());
        return userClient.update(userId, userRequestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long userId) {
        log.info("Delete user {}", userId);
        return userClient.delete(userId);
    }

    private void validateEmail(String email) {
        if (email == null || email.isEmpty() || email.isBlank()) {
            throw new BadRequestException("Поле Email не может быть пустым");
        }
        Matcher matcher = UserController.pattern.matcher(email);
        if (!matcher.find()) {
            throw new BadRequestException("Не верный формат email");
        }
    }
}
