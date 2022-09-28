package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.UserController;

import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> findAll(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingParamState state,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "20") Integer size
    ) {
        if (from < 0) {
            throw new BadRequestException("Параметр from не должен быть отрицательным");
        }
        if (size <= 0) {
            throw new BadRequestException("Параметр size должен быть больше нуля");
        }
        log.info("Find all bookings for user {}", userId);
        return bookingClient.findAll(userId, state, from, size);
    }

    @GetMapping ("/owner")
    public ResponseEntity<Object> findAllByOwner(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingParamState state,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "20") Integer size
    ) {
        if (from < 0) {
            throw new BadRequestException("Параметр from не должен быть отрицательным");
        }
        if (size <= 0) {
            throw new BadRequestException("Параметр size должен быть больше нуля");
        }
        log.info("Find all bookings for owner {}", userId);
        return bookingClient.findAllByOwner(userId, state, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long bookingId
    ) {
        log.info("Find booking {} for user {}", bookingId, userId);
        return bookingClient.findOne(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestBody BookingPostDto bookingPostDto
    ) {
        validationBooking(bookingPostDto);
        log.info("Create booking {}", bookingPostDto.toString());
        return bookingClient.create(userId, bookingPostDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("bookingId") Long bookingId,
            @RequestParam Boolean approved
    ) {
        log.info("Update booking state for user - {}, booking - {}, approved - {}", userId, bookingId, approved);
        return bookingClient.setApproved(userId, bookingId, approved);
    }

    private void validationBooking(BookingPostDto bookingPostDto) {
        if (bookingPostDto.getStart().isAfter(bookingPostDto.getEnd())) {
            throw new BadRequestException(String.format("Время start %s больше end", bookingPostDto.getStart()));
        }
        if (bookingPostDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(String.format("Время start %s в прошлом", bookingPostDto.getStart()));
        }
        if (bookingPostDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(String.format("Время end %s в прошлом", bookingPostDto.getEnd()));
        }
    }
}
