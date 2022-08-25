package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingItemDto;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.Collection;

public final class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? UserMapper.toUserDto(item.getOwner()) : null,
                item.getRequest() != null ? item.getRequest() : null,
                item.getComments() != null ? CommentMapper.commentDto(item.getComments()) : null

        );
    }

    public static ItemWithBookingDto toItemWithBookingDto(
            Item item,
            BookingItemDto lastBooking,
            BookingItemDto nextBooking
    ) {
        return new ItemWithBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner() : null,
                item.getRequest() != null ? item.getRequest() : null,
                item.getComments() != null ? CommentMapper.commentDto(item.getComments()) : null,
                lastBooking,
                nextBooking
        );
    }

    public static Collection<ItemDto> toItemDto(Collection<Item> items) {
        Collection<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(toItemDto(item));
        }
        return itemsDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}
