package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingItemDto;

import java.io.Serializable;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemWithBookingDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private Collection<CommentDto> comments;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
}
