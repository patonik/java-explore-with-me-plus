package ru.practicum.dto.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.validation.LaterThan;

import java.time.LocalDateTime;

/**
 * DTO request to the {@link ru.practicum.priv.controller.PrivateEventController}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    @Min(1)
    private Long categoryId;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @LaterThan(2)
    @NotNull
    private LocalDateTime eventDate;
    @Valid
    @NotNull
    private Location location;
    @Builder.Default
    private Boolean paid = Boolean.FALSE;
    @Builder.Default
    private Integer participantLimit = 0;
    @Builder.Default
    private Boolean requestModeration = Boolean.TRUE;
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
