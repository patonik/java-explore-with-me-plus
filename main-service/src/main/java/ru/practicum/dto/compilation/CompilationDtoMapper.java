package ru.practicum.dto.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompilationDtoMapper {
    CompilationDto toCompilationDto(Compilation compilation);

    EventShortDto toEventShortDto(Event event);

    Compilation updateCompilation(UpdateCompilationRequest updateCompilationRequest,
                                  @MappingTarget Compilation compilation);
}
