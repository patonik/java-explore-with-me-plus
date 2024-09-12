package ru.practicum.priv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.State;
import ru.practicum.dto.event.request.ParticipationRequestDto;
import ru.practicum.dto.event.request.RequestDtoMapper;
import ru.practicum.dto.event.request.Status;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Request;
import ru.practicum.priv.repository.PrivateEventRepository;
import ru.practicum.priv.repository.PrivateUserRepository;
import ru.practicum.priv.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PrivateRequestServiceImpl implements PrivateRequestService {
    private final RequestRepository requestRepository;
    private final PrivateEventRepository eventRepository;
    private final PrivateUserRepository userRepository;
    private final RequestDtoMapper requestMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getMyRequests(Long userId) {
        return requestRepository.findByRequesterId(userId);
    }

    @Override
    public ParticipationRequestDto addMyRequest(Long userId, Long eventId) {
        var optUser = userRepository.findById(userId);
        var optEvent = eventRepository.findById(eventId);

        if (optUser.isEmpty()) {
            throw new NotFoundException(String.format("user with id %s", userId));
        }
        if (optEvent.isEmpty()) {
            throw new NotFoundException(String.format("event with id %s", eventId));
        }
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException("request already exists");
        }
        if (eventRepository.findByIdAndInitiatorId(eventId, userId).isEmpty()) {
            throw new ConflictException("""
                    the initiator of the event cannot add a participation request for their own event.
                    """);
        }

        var user = optUser.get();
        var event = optEvent.get();

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("cannot participate in an unpublished event.");
        }
        if (event.getParticipantLimit().equals(requestRepository.countByEventId(eventId))) {
            throw new ConflictException("The participant limit for this event has been reached.");
        }

        var requestStatus = event.getRequestModeration() ? Status.PENDING : Status.CONFIRMED;
        var request = new Request(null, LocalDateTime.now(), user, event, requestStatus);

        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelMyRequest(Long userId, Long requestId) {
        var request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new NotFoundException(String.format("request with id %s", requestId)));
        request.setStatus(Status.PENDING);
        return requestMapper.toParticipationRequestDto(request);
    }
}
