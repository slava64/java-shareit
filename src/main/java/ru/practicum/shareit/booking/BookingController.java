package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserController;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public Collection<BookingDto> findAll(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingParamState state
    ) {
        log.info("Find all bookings for user {}", userId);
        return bookingService.findAll(userId, state);
    }

    @GetMapping ("/owner")
    public Collection<BookingDto> findAllByOwner(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingParamState state
    ) {
        log.info("Find all bookings for owner {}", userId);
        return bookingService.findAllByOwner(userId, state);
    }

    @GetMapping("/{id}")
    public BookingDto findOne(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("id") Long bookingId
    ) {
        log.info("Find booking {} for user {}", bookingId, userId);
        return bookingService.findOne(userId, bookingId);
    }

    @PostMapping
    public BookingDto create(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @RequestBody BookingPostDto bookingPostDto
    ) {
        log.info("Create booking {}", bookingPostDto.toString());
        return bookingService.create(userId, bookingPostDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(
            @RequestHeader(UserController.HTTP_USER_ID_HEADER) Long userId,
            @PathVariable("bookingId") Long bookingId,
            @RequestParam Boolean approved
    ) {
        log.info("Update booking state for user - {}, booking - {}, approved - {}", userId, bookingId, approved);
        return bookingService.setApproved(userId, bookingId, approved);
    }
}
