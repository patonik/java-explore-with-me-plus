package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitListElementDto;
import ru.practicum.dto.ServiceHitDto;
import ru.practicum.mapper.ServiceHitMapper;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static ru.practicum.constants.DataTransferConvention.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StatService {
    private final StatRepository statRepository;
    private final ServiceHitMapper serviceHitMapper;

    public ServiceHitDto registerHit(ServiceHitDto serviceHitDto) {
        return serviceHitMapper.toDto(statRepository.save(serviceHitMapper.toEntity(serviceHitDto)));
    }

    public List<HitListElementDto> getHits(String start, String end, String[] uris, Boolean unique) {
        LocalDateTime from;
        LocalDateTime to;
        try {
            from = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
            to = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e);
        }
        List<HitListElementDto> hitListElementDtos;
        if (unique) {
            hitListElementDtos = statRepository.getHitListElementDtosDistinctIp(from, to, uris);
        } else {
            hitListElementDtos = statRepository.getHitListElementDtos(from, to, uris);
        }
        return hitListElementDtos;
    }
}
