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
            .toList();
        if (shortDtos.isEmpty()) {
            return allCompilationDtos;
        }
        Params params = Statistical.getParams(new ArrayList<>(shortDtos));
        log.info("parameters for statService created: {}", params);
        List<StatResponseDto> statResponseDtoList =
            httpStatsClient.getStats(params.start(), params.end(), params.uriList(), false);
        long[] hitList = statResponseDtoList
            .stream()
            .sorted(Comparator.comparingLong(x -> Long.parseLong(x.getUri().split("/")[2])))
            .mapToLong(StatResponseDto::getHits)
            .toArray();
        shortDtos.sort(Comparator.comparingLong(Statistical::getId));
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
}
