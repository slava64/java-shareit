package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Collection<ItemRequestDto> findAllByUser(Long userId) {
        findUser(userId);
        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);

        return ItemRequestMapper.toItemRequestDto(itemRequests);
    }

    @Override
    public Collection<ItemRequestDto> findAllByUser(Long userId, Integer from, Integer size) {
        findUser(userId);
        Pageable pageable = PageRequest.of((int) from / size, size, Sort.by("created").descending());
        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNot(userId, pageable);

        return ItemRequestMapper.toItemRequestDto(itemRequests);
    }

    @Override
    public ItemRequestDto findOneByUser(Long userId, Long id) {
        findUser(userId);
        ItemRequest itemRequest = findItemRequest(id);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDto add(Long userId, ItemRequestPostDto itemRequestPostDto) {
        User user = findUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestPostDto, user);
        ItemRequest saveItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(saveItemRequest);
    }

    private ItemRequest findItemRequest(Long id) {
        return itemRequestRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Запрос %d не найден", id)));
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь %d не найден", id)));
    }
}
