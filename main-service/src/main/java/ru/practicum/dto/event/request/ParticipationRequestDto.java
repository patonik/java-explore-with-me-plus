package ru.practicum.dto.event.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    @JsonProperty("event")
    private Long eventId;
    private LocalDateTime created;
    private Long requester;
    private Status status;
}
