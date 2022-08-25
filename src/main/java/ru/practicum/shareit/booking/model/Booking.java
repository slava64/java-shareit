package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Data
public class Booking {
    public enum BookingStatus {
        WAITING, APPROVED, REJECTED, CANCELED;
    }

    private Long id;
    private String start;
    private String end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
