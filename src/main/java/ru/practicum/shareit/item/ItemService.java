package ru.practicum.shareit.item;

import java.util.Collection;

interface ItemService {
    public Collection<ItemDto> findAllByUser(Long userId);
    public ItemDto findOneByUser(Long userId, Long id);
    public ItemDto add(Long userId, ItemDto itemDto);
    public ItemDto update(Long userId, Long id, ItemDto itemDto);
    public Boolean delete(Long userId, Long id);
    public Collection<ItemDto> search(Long userId, String text);
}
