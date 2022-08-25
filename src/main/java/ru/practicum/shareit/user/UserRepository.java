package ru.practicum.shareit.user;

import java.util.Map;
import java.util.Optional;

public interface UserRepository {
    public Map<Long, User> findAll();
    public Optional<User> findOne(Long id);
    public User add(User user);
    public User update(Long id, User user);
    public Boolean delete(Long id);
    public Boolean isExistByEmail(String email);
}
