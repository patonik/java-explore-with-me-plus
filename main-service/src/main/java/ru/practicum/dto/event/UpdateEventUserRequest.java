package ru.practicum.dto.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.validation.LaterThan;

import java.time.LocalDateTime;

@Data
@Builder
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    @Min(1)
    private Long category;
    @Size(min = 20, max = 7000)
    private String description;
    @LaterThan
    private LocalDateTime eventDate;
    @Valid
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private UserStateAction userStateAction;
    @Size(min = 3, max = 120)
    private String title;
}
