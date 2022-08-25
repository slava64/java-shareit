package ru.practicum.shareit.item;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.User;

@Getter
@Setter
@ToString
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
