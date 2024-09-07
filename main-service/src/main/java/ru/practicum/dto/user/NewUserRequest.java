package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO request to {@link ru.practicum.admin.controller.AdminUserController}
 */
@Data
public class NewUserRequest {
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
    @NotNull
    @Email
    @Size(min = 6, max = 254)
    private String email;
}
