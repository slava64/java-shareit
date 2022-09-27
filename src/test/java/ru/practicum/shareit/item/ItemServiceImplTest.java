package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

public class ItemServiceImplTest {
    ItemService itemService;
    ItemRepository itemRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;
    BookingRepository bookingRepository;
    ItemRequestRepository itemRequestRepository;

    private final Item item = new Item();
    private final User user = new User();
    private final Booking booking = new Booking();
    private final ItemRequest itemRequest = new ItemRequest();
    private final Comment comment = new Comment();

    public ItemServiceImplTest() {
        user.setId(1L);
        user.setName("petr");
        user.setEmail("petr@mail.com");

        item.setId(1L);
        item.setName("Name");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        comment.setId(1L);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        itemRequest.setId(1L);
        itemRequest.setDescription("Desc");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);

        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);

        itemService = new ItemServiceImpl(
                userRepository,
                itemRepository,
                commentRepository,
                bookingRepository,
                itemRequestRepository
                );
    }

    @Test
    void testFindAllByUser() {
        Mockito.when(
                itemRepository.findAllByOwnerIdOrderById(any())
        ).thenReturn(List.of(item));
        Collection<ItemWithBookingDto> itemWithBookingDtoList = itemService.findAllByUser(1L);

        assertThat(itemWithBookingDtoList).isNotNull();
        assertThat(itemWithBookingDtoList.size()).isEqualTo(1);
    }

    @Test
    void testFindOneByUser() {
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        ItemWithBookingDto itemWithBookingDtoList = itemService.findOneByUser(1L, 1L);

        assertThat(itemWithBookingDtoList).isNotNull();
        assertThat(itemWithBookingDtoList.getId()).isEqualTo(item.getId());
        assertThat(itemWithBookingDtoList.getName()).isEqualTo(item.getName());
        assertThat(itemWithBookingDtoList.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemWithBookingDtoList.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    void testFindOneByUserWhereUserNotFound() {
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findOneByUser(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testFindOneByUserWhereItemNotFound() {
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> itemService.findOneByUser(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testAdd() {
        item.setRequest(itemRequest);

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "Desc",
                true,
                1L,
                null
        );

        ItemDto itemDtoAdded = itemService.add(1L, itemDto);

        assertThat(itemDtoAdded).isNotNull();
        assertThat(itemDtoAdded.getId()).isEqualTo(item.getId());
        assertThat(itemDtoAdded.getName()).isEqualTo(item.getName());
        assertThat(itemDtoAdded.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDtoAdded.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(itemDtoAdded.getRequestId()).isNotNull();
    }

    @Test
    void testAddWhereNameIsEmpty() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto(
                1L,
                "",
                "Desc",
                true,
                1L,
                null
        );

        assertThatThrownBy(() -> itemService.add(1L, itemDto))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testAddWhereDescriptionIsEmpty() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "",
                true,
                1L,
                null
        );

        assertThatThrownBy(() -> itemService.add(1L, itemDto))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testAddWhereAvailableIsEmpty() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "Description",
                null,
                1L,
                null
        );

        assertThatThrownBy(() -> itemService.add(1L, itemDto))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testAddWhereUserNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "Description",
                true,
                1L,
                null
        );

        assertThatThrownBy(() -> itemService.add(1L, itemDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testAddWhereItemRequestNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "Description",
                true,
                1L,
                null
        );

        assertThatThrownBy(() -> itemService.add(1L, itemDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testCreateComment() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.save(any())).thenReturn(comment);
        Mockito.when(
                bookingRepository.countByBookerIdAndItemIdAndStatusAndEndIsBefore(any(), any(), any(), any())
        ).thenReturn(1L);

        CommentPostDto commentPostDto = new CommentPostDto(
                "Comment"
        );

        CommentDto commentDto = itemService.createComment(1L, 1L, commentPostDto);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getId()).isEqualTo(comment.getId());
        assertThat(commentDto.getText()).isEqualTo(comment.getText());
        assertThat(commentDto.getAuthorName()).isEqualTo(user.getName());
        assertThat(commentDto.getCreated()).isNotNull();
    }

    @Test
    void testCreateCommentWhereTextIsEmpty() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.save(any())).thenReturn(comment);
        Mockito.when(
                bookingRepository.countByBookerIdAndItemIdAndStatusAndEndIsBefore(any(), any(), any(), any())
        ).thenReturn(1L);

        CommentPostDto commentPostDto = new CommentPostDto(
                ""
        );

        assertThatThrownBy(() -> itemService.createComment(1L, 1L, commentPostDto))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testCreateCommentWhereUserNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.save(any())).thenReturn(comment);
        Mockito.when(
                bookingRepository.countByBookerIdAndItemIdAndStatusAndEndIsBefore(any(), any(), any(), any())
        ).thenReturn(1L);

        CommentPostDto commentPostDto = new CommentPostDto(
                "Comment"
        );

        assertThatThrownBy(() -> itemService.createComment(1L, 1L, commentPostDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testCreateCommentWhereItemNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(commentRepository.save(any())).thenReturn(comment);
        Mockito.when(
                bookingRepository.countByBookerIdAndItemIdAndStatusAndEndIsBefore(any(), any(), any(), any())
        ).thenReturn(1L);

        CommentPostDto commentPostDto = new CommentPostDto(
                "Comment"
        );

        assertThatThrownBy(() -> itemService.createComment(1L, 1L, commentPostDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testCreateCommentWhereItemWithoutBooking() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.save(any())).thenReturn(comment);
        Mockito.when(
                bookingRepository.countByBookerIdAndItemIdAndStatusAndEndIsBefore(any(), any(), any(), any())
        ).thenReturn(0L);

        CommentPostDto commentPostDto = new CommentPostDto(
                "Comment"
        );

        assertThatThrownBy(() -> itemService.createComment(1L, 1L, commentPostDto))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testUpdate() {
        item.setRequest(itemRequest);

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "Desc",
                true,
                1L,
                null
        );

        ItemDto itemDtoAdded = itemService.update(1L, 1L, itemDto);

        assertThat(itemDtoAdded).isNotNull();
        assertThat(itemDtoAdded.getId()).isEqualTo(item.getId());
        assertThat(itemDtoAdded.getName()).isEqualTo(item.getName());
        assertThat(itemDtoAdded.getDescription()).isEqualTo(item.getDescription());
        assertThat(itemDtoAdded.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(itemDtoAdded.getRequestId()).isNotNull();
    }

    @Test
    void testUpdateWhereItemNotFound() {
        item.setRequest(itemRequest);

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "Desc",
                true,
                1L,
                null
        );

        assertThatThrownBy(() -> itemService.update(1L, 1L, itemDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testUpdateWhereUserNotFound() {
        item.setRequest(itemRequest);

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "Desc",
                true,
                1L,
                null
        );

        assertThatThrownBy(() -> itemService.update(1L, 1L, itemDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testUpdateWhereUserWithoutItem() {
        item.setRequest(itemRequest);

        User newUser = new User();
        newUser.setId(2L);
        newUser.setName("petr");
        newUser.setEmail("petr@mail.com");

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(newUser));
        Mockito.when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "Desc",
                true,
                1L,
                null
        );

        assertThatThrownBy(() -> itemService.update(1L, 1L, itemDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testUpdateWhereItemRequestNotFound() {
        item.setRequest(itemRequest);

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto(
                1L,
                "Name",
                "Desc",
                true,
                1L,
                null
        );

        assertThatThrownBy(() -> itemService.update(1L, 1L, itemDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testDelete() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.doNothing().when(itemRepository).deleteById(any());

        Boolean isDelete = itemService.delete(1L, 1L);

        assertThat(isDelete).isEqualTo(true);
    }

    @Test
    void testDeleteUserNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.doNothing().when(itemRepository).deleteById(any());

        assertThatThrownBy(() -> itemService.delete(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testDeleteItemNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.doNothing().when(itemRepository).deleteById(any());

        assertThatThrownBy(() -> itemService.delete(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testSearch() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.search(any())).thenReturn(List.of(item));

        Collection<ItemDto> itemDtoList = itemService.search(1L, "text");

        assertThat(itemDtoList).isNotNull();
        assertThat(itemDtoList.size()).isEqualTo(1);
    }

    @Test
    void testSearchWhereTextIsEmpty() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.search(any())).thenReturn(List.of(item));

        Collection<ItemDto> itemDtoList = itemService.search(1L, "");

        assertThat(itemDtoList).isNotNull();
        assertThat(itemDtoList.size()).isEqualTo(0);
    }
}
