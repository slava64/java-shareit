package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.*;

public class UserRepositoryImpl implements UserRepository_ {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = Long.valueOf(1);

    @Override
    public Map<Long, User> findAll() {
        return users;
    }

    @Override
    public Optional<User> findOne(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User add(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User update(Long id, User user) {
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public Boolean delete(Long id) {
        users.remove(id);
        return true;
    }

    @Override
    public Boolean isExistByEmail(String email) {
        return users.values()
                .stream()
                .map(User::getEmail)
                .filter(e -> e.equals(email))
                .count() > 0;
    }

    private Long getId() {
        long lastId = users.values()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
