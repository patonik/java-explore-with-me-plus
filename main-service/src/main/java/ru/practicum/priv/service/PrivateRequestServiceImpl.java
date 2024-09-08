package ru.practicum.priv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.request.ParticipationRequestDto;
import ru.practicum.priv.repository.PrivateRequestRepository;

@Service
@RequiredArgsConstructor
public class PrivateRequestServiceImpl implements PrivateRequestService {

    private final PrivateRequestRepository requestRepository;

    @Override
    public ParticipationRequestDto addMyRequest(Long userId, Long eventId) {
        return null;
    }
}
