package ru.practicum.pub.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.HttpStatsClient;
import ru.practicum.dto.StatRequestDto;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventFullDtoMapper;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.SortCriterium;
import ru.practicum.dto.event.State;
import ru.practicum.dto.event.request.RequestCount;
import ru.practicum.dto.event.request.Status;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.pub.repository.PublicEventRepository;
import ru.practicum.util.LocalDateTimeComparator;
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
public class PublicEventService {
    private final PublicEventRepository publicEventRepository;
    private final HttpStatsClient httpStatsClient;
    private final EventFullDtoMapper eventFullDtoMapper;

    public EventFullDto getEvent(Long id, HttpServletRequest request) {
        Event event = publicEventRepository.findByIdAndState(id, State.PUBLISHED)
            .orElseThrow(() -> new NotFoundException("Event not found with id: " + id));
        sendHitToStatsService(request);
        Params params = Statistical.getParams(List.of(event));
        log.info("parameters for statService created: {}", params);
        List<StatResponseDto> statResponseDtoList =
            httpStatsClient.getStats(params.start(), params.end(), params.uriList(), true);
        RequestCount requestCount = publicEventRepository.getRequestCountByEventAndStatus(id, Status.CONFIRMED);
        Long hits = 0L;
        if (!statResponseDtoList.isEmpty()) {
            hits = statResponseDtoList.getFirst().getHits();
        }
        return eventFullDtoMapper.toDto(event,
            requestCount.getConfirmedRequests(), hits);
    }

    public List<EventShortDto> getEvents(String text, Long[] categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, SortCriterium sort,
                                         Integer from, Integer size, HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ConstraintViolationException("Range start is after range end", Set.of());
        }
        Pageable pageable = PageRequest.of(from, size);
        log.info(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
        List<EventShortDto> shortDtos =
            publicEventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
        if (shortDtos.isEmpty()) {
            sendHitToStatsService(request);
            return shortDtos;
        }
        sendHitToStatsService(request);
        Params params = Statistical.getParams(new ArrayList<>(shortDtos));
        log.info("parameters for statService created: {}", params);
        List<StatResponseDto> statResponseDtoList =
            httpStatsClient.getStats(params.start(), params.end(), params.uriList(), true);
        Map<Long, Long> hitMap = statResponseDtoList
            .stream()
            .collect(Collectors.toMap(x -> Long.parseLong(x.getUri().split("/")[2]), StatResponseDto::getHits));
        shortDtos.sort(Comparator.comparingLong(EventShortDto::getId));
        for (EventShortDto eventShortDto : shortDtos) {
            long hits = 0L;
            Long eventId = eventShortDto.getId();
            if (!hitMap.isEmpty() && hitMap.containsKey(eventId)) {
                hits = hitMap.get(eventId);
            }
            eventShortDto.setViews(hits);
        }
        switch (sort) {
            case null:
                break;
            case VIEWS:
                shortDtos.sort(Comparator.comparingLong(EventShortDto::getViews));
                break;
            case EVENT_DATE:
                shortDtos.sort(new LocalDateTimeComparator());
                break;
        }
        return shortDtos;
    }

    private void sendHitToStatsService(HttpServletRequest request) {
        StatRequestDto hit = StatRequestDto.builder()
            .app("public-event-service")
            .uri(request.getRequestURI())
            .ip(request.getRemoteAddr())
            .timestamp(LocalDateTime.now())
            .build();
        httpStatsClient.sendHit(hit, StatRequestDto.class);
    }

}
