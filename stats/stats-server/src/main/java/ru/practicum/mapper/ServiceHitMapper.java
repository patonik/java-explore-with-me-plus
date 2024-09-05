package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.dto.ServiceHitDto;
import ru.practicum.model.ServiceHit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceHitMapper {
    @Mapping(target = "timestamp", source = "created")
    ServiceHitDto toDto(ServiceHit serviceHit);

    @Mapping(target = "created", source = "timestamp")
    ServiceHit toEntity(ServiceHitDto serviceHitDto);
}
