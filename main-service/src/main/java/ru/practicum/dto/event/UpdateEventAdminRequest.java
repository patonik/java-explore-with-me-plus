package ru.practicum.dto.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.validation.LaterThan;

import java.time.LocalDateTime;

/**
 * DTO request to the {@link ru.practicum.admin.controller.AdminEventController}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    @Min(1)
    private Long categoryId;
    @Size(min = 20, max = 7000)
    private String description;
    @LaterThan
    private LocalDateTime eventDate;
    @Valid
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private AdminStateAction stateAction;
    @Size(min = 3, max = 120)
    private String title;
}
