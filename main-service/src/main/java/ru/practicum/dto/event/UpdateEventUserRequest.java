package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.validation.LaterThan;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    @Min(1)
    @JsonProperty("category")
    private Long categoryId;
    @Size(min = 20, max = 7000)
    private String description;
    @LaterThan
    private LocalDateTime eventDate;
    @Valid
    private Location location;
    private Boolean paid;
    @Positive
    private Integer participantLimit;
    private Boolean requestModeration;
    private UserStateAction userStateAction;
    @Size(min = 3, max = 120)
    private String title;
}
