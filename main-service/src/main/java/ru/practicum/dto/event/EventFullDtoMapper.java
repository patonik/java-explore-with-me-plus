package ru.practicum.dto.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.model.Event;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventFullDtoMapper {
    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    EventFullDto toDto(Event event, Long confirmedRequests);
}
