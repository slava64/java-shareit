package ru.practicum.shareit.requests;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;

public final class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestPostDto itemRequestPostDto, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestPostDto.getDescription());
        itemRequest.setRequestor(requestor);
        return itemRequest;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRequest.getItems() != null ? ItemMapper.toItemWithItemRequestDto(itemRequest.getItems()) : null
                );
    }

    public static Collection<ItemRequestDto> toItemRequestDto(Collection<ItemRequest> itemRequests) {
        Collection<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for(ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(ItemRequestMapper.toItemRequestDto(itemRequest));
        }
        return itemRequestDtos;
    }
}
