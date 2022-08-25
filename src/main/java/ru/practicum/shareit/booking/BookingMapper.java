package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.Collection;

public final class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem() != null ? ItemMapper.toItemDto(booking.getItem()) : null,
                booking.getBooker() != null ? UserMapper.toUserDto(booking.getBooker()) : null,
                booking.getStatus() != null ? booking.getStatus() : null
        );
    }

    public static Collection<BookingDto> toBookingDto(Collection<Booking> bookings) {
        Collection<BookingDto> bookingsDto = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingsDto.add(toBookingDto(booking));
        }
        return bookingsDto;
    }

    public static BookingItemDto bookingItemDto(Booking booking) {
        return new BookingItemDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker() != null ? booking.getBooker().getId() : null,
                booking.getStatus() != null ? booking.getStatus() : null
        );
    }

    public static Booking toNewBooking(BookingPostDto bookingPostDto, User user, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingPostDto.getStart());
        booking.setEnd(bookingPostDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }
}
