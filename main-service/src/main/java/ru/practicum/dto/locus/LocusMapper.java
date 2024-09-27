package ru.practicum.dto.locus;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.model.Locus;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LocusMapper {
    Locus toLocus(NewLocusDto newLocusDto);

    @Mapping(target = "id", ignore = true)
    Locus updateLocus(@MappingTarget Locus locus, LocusUpdateDto locusUpdateDto);
}
