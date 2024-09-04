package ru.practicum.dto;

import org.mapstruct.Mapper;
import ru.practicum.model.ServiceHit;

@Mapper
public interface ServiceHitMapper {
    ServiceHitDto toDto(ServiceHit serviceHit);

    ServiceHit toEntity(ServiceHitDto serviceHitDto);
}
