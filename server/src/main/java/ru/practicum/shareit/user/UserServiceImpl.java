package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUserDto(users);
    }

    @Override
    public UserDto findOne(Long id) {
        return UserMapper.toUserDto(findUser(id));
    }

    @Override
    public UserDto add(UserDto userDto) {
        User addUser = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(addUser);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = findUser(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        User updatedUser = userRepository.save(user);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public Boolean delete(Long id) {
        findUser(id);
        userRepository.deleteById(id);
        return true;
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь %d не найден", id))
        );
    }
}
