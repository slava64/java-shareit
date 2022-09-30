package ru.practicum.shareit.booking;

import java.util.Collection;

public interface BookingService {
    Collection<BookingDto> findAll(Long userId, BookingParamState state, Integer from, Integer size);

    Collection<BookingDto> findAllByOwner(Long userId, BookingParamState state, Integer from, Integer size);

    BookingDto findOne(Long userId, Long id);

    BookingDto create(Long userId, BookingPostDto bookingPostDto);

    BookingDto setApproved(Long userId, Long bookingId, Boolean isApproved);
}
