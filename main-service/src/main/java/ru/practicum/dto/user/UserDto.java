package ru.practicum.dto.user;

import lombok.Data;

/**
 * DTO response of the {@link ru.practicum.admin.controller.AdminUserController}
 */

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
