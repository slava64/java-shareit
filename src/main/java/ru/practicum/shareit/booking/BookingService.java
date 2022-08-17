package ru.practicum.shareit.booking;

import java.util.Collection;

interface BookingService {
    public Collection<BookingDto> findAll(Long userId, BookingParamState state);
    public Collection<BookingDto> findAllByOwner(Long userId, BookingParamState state);
    public BookingDto findOne(Long userId, Long id);
    public BookingDto create(Long userId, BookingPostDto bookingPostDto);
    public BookingDto setApproved(Long userId, Long bookingId, Boolean isApproved);
    public Boolean delete(Long id);
}
