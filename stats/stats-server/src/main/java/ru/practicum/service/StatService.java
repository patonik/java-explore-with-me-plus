package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatRequestDto;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.mapper.ServiceHitMapper;
import ru.practicum.model.ServiceHit;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static ru.practicum.constants.DataTransferConvention.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class StatService {
    private final StatRepository statRepository;
    private final ServiceHitMapper serviceHitMapper;

    public StatRequestDto registerHit(StatRequestDto statRequestDto) {
        ServiceHit entity = serviceHitMapper.toEntity(statRequestDto);
        log.info("StatService converted entity: {}", entity);
        ServiceHit saved = statRepository.save(entity);
        log.info("StatService saved entity: {}", saved);
        StatRequestDto dto = serviceHitMapper.toDto(saved);
        log.info("StatService converted dto: {}", dto);
        return dto;
    }

    public List<StatResponseDto> getHits(String start, String end, String[] uris, Boolean unique) {
        LocalDateTime from;
        LocalDateTime to;
        try {
            from = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
            to = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e);
        }
        List<StatResponseDto> statResponseDtos;
        statResponseDtos = statRepository.getHitListElementDtos(from, to, uris, unique);
        return statResponseDtos;
    }
}
