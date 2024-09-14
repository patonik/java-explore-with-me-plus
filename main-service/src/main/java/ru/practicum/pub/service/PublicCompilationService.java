package ru.practicum.pub.service;

import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
            .sorted(
                (x, y) -> x.getEventDate().isBefore(y.getEventDate()) ? -1 :
                    x.getEventDate().isAfter(y.getEventDate()) ? 1 : 0).collect(Collectors.toList());
        if (shortDtos.isEmpty()) {
            return allCompilationDtos;
        }
        Params params = getParams(shortDtos);
        List<StatResponseDto> statResponseDto =
            httpStatsClient.getStats(params.start(), params.end(), params.uriList(), false);
        long[] hitList =
            statResponseDto
                .stream()
                .sorted(Comparator.comparingLong(x -> Long.parseLong(x.getUri().split("/")[2])))
                .mapToLong(StatResponseDto::getHits)
                .toArray();
        shortDtos.sort(Comparator.comparingLong(EventShortDto::getId));
        for (int i = 0; i < hitList.length; i++) {
            shortDtos.get(i).setViews(hitList[i]);
        }
        return allCompilationDtos;
    }

    public CompilationDto getCompilation(Long compId) {
        Compilation compilation =
            publicCompilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + compId + " not found"));
        return compilationDtoMapper.toCompilationDto(compilation);
    }

    private static Params getParams(List<EventShortDto> eventList) {
        String start = String.valueOf(eventList.getFirst().getCreatedOn());
        String end = String.valueOf(LocalDateTime.now());
        List<String> uriList = eventList.stream().map(x -> "/events/" + x.getId()).toList();
        return new Params(start, end, uriList);
    }

    private record Params(String start, String end, List<String> uriList) {
    }
}
