package ru.practicum.shareit.requests;

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
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemRequestServiceImpl(UserRepository userRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public Collection<ItemRequestDto> findAllByUser(Long userId) {
        findUser(userId);
        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);

        return ItemRequestMapper.toItemRequestDto(itemRequests);
    }

    @Override
    public Collection<ItemRequestDto> findAllByUser(Long userId, Integer from, Integer size) {
        findUser(userId);
        if (from < 0) {
            throw new BadRequestException("Параметр from не должен быть отрицательным");
        }
        if (size <= 0) {
            throw new BadRequestException("Параметр size должен быть больше нуля");
        }
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
        validationItemRequest(itemRequestPostDto);
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

    private void validationItemRequest(ItemRequestPostDto itemRequestPostDto) {
        if (itemRequestPostDto.getDescription() == null
                || itemRequestPostDto.getDescription().isEmpty()
                || itemRequestPostDto.getDescription().isBlank()
                ) {
            throw new BadRequestException("Поле description не может быть пусты");
        }
    }
}
