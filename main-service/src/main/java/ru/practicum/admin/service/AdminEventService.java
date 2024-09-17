package ru.practicum.admin.service;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HttpStatsClient;
import ru.practicum.admin.repository.AdminCategoryRepository;
import ru.practicum.admin.repository.AdminEventRepository;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventFullDtoMapper;
import ru.practicum.dto.event.State;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventAdminRequestMapper;
import ru.practicum.dto.event.request.RequestCount;
import ru.practicum.dto.event.request.Status;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.util.Params;
import ru.practicum.util.Statistical;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminEventService {
    private final AdminEventRepository adminEventRepository;
    private final AdminCategoryRepository categoryRepository;
    private final UpdateEventAdminRequestMapper updateEventAdminRequestMapper;
    private final EventFullDtoMapper eventFullDtoMapper;
    private final HttpStatsClient httpStatsClient;

    public List<EventFullDto> getEvents(Long[] users, String[] states, Long[] categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ConstraintViolationException("Range start is after range end", Set.of());
        }
        Pageable pageable = PageRequest.of(from, size);
        List<EventFullDto> events =
            new ArrayList<>(adminEventRepository.getEvents(users, states, categories, rangeStart, rangeEnd, pageable));
        if (events.isEmpty()) {
            return events;
        }
        Params params = Statistical.getParams(new ArrayList<>(events));
        log.info("parameters for statService created: {}", params);
        List<StatResponseDto> statResponseDtoList =
            httpStatsClient.getStats(params.start(), params.end(), params.uriList(), true);
        Map<Long, Long> hitMap = statResponseDtoList
            .stream()
            .collect(Collectors.toMap(x -> Long.parseLong(x.getUri().split("/")[2]), StatResponseDto::getHits));
        events.sort(Comparator.comparingLong(EventFullDto::getId));
        for (EventFullDto eventFullDto : events) {
            long hits = 0L;
            Long eventId = eventFullDto.getId();
            if (!hitMap.isEmpty() && hitMap.containsKey(eventId)) {
                hits = hitMap.get(eventId);
            }
            eventFullDto.setViews(hits);
        }
        return events;
    }

    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest adminRequest) {
        Event event =
            adminEventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        Long categoryId = adminRequest.getCategoryId();
        Category category;
        if (categoryId != null) {
            category =
                categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
        } else {
            category = event.getCategory();
        }
        log.info("updating event: {}", event);
        event = updateEventAdminRequestMapper.updateEvent(adminRequest, event,
            category);
        log.info("updated event: {}", event);
        log.info("saving...");
        switch (adminRequest.getStateAction()) {
            case null:
                break;
            case PUBLISH_EVENT:
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;
            case REJECT_EVENT:
                event.setState(State.CANCELED);
                log.info("event state changed to {}", event.getState());
                break;
        }
        event = adminEventRepository.save(event);
        RequestCount requestCount = adminEventRepository.getRequestCountByEventAndStatus(eventId, Status.CONFIRMED);
        Params params = Statistical.getParams(List.of(event));
        log.info("parameters for statService created: {}", params);
        List<StatResponseDto> statResponseDtoList =
            httpStatsClient.getStats(params.start(), params.end(), params.uriList(), true);
        Long hits = 0L;
        if (!statResponseDtoList.isEmpty()) {
            hits = statResponseDtoList.getFirst().getHits();
        }
        return eventFullDtoMapper.toDto(event, hits,
            requestCount.getConfirmedRequests());
    }
}
