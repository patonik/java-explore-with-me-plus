package ru.practicum.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.admin.repository.AdminCompilationRepository;
import ru.practicum.dto.compilation.CompilationDto;

@Service
@RequiredArgsConstructor
public class AdminCompilationService {
    private final AdminCompilationRepository adminCompilationRepository;

    public CompilationDto addCompilation(CompilationDto compilationDto) {

        return null;
    }

    public CompilationDto deleteCompilation(Long compId) {
        return null;
    }

    public CompilationDto updateCompilation(Long compId, CompilationDto compilationDto) {
        return null;
    }
}
