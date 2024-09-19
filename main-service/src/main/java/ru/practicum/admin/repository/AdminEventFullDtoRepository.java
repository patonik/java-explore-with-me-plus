package ru.practicum.admin.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.event.EventFullDto;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventFullDtoRepository {
    List<EventFullDto> getEventsOrderedById(Long[] users, String[] states, Long[] categories, LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd, Pageable pageable);
}
