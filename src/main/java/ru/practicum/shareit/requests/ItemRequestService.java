package ru.practicum.shareit.requests;

import java.util.Collection;

public interface ItemRequestService {
    Collection<ItemRequestDto> findAllByUser(Long userId);
    Collection<ItemRequestDto> findAllByUser(Long userId, Integer from, Integer size);
    ItemRequestDto findOneByUser(Long userId, Long id);
    ItemRequestDto add(Long userId, ItemRequestPostDto itemRequestDto);
}
