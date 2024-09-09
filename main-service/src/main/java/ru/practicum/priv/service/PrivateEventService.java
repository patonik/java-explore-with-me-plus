package ru.practicum.priv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HttpStatsClient;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventFullDtoMapper;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventShortDtoMapper;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.NewEventDtoMapper;
import ru.practicum.dto.event.State;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.event.UpdateEventUserRequestMapper;
import ru.practicum.dto.event.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.request.ParticipationRequestDto;
import ru.practicum.dto.event.request.Status;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.priv.repository.PrivateCategoryRepository;
import ru.practicum.priv.repository.PrivateEventRepository;
import ru.practicum.priv.repository.PrivateUserRepository;
import ru.practicum.priv.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateEventService {
    private final PrivateEventRepository privateEventRepository;
    private final PrivateUserRepository privateUserRepository;
    private final PrivateCategoryRepository privateCategoryRepository;
    private final RequestRepository requestRepository;
    private final NewEventDtoMapper newEventDtoMapper;
    private final EventFullDtoMapper eventFullDtoMapper;
    private final EventShortDtoMapper eventShortDtoMapper;
    private final UpdateEventUserRequestMapper updateEventUserRequestMapper;
    private final HttpStatsClient httpStatsClient;

    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        User initiator =
            privateUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not authorized to add new event"));
        Category category = privateCategoryRepository.findById(newEventDto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("No such category"));
        Event event = newEventDtoMapper.toEvent(newEventDto, initiator, category, State.PENDING);
        event = privateEventRepository.save(event);
        Long confirmedRequests = 0L;
        return eventFullDtoMapper.toDto(event, confirmedRequests);
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getMyEvents(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Event> eventList = privateEventRepository.findAllByInitiatorIdOrderByCreatedOnAsc(userId, pageable);
        Params params = getParams(eventList);
        List<StatResponseDto> statResponseDto =
            httpStatsClient.getStats(params.start(), params.end(), params.uriList(), false);
        long[] hitList =
            statResponseDto
                .stream()
                .sorted(Comparator.comparingLong(x -> Long.parseLong(x.getUri().split("/")[2])))
                .mapToLong(StatResponseDto::getHits)
                .toArray();
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (int i = 0; i < hitList.length; i++) {
            eventShortDtos.add(eventShortDtoMapper.toDto(eventList.get(i), hitList[i]));
        }
        return eventShortDtos;
    }

    @Transactional(readOnly = true)
    public EventFullDto getMyEvent(Long userId, Long eventId) {
        Event event =
            privateEventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
        Long confirmedRequests =
            privateEventRepository.getRequestCountByEventAndStatus(eventId, Status.CONFIRMED).getConfirmedRequests();
        return eventFullDtoMapper.toDto(event, confirmedRequests);
    }

    @Transactional
    public EventFullDto updateMyEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event =
            privateEventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("You can't update a new event: " + eventId);
        }
        event = updateEventUserRequestMapper.updateEvent(updateEventUserRequest, event);
        event = privateEventRepository.save(event);
        Long confirmedRequests =
            privateEventRepository.getRequestCountByEventAndStatus(eventId, Status.CONFIRMED).getConfirmedRequests();
        return eventFullDtoMapper.toDto(event, confirmedRequests);
    }

    public List<ParticipationRequestDto> getMyEventRequests(Long userId, Long eventId) {
        Event event =
            privateEventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
        return requestRepository.findByEventId(eventId);
    }

    public EventRequestStatusUpdateResult updateMyEventRequests(Long userId, Long eventId,
                                                                EventRequestStatusUpdateRequest updateRequest) {
        Event event =
            privateEventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
        return null;
    }

    private static Params getParams(List<Event> eventList) {
        String start = String.valueOf(eventList.getFirst().getCreatedOn());
        String end = String.valueOf(LocalDateTime.now());
        List<String> uriList = eventList.stream().map(x -> "/events/" + x.getId()).toList();
        return new Params(start, end, uriList);
    }

    private record Params(String start, String end, List<String> uriList) {
    }
}
