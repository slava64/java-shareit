package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingItemDto;
import ru.practicum.shareit.requests.ItemRequest;
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
                item.getRequest() != null ? item.getRequest().getId() : null,
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
                item.getRequest() != null ? item.getRequest().getId() : null,
                item.getComments() != null ? CommentMapper.commentDto(item.getComments()) : null,
                lastBooking,
                nextBooking
        );
    }

    public static ItemWithItemRequestDto toItemWithItemRequestDto(Item item) {
        return new ItemWithItemRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Collection<ItemWithItemRequestDto> toItemWithItemRequestDto(Collection<Item> items) {
        Collection<ItemWithItemRequestDto> itemsWithItemRequestDto = new ArrayList<>();
        for (Item item : items) {
            itemsWithItemRequestDto.add(toItemWithItemRequestDto(item));
        }
        return itemsWithItemRequestDto;
    }

    public static Collection<ItemDto> toItemDto(Collection<Item> items) {
        Collection<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(toItemDto(item));
        }
        return itemsDto;
    }

    public static Item toItem(ItemDto itemDto, ItemRequest request) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setRequest(request);
        return item;
    }
}
