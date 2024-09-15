package ru.practicum.dto;

import java.time.LocalDateTime;

public interface Statistical {
    LocalDateTime getCreatedOn();

    LocalDateTime getEventDate();

    Long getId();
}
