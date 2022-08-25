package ru.practicum.shareit.user;

import java.util.Collection;

interface UserService {
    public Collection<UserDto> findAll();
    public UserDto findOne(Long id);
    public UserDto add(UserDto userDto);
    public UserDto update(Long id, UserDto userDto);
    public Boolean delete(Long id);
}
