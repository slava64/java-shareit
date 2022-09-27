package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemService {
    Collection<ItemWithBookingDto> findAllByUser(Long userId);
    ItemWithBookingDto findOneByUser(Long userId, Long id);
    ItemDto add(Long userId, ItemDto itemPostDto);
    CommentDto createComment(Long userId, Long itemId, CommentPostDto commentDto);
    ItemDto update(Long userId, Long id, ItemDto itemPostDto);
    Boolean delete(Long userId, Long id);
    Collection<ItemDto> search(Long userId, String text);
}
