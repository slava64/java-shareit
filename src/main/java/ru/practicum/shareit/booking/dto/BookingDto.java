package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class BookingDto {
    private Long id;
    private String start;
    private String end;
    private Item item;
    private User booker;
    private Booking.BookingStatus status;
}
