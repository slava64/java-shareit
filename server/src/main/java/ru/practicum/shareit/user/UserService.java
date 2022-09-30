package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> findAll();

    UserDto findOne(Long id);

    UserDto add(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    Boolean delete(Long id);
}
