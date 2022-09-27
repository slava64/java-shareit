package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

public class ItemRequestServiceImplTest {
    User user = new User();
    ItemRequest itemRequest = new ItemRequest();
    ItemRequestService itemRequestService;

    UserRepository userRepository;
    ItemRequestRepository itemRequestRepository;

    public ItemRequestServiceImplTest() {
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@mail.ru");

        itemRequest.setId(1L);
        itemRequest.setDescription("Description");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);

        userRepository = Mockito.mock(UserRepository.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);

        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRequestRepository);
    }

    @Test
    void testFindAllByUser() {
        Mockito.when(
                itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(any())
        ).thenReturn(List.of(itemRequest));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));

        Collection<ItemRequestDto> itemRequestDtoList = itemRequestService.findAllByUser(1L);

        assertThat(itemRequestDtoList).isNotNull();
        assertThat(itemRequestDtoList.size()).isEqualTo(1);
    }

    @Test
    void testFindAllByUserWhereUserNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemRequestService.findAllByUser(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testFindAllByUserWithPageable() {
        Mockito.when(
                itemRequestRepository.findAllByRequestorIdNot(any(), any())
        ).thenReturn(List.of(itemRequest));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));

        Collection<ItemRequestDto> itemRequestDtoList = itemRequestService.findAllByUser(1L, 0, 20);

        assertThat(itemRequestDtoList).isNotNull();
        assertThat(itemRequestDtoList.size()).isEqualTo(1);
    }

    @Test
    void testFindAllByUserWithPageableWhereUserNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemRequestService.findAllByUser(1L, 0, 20))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testFindAllByUserWithPageableWhereFromLessZero() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> itemRequestService.findAllByUser(1L, -1, 20))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testFindAllByUserWithPageableWhereSizeZero() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> itemRequestService.findAllByUser(1L, 0, 0))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testFindOneByUser() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));
        ItemRequestDto itemRequestDto = itemRequestService.findOneByUser(1L, 1L);

        assertThat(itemRequestDto).isNotNull();
        assertThat(itemRequestDto.getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequestDto.getCreated()).isEqualTo(itemRequest.getCreated());
    }

    @Test
    void testFindOneByUserWhereUserNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.findOneByUser(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testFindOneByUserWhereItemRequestNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.findOneByUser(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testAddWhereUserNotFound() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());
        ItemRequestPostDto itemRequestPostDto = new ItemRequestPostDto(
                "Description"
        );
        assertThatThrownBy(() -> itemRequestService.add(1L, itemRequestPostDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testAddWhereDescriptionIsEmpty() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestPostDto itemRequestPostDto = new ItemRequestPostDto(
                ""
        );
        assertThatThrownBy(() -> itemRequestService.add(1L, itemRequestPostDto))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testAdd() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestPostDto itemRequestPostDto = new ItemRequestPostDto(
                "Description"
        );
        ItemRequestDto itemRequestDto = itemRequestService.add(1L, itemRequestPostDto);
        assertThat(itemRequestDto).isNotNull();
        assertThat(itemRequestDto.getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequestDto.getDescription()).isEqualTo(itemRequest.getDescription());
        assertThat(itemRequestDto.getCreated()).isEqualTo(itemRequest.getCreated());
    }
}
