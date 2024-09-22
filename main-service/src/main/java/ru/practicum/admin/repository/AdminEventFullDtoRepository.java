package ru.practicum.admin.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.State;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventFullDtoRepository {
    List<EventFullDto> getEventsOrderedById(List<Long> users,
                                            List<State> states,
                                            List<Long> categories,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Pageable pageable);
}
