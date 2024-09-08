package ru.practicum.priv.service;

import ru.practicum.dto.event.request.ParticipationRequestDto;

public interface PrivateRequestService {
    ParticipationRequestDto addMyRequest(Long userId, Long eventId);
}
