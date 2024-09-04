package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceHitDto {

    @NotBlank(message = "App cannot be blank or empty")
    private String app;

    @NotBlank(message = "URI cannot be blank or empty")
    private String uri;

    @Pattern(regexp = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$", message = "Invalid IP format")
    @NotBlank(message = "IP cannot be blank or empty")
    private String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Timestamp cannot be empty")
    private LocalDateTime timestamp;
}
