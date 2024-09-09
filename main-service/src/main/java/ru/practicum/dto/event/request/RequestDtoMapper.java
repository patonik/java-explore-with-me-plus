package ru.practicum.dto.event.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.Request;

@Mapper(componentModel = "spring")
public interface RequestDtoMapper {
    @Mapping(source = "requester.id", target = "requester")
    ParticipationRequestDto toParticipationRequestDto(Request request);
}
