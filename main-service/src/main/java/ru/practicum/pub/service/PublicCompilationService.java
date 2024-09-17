package ru.practicum.pub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.HttpStatsClient;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationDtoMapper;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.pub.repository.PublicCompilationRepository;
import ru.practicum.util.Params;
import ru.practicum.util.Statistical;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationService {
    private final PublicCompilationRepository publicCompilationRepository;
    private final HttpStatsClient httpStatsClient;
    private final CompilationDtoMapper compilationDtoMapper;

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<CompilationDto> allCompilationDtos = publicCompilationRepository.findAllCompilationDtos(pinned, pageable);
        List<EventShortDto> shortDtos = allCompilationDtos
            .stream()
            .flatMap(x -> x.getEvents().stream())
            .collect(Collectors.toList());
        if (shortDtos.isEmpty()) {
            return allCompilationDtos;
        }
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
        return allCompilationDtos;
    }

    public CompilationDto getCompilation(Long compId) {
        Compilation compilation =
            publicCompilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + compId + " not found"));
        return compilationDtoMapper.toCompilationDto(compilation);
    }
}
