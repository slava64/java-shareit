package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

public class BookingServiceImplTest {
    BookingService bookingService;

    UserRepository userRepository;
    ItemRepository itemRepository;
    BookingRepository bookingRepository;

    private final Item item = new Item();
    private final User user = new User();
    private final Booking booking = new Booking();

    public BookingServiceImplTest() {
        user.setId(1L);
        user.setName("petr");
        user.setEmail("petr@mail.com");

        item.setId(1L);
        item.setName("Name");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());

        userRepository = Mockito.mock(UserRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);

        bookingService = new BookingServiceImpl(
                bookingRepository,
                userRepository,
                itemRepository
        );
    }

    @Test
    void testFindAllWhereBookingParamStateIsAll() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByBookerIdOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> bookingDtoCollection = bookingService.findAll(
                1L,
                BookingParamState.ALL,
                0,
                20
        );

        assertThat(bookingDtoCollection).isNotNull();
        assertThat(bookingDtoCollection.size()).isEqualTo(1);
    }

    @Test
    void testFindAllWhereBookingParamStateIsCurrent() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(
                bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any())
        ).thenReturn(List.of(booking));

        Collection<BookingDto> bookingDtoCollection = bookingService.findAll(
                1L,
                BookingParamState.CURRENT,
                0,
                20
        );

        assertThat(bookingDtoCollection).isNotNull();
        assertThat(bookingDtoCollection.size()).isEqualTo(1);
    }

    @Test
    void testFindAllWhereBookingParamStateIsPast() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(
                bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(any(), any())
        ).thenReturn(List.of(booking));

        Collection<BookingDto> bookingDtoCollection = bookingService.findAll(
                1L,
                BookingParamState.PAST,
                0,
                20
        );

        assertThat(bookingDtoCollection).isNotNull();
        assertThat(bookingDtoCollection.size()).isEqualTo(1);
    }

    @Test
    void testFindAllWhereBookingParamStateIsFuture() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(
                bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(any(), any())
        ).thenReturn(List.of(booking));

        Collection<BookingDto> bookingDtoCollection = bookingService.findAll(
                1L,
                BookingParamState.FUTURE,
                0,
                20
        );

        assertThat(bookingDtoCollection).isNotNull();
        assertThat(bookingDtoCollection.size()).isEqualTo(1);
    }

    @Test
    void testFindAllWhereBookingParamStateIsWaiting() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(
                bookingRepository.findByBookerIdAndStatusOrderByStartDesc(any(), any())
        ).thenReturn(List.of(booking));

        Collection<BookingDto> bookingDtoCollection = bookingService.findAll(
                1L,
                BookingParamState.WAITING,
                0,
                20
        );

        assertThat(bookingDtoCollection).isNotNull();
        assertThat(bookingDtoCollection.size()).isEqualTo(1);
    }

    @Test
    void testFindAllWhereBookingParamStateIsRejected() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(
                bookingRepository.findByBookerIdAndStatusOrderByStartDesc(any(), any())
        ).thenReturn(List.of(booking));

        Collection<BookingDto> bookingDtoCollection = bookingService.findAll(
                1L,
                BookingParamState.REJECTED,
                0,
                20
        );

        assertThat(bookingDtoCollection).isNotNull();
        assertThat(bookingDtoCollection.size()).isEqualTo(1);
    }

    @Test
    void testFindAllWhereUserNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.findAll(
                1L,
                BookingParamState.REJECTED,
                0,
                20
        ))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testFindAllWhereFromLessZero() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> bookingService.findAll(
                1L,
                BookingParamState.REJECTED,
                -1,
                20
        ))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testFindAllWhereSizeIsZero() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> bookingService.findAll(
                1L,
                BookingParamState.REJECTED,
                0,
                0
        ))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testFindAllWhereSizeLessZero() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> bookingService.findAll(
                1L,
                BookingParamState.REJECTED,
                0,
                -1
        ))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testFindAllByOwnerWhereBookingParamStateIsAll() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findAllByOwner(any(), any())).thenReturn(List.of(booking));

        Collection<BookingDto> bookingDtoCollection = bookingService.findAllByOwner(
                1L,
                BookingParamState.ALL,
                0,
                20
        );

        assertThat(bookingDtoCollection).isNotNull();
        assertThat(bookingDtoCollection.size()).isEqualTo(1);
    }

    @Test
    void testFindAllByOwnerWhereBookingParamStateIsCurrent() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(
                bookingRepository.findAllByOwnerInCurrent(any())
        ).thenReturn(List.of(booking));

        Collection<BookingDto> bookingDtoCollection = bookingService.findAllByOwner(
                1L,
                BookingParamState.CURRENT,
                0,
                20
        );

        assertThat(bookingDtoCollection).isNotNull();
        assertThat(bookingDtoCollection.size()).isEqualTo(1);
    }

    @Test
    void testFindAllByOwnerWhereBookingParamStateIsPast() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(
                bookingRepository.findAllByOwnerInPast(any())
        ).thenReturn(List.of(booking));

        Collection<BookingDto> bookingDtoCollection = bookingService.findAllByOwner(
                1L,
                BookingParamState.PAST,
                0,
                20
        );

        assertThat(bookingDtoCollection).isNotNull();
        assertThat(bookingDtoCollection.size()).isEqualTo(1);
    }

    @Test
    void testFindAllByOwnerWhereBookingParamStateIsFuture() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(
                bookingRepository.findAllByOwnerInFuture(any())
        ).thenReturn(List.of(booking));

        Collection<BookingDto> bookingDtoCollection = bookingService.findAllByOwner(
                1L,
                BookingParamState.FUTURE,
                0,
                20
        );

        assertThat(bookingDtoCollection).isNotNull();
        assertThat(bookingDtoCollection.size()).isEqualTo(1);
    }

    @Test
    void testFindAllByOwnerWhereBookingParamStateIsWaiting() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(
                bookingRepository.findAllByOwnerByStatus(any(), any())
        ).thenReturn(List.of(booking));

        Collection<BookingDto> bookingDtoCollection = bookingService.findAllByOwner(
                1L,
                BookingParamState.WAITING,
                0,
                20
        );

        assertThat(bookingDtoCollection).isNotNull();
        assertThat(bookingDtoCollection.size()).isEqualTo(1);
    }

    @Test
    void testFindAllByOwnerWhereBookingParamStateIsRejected() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(
                bookingRepository.findAllByOwnerByStatus(any(), any())
        ).thenReturn(List.of(booking));

        Collection<BookingDto> bookingDtoCollection = bookingService.findAllByOwner(
                1L,
                BookingParamState.REJECTED,
                0,
                20
        );

        assertThat(bookingDtoCollection).isNotNull();
        assertThat(bookingDtoCollection.size()).isEqualTo(1);
    }

    @Test
    void testFindAllByOwnerWhereUserNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.findAllByOwner(
                1L,
                BookingParamState.REJECTED,
                0,
                20
        ))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testFindAllByOwnerWhereFromLessZero() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> bookingService.findAllByOwner(
                1L,
                BookingParamState.REJECTED,
                -1,
                20
        ))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testFindAllByOwnerWhereSizeIsZero() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> bookingService.findAllByOwner(
                1L,
                BookingParamState.REJECTED,
                0,
                0
        ))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testFindAllByOwnerWhereSizeLessZero() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> bookingService.findAllByOwner(
                1L,
                BookingParamState.REJECTED,
                0,
                -1
        ))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testFindOne() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.findOne(1L, 1L);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingDto.getItem()).isNotNull();
    }

    @Test
    void testFindOneWhereUserNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.findOne(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testFindOneWhereBookingNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.findOne(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testFindOneWhereIsNotOwner() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.findOne(2L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testCreate() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);

        BookingPostDto bookingPostDto = new BookingPostDto(
                1L,
                LocalDateTime.now().plus(10L, ChronoUnit.HOURS),
                LocalDateTime.now().plus(100L, ChronoUnit.HOURS)
        );
        BookingDto bookingDto = bookingService.create(2L, bookingPostDto);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingDto.getItem()).isNotNull();
    }

    @Test
    void testSetApproved() {
        booking.setStatus(BookingStatus.WAITING);

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);

        BookingPostDto bookingPostDto = new BookingPostDto(
                1L,
                LocalDateTime.now().plus(10L, ChronoUnit.HOURS),
                LocalDateTime.now().plus(100L, ChronoUnit.HOURS)
        );
        BookingDto bookingDto = bookingService.setApproved(1L, 1L, true);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
        assertThat(bookingDto.getItem()).isNotNull();
    }
}
