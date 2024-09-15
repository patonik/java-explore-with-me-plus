package ru.practicum.dto.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationDtoMapper {
    CompilationDto toCompilationDto(Compilation compilation);

    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "id", ignore = true)
    Compilation updateCompilation(UpdateCompilationRequest updateCompilationRequest,
                                  @MappingTarget Compilation compilation);
}
