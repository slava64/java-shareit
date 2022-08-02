package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.user.User;

import java.time.Instant;

public class ItemRequestDto {
    private Long id;
    private String description;
    private User requestor;
    private Instant created;
}
