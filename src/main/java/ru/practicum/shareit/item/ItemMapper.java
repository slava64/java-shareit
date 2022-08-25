package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.ArrayList;
import java.util.Collection;

final class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner() : null,
                item.getRequest() != null ? item.getRequest() : null
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
        item.setOwner(itemDto.getOwner());
        item.setRequest(itemDto.getRequest());
        return item;
    }
}
