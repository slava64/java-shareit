package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Collection<UserDto> findAll() {
        return UserMapper.toUserDto(userRepository.findAll().values());
    }

    @Override
    public UserDto findOne(Long id) {
        return UserMapper.toUserDto(findUser(id));
    }

    @Override
    public UserDto add(UserDto userDto) {
        validateEmail(userDto.getEmail());
        User addUser = userRepository.add(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(addUser);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        if (userDto.getEmail() != null) {
            validateEmail(userDto.getEmail());
        }
        User user = findUser(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        User updatedUser = userRepository.update(id, user);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public Boolean delete(Long id) {
        findUser(id);
        return userRepository.delete(id);
    }

    private User findUser(Long id) {
        return userRepository.findOne(id).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь %d не найден", id)));
    }

    private void validateEmail(String email) {
        if(email == null || email.isEmpty() || email.isBlank()) {
            throw new BadRequestException("Поле Email не может быть пустым");
        }
        Pattern pattern = Pattern.compile("^(.+)@([^@]+[^.])$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.find()) {
            throw new BadRequestException("Не верный формат email");
        }
        if(userRepository.isExistByEmail(email)) {
            throw new ConflictException(String.format("Пользователь %s уже существует", email));
        }
    }
}