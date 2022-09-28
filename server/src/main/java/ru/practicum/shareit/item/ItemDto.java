package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    @Transient
    private List<CommentDto> comments;
}
