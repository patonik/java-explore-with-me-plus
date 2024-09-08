package ru.practicum.dto.event.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.Request;

@Mapper(componentModel = "spring")
public interface RequestDtoMapper {
    @Mapping(target = "id", ignore = true)
    Request toRequest(ParticipationRequestDto requestDto);
}
