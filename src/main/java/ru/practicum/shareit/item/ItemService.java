package ru.practicum.shareit.item;

import java.util.Collection;

interface ItemService {
    public Collection<ItemWithBookingDto> findAllByUser(Long userId);
    public ItemWithBookingDto findOneByUser(Long userId, Long id);
    public ItemDto add(Long userId, ItemDto itemPostDto);
    public CommentDto createComment(Long userId, Long itemId, CommentPostDto commentDto);
    public ItemDto update(Long userId, Long id, ItemDto itemPostDto);
    public Boolean delete(Long userId, Long id);
    public Collection<ItemDto> search(Long userId, String text);
}
