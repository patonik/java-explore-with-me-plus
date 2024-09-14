package ru.practicum.dto.compilation;

import lombok.Data;
import ru.practicum.dto.event.EventShortDto;

import java.util.Set;

@Data
public class CompilationDto {
    private Long id;
    private Set<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
