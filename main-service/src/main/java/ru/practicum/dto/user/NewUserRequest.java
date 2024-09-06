package ru.practicum.dto.user;

import lombok.Data;

@Data
public class NewUserRequest {
    private String name;
    private String email;
}
