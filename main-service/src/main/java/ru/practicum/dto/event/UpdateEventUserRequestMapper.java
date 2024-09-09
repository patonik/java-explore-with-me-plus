package ru.practicum.dto.event;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ru.practicum.model.Event;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UpdateEventUserRequestMapper {
    Event updateEvent(UpdateEventUserRequest updateEventUserRequest, @MappingTarget Event event);
}
