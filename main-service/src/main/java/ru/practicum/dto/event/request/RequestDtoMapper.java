package ru.practicum.dto.event.request;

import org.mapstruct.Mapper;
import ru.practicum.model.Request;

@Mapper(componentModel = "spring")
public interface RequestDtoMapper {
    ParticipationRequestDto toParticipationRequestDto(Request request);
}
