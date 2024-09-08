package ru.practicum.priv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.request.ParticipationRequestDto;
import ru.practicum.dto.event.request.RequestDtoMapper;
import ru.practicum.dto.event.request.Status;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Request;
import ru.practicum.priv.repository.PrivateRequestRepository;
import ru.practicum.priv.repository.PrivateUserRepository;
import ru.practicum.pub.repository.PublicEventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PrivateRequestServiceImpl implements PrivateRequestService {
    private final PrivateRequestRepository requestRepository;
    private final PublicEventRepository eventRepository;
    private final PrivateUserRepository userRepository;
    private final RequestDtoMapper requestMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getMyRequests(Long userId) {
        return requestRepository.findByRequesterId(userId);
    }

    @Override
    public ParticipationRequestDto addMyRequest(Long userId, Long eventId) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("user with id %s", userId)));
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("event with id %s", eventId)));
        var request = new Request(null, LocalDateTime.now(), user, event, Status.PENDING);

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelMyRequest(Long userId, Long requestId) {
        var request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new NotFoundException(String.format("request with id %s", requestId)));
        request.setStatus(Status.REJECTED);
        return requestMapper.toParticipationRequestDto(request);
    }
}
