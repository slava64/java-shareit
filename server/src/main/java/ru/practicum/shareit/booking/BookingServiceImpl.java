package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRepository itemRepository;

    @Override
    public Collection<BookingDto> findAll(Long userId, BookingParamState state, Integer from, Integer size) {
        findUser(userId);
        List<Booking> bookings;
        switch (state)
        {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                        userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(
                        userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED);
                break;
            case ALL:
            default:
                Pageable pageable = PageRequest.of((int) from / size, size);
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
                break;
        }
        return BookingMapper.toBookingDto(bookings);
    }

    @Override
    public Collection<BookingDto> findAllByOwner(Long userId, BookingParamState state, Integer from, Integer size) {
        findUser(userId);
        List<Booking> bookings;
        switch (state)
        {
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerInCurrent(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerInPast(userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerInFuture(userId);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerByStatus(userId, BookingStatus.WAITING.toString());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerByStatus(userId, BookingStatus.REJECTED.toString());
                break;
            case ALL:
            default:
                Pageable pageable = PageRequest.of((int) from / size, size);
                bookings = bookingRepository.findAllByOwner(userId, pageable);
                break;
        }
        return BookingMapper.toBookingDto(bookings);
    }

    @Override
    public BookingDto findOne(Long userId, Long id) {
        findUser(userId);
        Booking booking = findBooking(id);
        if (!checkIsBooker(userId, booking) && !checkIsOwner(userId, booking)) {
            throw new NotFoundException(String.format("Вы не являетесь автором бронирования или владельцем вещи"));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto create(Long userId, BookingPostDto bookingPostDto) {
        User user = findUser(userId);
        Item item = findItem(bookingPostDto.getItemId());
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException(String.format("Собственник не может бронировать свою вещь"));
        }
        if(!item.getAvailable()) {
            throw new BadRequestException(String.format("Вещь %d не доступна", bookingPostDto.getItemId()));
        }
        Booking booking = bookingRepository.save(BookingMapper.toNewBooking(bookingPostDto, user, item));
        return BookingMapper.toBookingDto(booking);
    }

    public BookingDto setApproved(Long userId, Long bookingId, Boolean isApproved) {
        findUser(userId);
        Booking booking = findBooking(bookingId);
        if (!checkIsOwner(userId, booking)) {
            throw new NotFoundException(
                    String.format("Пользователь %d не является владельцем вещи %d", userId, bookingId)
            );
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED) ||
                booking.getStatus().equals(BookingStatus.REJECTED)) {
            throw new BadRequestException(String.format("Вещь %d уже проверена", bookingId));
        }
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь %d не найден", id)));
    }

    private Item findItem(Long id) {
        return itemRepository.findById(id).orElseThrow(
                    () -> new NotFoundException(String.format("Вещь %d не найдена", id)));
    }

    private Booking findBooking(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Бронирование %d не найдено", id)));
    }

    private boolean checkIsOwner(Long userId, Booking booking) {
        if (userId == booking.getItem().getOwner().getId()) {
            return true;
        }
        return false;
    }

    private boolean checkIsBooker(Long userId, Booking booking) {
        if (userId == booking.getBooker().getId()) {
            return true;
        }
        return false;
    }
}
