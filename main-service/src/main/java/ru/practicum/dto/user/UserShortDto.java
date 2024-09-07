package ru.practicum.dto.user;

import lombok.Data;

/**
 * DTO part of {@link ru.practicum.dto.event.EventShortDto} and {@link ru.practicum.dto.event.EventFullDto}
 */
@Data
public class UserShortDto {
    private Long id;
    private String name;
}
