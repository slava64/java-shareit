package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Collection<ItemDto> findAllByUser(Long userId) {
        Collection<Item> items = itemRepository.findAllByOwnerId(userId);
        return ItemMapper.toItemDto(items);
    }

    @Override
    public ItemDto findOneByUser(Long userId, Long id) {
        findUser(id);
        Item item = findItem(id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        validationItem(itemDto);
        itemDto.setOwner(findUser(userId));
        Item item = itemRepository.save(ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long id, ItemDto itemDto) {
        Item item = findItem(id);
        if (!item.getOwner().equals(findUser(userId))) {
            throw new NotFoundException("У пользователя нет такой вещи");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public Boolean delete(Long userId, Long id) {
        findUser(id);
        findItem(id);
        itemRepository.deleteById(id);
        return true;
    }

    @Override
    public Collection<ItemDto> search(Long userId, String text) {
        //User user = findUser(userId);
        //Collection<Item> items = itemRepository.search(text);
        //return ItemMapper.toItemDto(items);
        return new ArrayList<>();
    }

    private Item findItem(Long id) {
        return itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Вещь %d не найден", id)));
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь %d не найден", id)));
    }

    private void validationItem(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new BadRequestException("Поле Available не может быть пустым");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty() || itemDto.getName().isBlank()) {
            throw new BadRequestException("Поле Name не может быть пустым");
        }
        if (itemDto.getDescription() == null
                || itemDto.getDescription().isEmpty()
                || itemDto.getDescription().isBlank()
        ) {
            throw new BadRequestException("Поле Description не может быть пустым");
        }
    }
}
