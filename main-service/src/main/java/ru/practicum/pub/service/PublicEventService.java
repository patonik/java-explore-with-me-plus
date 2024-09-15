package ru.practicum.pub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.HttpStatsClient;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.dto.Statistical;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicEventService {
    private final PublicEventRepository publicEventRepository;
    private final HttpStatsClient httpStatsClient;
    private final EventFullDtoMapper eventFullDtoMapper;

    public EventFullDto getEvent(Long id) {
        Event event = publicEventRepository.findByIdAndState(id, State.PUBLISHED)
            .orElseThrow(() -> new NotFoundException("Event not found with id: " + id));
        Params params = getParams(List.of(event));
        List<StatResponseDto> statResponseDtoList =
            httpStatsClient.getStats(params.start(), params.end(), params.uriList(), false);
        RequestCount requestCount = publicEventRepository.getRequestCountByEventAndStatus(id, Status.CONFIRMED);
        return eventFullDtoMapper.toDto(event,
            requestCount.getConfirmedRequests(), statResponseDtoList.getFirst().getHits());
    }

    public List<EventShortDto> getEvents(String text, Long[] categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, SortCriterium sort,
                                         Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<EventShortDto> shortDtos =
            publicEventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
        Params params = getParams(new ArrayList<>(shortDtos));
        List<StatResponseDto> statResponseDtoList =
            httpStatsClient.getStats(params.start(), params.end(), params.uriList(), false);
        long[] hitList = statResponseDtoList
            .stream()
            .sorted(Comparator.comparingLong(x -> Long.parseLong(x.getUri().split("/")[2])))
            .mapToLong(StatResponseDto::getHits)
            .toArray();
        shortDtos.sort(Comparator.comparingLong(EventShortDto::getId));
        for (int i = 0; i < hitList.length; i++) {
            shortDtos.get(i).setViews(hitList[i]);
        }
        switch (sort) {
            case VIEWS:
                shortDtos.sort(Comparator.comparingLong(EventShortDto::getViews));
            case EVENT_DATE:
                shortDtos.sort(new LocalDateTimeComparator());
        }
        return shortDtos;
    }

    private static Params getParams(List<Statistical> events) {
        String end = String.valueOf(LocalDateTime.now());
        String start = String.valueOf(events.stream().min(new LocalDateTimeComparator())
            .orElseThrow(() -> new RuntimeException("start date cannot be null")).getEventDate());
        List<String> uriList = events.stream().map(x -> "/events/" + x.getId()).toList();
        return new Params(start, end, uriList);
    }

    private record Params(String start, String end, List<String> uriList) {
    }

    private static class LocalDateTimeComparator implements Comparator<Statistical> {
        @Override
        public int compare(Statistical x, Statistical y) {
            return x.getEventDate().isBefore(y.getEventDate()) ? -1 :
                x.getEventDate().isAfter(y.getEventDate()) ? 1 : 0;
        }
    }
}
