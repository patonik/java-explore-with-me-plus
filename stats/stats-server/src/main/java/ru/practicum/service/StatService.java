package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitListElementDto;
import ru.practicum.dto.ServiceHitDto;
import ru.practicum.dto.ServiceHitMapper;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatService {
    private final StatRepository statRepository;
    private final ServiceHitMapper serviceHitMapper;

    public ServiceHitDto registerHit(ServiceHitDto serviceHitDto) {
        return serviceHitMapper.toDto(statRepository.save(serviceHitMapper.toEntity(serviceHitDto)));
    }

    public List<HitListElementDto> getHits(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        List<HitListElementDto> hitListElementDtos;
        if (unique) {
            hitListElementDtos = statRepository.getHitListElementDtosDistinctIp(start, end, uris);
        } else {
            hitListElementDtos = statRepository.getHitListElementDtos(start, end, uris);
        }
        return hitListElementDtos;
    }
}
