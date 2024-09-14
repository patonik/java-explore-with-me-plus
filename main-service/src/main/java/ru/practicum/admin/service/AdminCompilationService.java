package ru.practicum.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.admin.repository.AdminCompilationRepository;

@Service
@RequiredArgsConstructor
public class AdminCompilationService {
    private final AdminCompilationRepository adminCompilationRepository;
}
