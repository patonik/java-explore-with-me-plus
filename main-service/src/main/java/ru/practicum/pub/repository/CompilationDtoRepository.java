package ru.practicum.pub.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.compilation.CompilationDto;

import java.util.List;

public interface CompilationDtoRepository {
    List<CompilationDto> findAllCompilationDtos(Boolean pinned, Pageable pageable);
}
