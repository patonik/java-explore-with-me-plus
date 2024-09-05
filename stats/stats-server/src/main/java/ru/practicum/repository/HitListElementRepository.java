package ru.practicum.repository;

import ru.practicum.dto.HitListElementDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitListElementRepository {
    List<HitListElementDto> getHitListElementDtos(LocalDateTime start,
                                                  LocalDateTime end,
                                                  String[] uris,
                                                  boolean unique);
}
