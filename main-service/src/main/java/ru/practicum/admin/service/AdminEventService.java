package ru.practicum.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HttpStatsClient;
import ru.practicum.admin.repository.AdminCategoryRepository;
import ru.practicum.admin.repository.AdminEventRepository;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.dto.Statistical;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventFullDtoMapper;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventAdminRequestMapper;
import ru.practicum.dto.event.request.RequestCount;
import ru.practicum.dto.event.request.Status;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventService {
    private final AdminEventRepository adminEventRepository;
    private final AdminCategoryRepository categoryRepository;
    private final UpdateEventAdminRequestMapper updateEventAdminRequestMapper;
    private final EventFullDtoMapper eventFullDtoMapper;
    private final HttpStatsClient httpStatsClient;

    public List<EventFullDto> getEvents(Long[] users, String[] states, Long[] categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<EventFullDto> events =
            new ArrayList<>(adminEventRepository.getEvents(users, states, categories, rangeStart, rangeEnd, pageable));
        if (events.isEmpty()) {
            return events;
        }
        Params params = getParams(new ArrayList<>(events));
        List<StatResponseDto> statResponseDto =
            httpStatsClient.getStats(params.start(), params.end(), params.uriList(), false);
        long[] hitList =
            statResponseDto
                .stream()
                .sorted(Comparator.comparingLong(x -> Long.parseLong(x.getUri().split("/")[2])))
                .mapToLong(StatResponseDto::getHits)
                .toArray();
        events.sort(Comparator.comparingLong(EventFullDto::getId));
        for (int i = 0; i < hitList.length; i++) {
            events.get(i).setViews(hitList[i]);
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
        event = updateEventAdminRequestMapper.updateEvent(adminRequest, event,
            category);
        event = adminEventRepository.save(event);
        RequestCount requestCount = adminEventRepository.getRequestCountByEventAndStatus(eventId, Status.CONFIRMED);
        Params params = getParams(List.of(event));
        List<StatResponseDto> statResponseDtoList =
            httpStatsClient.getStats(params.start(), params.end(), params.uriList(), false);
        return eventFullDtoMapper.toDto(event, statResponseDtoList.getFirst().getHits(),
            requestCount.getConfirmedRequests());
    }

    private static Params getParams(List<Statistical> events) {
        String end = String.valueOf(LocalDateTime.now());
        String start = String.valueOf(events.stream().min((x, y) -> x.getEventDate().isBefore(y.getEventDate()) ? -1 :
                x.getEventDate().isAfter(y.getEventDate()) ? 1 : 0)
            .orElseThrow(() -> new RuntimeException("start date cannot be null")).getEventDate());
        List<String> uriList = events.stream().map(x -> "/events/" + x.getId()).toList();
        return new Params(start, end, uriList);
    }

    private record Params(String start, String end, List<String> uriList) {
    }
}
