package ru.practicum.dto.event.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ParticipationRequestDto {

    @Positive(message = "ID must be a positive number")
    private Long id;

    @NotNull(message = "Creation date cannot be null")
    @PastOrPresent(message = "Creation date cannot be in the future")
    private LocalDateTime created;

    @NotNull(message = "Requester ID cannot be null")
    @Positive(message = "Requester ID must be a positive number")
    private Long requester;

    @NotNull(message = "Status cannot be null")
    private Status status;
}
