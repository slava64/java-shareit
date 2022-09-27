package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;

public class UserServiceImplTest {
    UserService userService;

    UserRepository userRepository;

    private final User user = new User();

    public UserServiceImplTest() {
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@mail.ru");

        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testFindAll() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));

        Collection<UserDto> userDtoList = userService.findAll();

        assertThat(userDtoList).isNotNull();
        assertThat(userDtoList.size()).isEqualTo(1);
    }

    @Test
    void testFindOne() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto userDto = userService.findOne(1L);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(user.getId());
        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void testFindOneNotFound() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findOne(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testAdd() {
        Mockito.when(userRepository.save(any())).thenReturn(user);
        UserDto userDto = new UserDto(
                1L,
                "test",
                "test@mail.ru"
        );
        UserDto addUser = userService.add(userDto);

        assertThat(userDto).isNotNull();
        assertThat(addUser.getId()).isEqualTo(user.getId());
        assertThat(addUser.getName()).isEqualTo(user.getName());
        assertThat(addUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void testAddValidateEmailIsEmpty() {
        UserDto userDto = new UserDto(
                1L,
                "test",
                ""
        );
        assertThatThrownBy(() -> userService.add(userDto))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testAddValidateEmailFormat() {
        UserDto userDto = new UserDto(
                1L,
                "test",
                "test"
        );
        assertThatThrownBy(() -> userService.add(userDto))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testUpdate() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(any())).thenReturn(user);

        UserDto userDto = new UserDto(
                1L,
                "test",
                "test@mail.ru"
        );
        UserDto addUser = userService.update(1L, userDto);

        assertThat(userDto).isNotNull();
        assertThat(addUser.getId()).isEqualTo(user.getId());
        assertThat(addUser.getName()).isEqualTo(user.getName());
        assertThat(addUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void testUpdateUserNotFound() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        UserDto userDto = new UserDto(
                1L,
                "test",
                "test@mail.ru"
        );
        assertThatThrownBy(() -> userService.update(1L, userDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testUpdateValidateEmailFormat() {
        UserDto userDto = new UserDto(
                1L,
                "test",
                "test"
        );
        assertThatThrownBy(() -> userService.update(1L, userDto))
                .isInstanceOf(BadRequestException.class);
    }
}
