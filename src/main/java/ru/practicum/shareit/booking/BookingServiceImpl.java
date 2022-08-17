package ru.practicum.shareit.booking;

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
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            ItemRepository itemRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Collection<BookingDto> findAll(Long userId, BookingParamState state) {
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
                        userId, Booking.BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, Booking.BookingStatus.REJECTED);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
        }
        return BookingMapper.toBookingDto(bookings);
    }

    @Override
    public Collection<BookingDto> findAllByOwner(Long userId, BookingParamState state) {
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
                bookings = bookingRepository.findAllByOwnerByStatus(userId, Booking.BookingStatus.WAITING.toString());
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerByStatus(userId, Booking.BookingStatus.REJECTED.toString());
                break;
            case ALL:
            default:
                bookings = bookingRepository.findAllByOwner(userId);
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
        validationBooking(bookingPostDto);
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
        if (booking.getStatus().equals(Booking.BookingStatus.APPROVED) ||
                booking.getStatus().equals(Booking.BookingStatus.REJECTED)) {
            throw new BadRequestException(String.format("Вещь %d уже проверена", bookingId));
        }
        if (isApproved) {
            booking.setStatus(Booking.BookingStatus.APPROVED);
        } else {
            booking.setStatus(Booking.BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public Boolean delete(Long id) {
        return null;
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

    private void validationBooking(BookingPostDto bookingPostDto) {
        Item item = findItem(bookingPostDto.getItemId());
        if(!item.getAvailable()) {
            throw new BadRequestException(String.format("Вещь %d не доступна", bookingPostDto.getItemId()));
        }
        if(bookingPostDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(String.format("Время start %s в прошлом", bookingPostDto.getStart()));
        }
        if(bookingPostDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(String.format("Время end %s в прошлом", bookingPostDto.getEnd()));
        }
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
