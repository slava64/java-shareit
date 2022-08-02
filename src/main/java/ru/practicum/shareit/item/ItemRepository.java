package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

interface ItemRepository {
    public Collection<Item> findAllByUser(User user);
    public Optional<Item> findOne(Long id);
    public Item add(Item item);
    public Item update(Long id, Item item);
    public Boolean delete(Long id);
    public Collection<Item> search(String text);
}
