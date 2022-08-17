package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingItemDto;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Service
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(
            UserRepository userRepository,
            ItemRepository itemRepository,
            CommentRepository commentRepository,
            BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Collection<ItemWithBookingDto> findAllByUser(Long userId) {
        Collection<Item> items = itemRepository.findAllByOwnerIdOrderById(userId);
        Collection<ItemWithBookingDto> itemsDto = new ArrayList<>();
        for (Item item: items) {
            itemsDto.add(getItemWithBookingDto(userId, item));
        }
        return itemsDto;
    }

    @Override
    public ItemWithBookingDto findOneByUser(Long userId, Long itemId) {
        findUser(userId);
        Item item = findItem(itemId);
        return getItemWithBookingDto(userId, item);
    }

    // Возвращает ItemWithBookingDto
    private ItemWithBookingDto getItemWithBookingDto(Long userId, Item item) {
        BookingItemDto lastBookingDto = null;
        BookingItemDto nextBookingDto = null;
        if (userId == item.getOwner().getId()) {
            Booking lastBooking = bookingRepository.
                    findFirstByItemIdAndEndIsBeforeOrderByStartDesc(item.getId(), LocalDateTime.now()).orElse(null);
            Booking nextBooking = bookingRepository.
                    findFirstByItemIdAndStartIsAfterOrderByStart(item.getId(), LocalDateTime.now()).orElse(null);
            lastBookingDto = lastBooking != null ? BookingMapper.bookingItemDto(lastBooking) : null;
            nextBookingDto = nextBooking != null ? BookingMapper.bookingItemDto(nextBooking) : null;
        }
        return ItemMapper.toItemWithBookingDto(item, lastBookingDto, nextBookingDto);
    }

    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        validationItem(itemDto);
        User user = findUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        Item saveItem = itemRepository.save(item);
        return ItemMapper.toItemDto(saveItem);
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentPostDto commentPostDto) {
        validationComment(commentPostDto);
        User user = findUser(userId);
        Item item = findItem(itemId);
        if(bookingRepository.countByBookerIdAndItemIdAndStatusAndEndIsBefore(
                userId,
                itemId,
                Booking.BookingStatus.APPROVED,
                LocalDateTime.now()
        ) == 0) {
            throw new BadRequestException(
                    String.format(
                            "Пользователь %d не арендовал вещь %d или же не истекло время аренды",
                            userId,
                            itemId
                    ));
        }
        Comment comment = CommentMapper.toComment(commentPostDto, user, item);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
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
        if (text.isEmpty())
            return new ArrayList<>();
        User user = findUser(userId);
        Collection<Item> items = itemRepository.search(text);
        return ItemMapper.toItemDto(items);
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
        if (itemDto.getName() == null
                || itemDto.getName().isEmpty()
                || itemDto.getName().isBlank()) {
            throw new BadRequestException("Поле Name не может быть пустым");
        }
        if (itemDto.getDescription() == null
                || itemDto.getDescription().isEmpty()
                || itemDto.getDescription().isBlank()
        ) {
            throw new BadRequestException("Поле Description не может быть пустым");
        }
    }

    private void validationComment(CommentPostDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new BadRequestException("Поле Text не может быть пустым");
        }
    }
}
