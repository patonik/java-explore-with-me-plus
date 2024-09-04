package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.dto.ServiceHitDto;
import ru.practicum.model.ServiceHit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceHitMapper {
    ServiceHitDto toDto(ServiceHit serviceHit);

    ServiceHit toEntity(ServiceHitDto serviceHitDto);
}
