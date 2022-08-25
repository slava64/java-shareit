package ru.practicum.shareit.booking;

import java.util.Collection;

public interface BookingService {
    Collection<BookingDto> findAll(Long userId, BookingParamState state);
    Collection<BookingDto> findAllByOwner(Long userId, BookingParamState state);
    BookingDto findOne(Long userId, Long id);
    BookingDto create(Long userId, BookingPostDto bookingPostDto);
    BookingDto setApproved(Long userId, Long bookingId, Boolean isApproved);
}
