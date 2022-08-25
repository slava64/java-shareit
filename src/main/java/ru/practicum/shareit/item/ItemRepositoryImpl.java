package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private Map<Long, Item> items = new HashMap<>();
    private Long id = Long.valueOf(0);

    @Override
    public Collection<Item> findAllByUser(User user) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findOne(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item add(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        return items.get(id);
    }

    @Override
    public Item update(Long id, Item item) {
        items.put(id, item);
        return items.get(id);
    }

    @Override
    public Boolean delete(Long id) {
        items.remove(id);
        return true;
    }

    @Override
    public Collection<Item> search(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        } else {
            return items.values()
                    .stream()
                    .filter(
                            item -> item.getName().toLowerCase().indexOf(text.toLowerCase()) > -1
                                    || item.getDescription().toLowerCase().indexOf(text.toLowerCase()) > -1)
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
    }
}
