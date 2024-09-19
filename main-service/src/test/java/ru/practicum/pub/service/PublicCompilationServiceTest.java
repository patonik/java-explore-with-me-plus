package ru.practicum.pub.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.HttpStatsClient;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationDtoMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.pub.repository.PublicCompilationRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PublicCompilationServiceTest {

    @Mock
    private PublicCompilationRepository publicCompilationRepository;

    @Mock
    private HttpStatsClient httpStatsClient;

    @Mock
    private CompilationDtoMapper compilationDtoMapper;

    @InjectMocks
    private PublicCompilationService publicCompilationService;

    private Compilation compilation;
    private CompilationDto compilationDto;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Sample compilation for testing
        compilation = new Compilation(1L, true, "Sample Compilation", Set.of());
        compilationDto =
                CompilationDto.builder().id(1L).title("Sample Compilation").pinned(true).events(Set.of()).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getCompilations_Success() {
        // Mock repository method to return compilation DTO
        List<Compilation> comps = List.of(compilation);
        when(publicCompilationRepository.findAllCompilations(anyBoolean(), any())).thenReturn(
                comps);
        List<CompilationDto> compDtos = List.of(compilationDto);
        when(compilationDtoMapper.toCompilationDtoList(comps)).thenReturn(compDtos);
        doNothing().when(publicCompilationRepository).populateEventShortDtos(anySet(), any());

        // Call the service method
        List<CompilationDto> compilations = publicCompilationService.getCompilations(true, 0, 10);

        // Verify the result
        assertNotNull(compilations);
        assertEquals(1, compilations.size());
        assertEquals("Sample Compilation", compilations.getFirst().getTitle());
        verify(publicCompilationRepository).findAllCompilations(anyBoolean(), any());
    }

    @Test
    void getCompilation_Success() {
        // Mock repository and mapper
        when(publicCompilationRepository.findById(anyLong())).thenReturn(Optional.of(compilation));
        when(compilationDtoMapper.toCompilationDto(any(Compilation.class))).thenReturn(compilationDto);

        // Call the service method
        CompilationDto result = publicCompilationService.getCompilation(1L);

        // Verify the result
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sample Compilation", result.getTitle());
        verify(publicCompilationRepository).findById(anyLong());
        verify(compilationDtoMapper).toCompilationDto(any(Compilation.class));
    }

    @Test
    void getCompilation_NotFound() {
        // Mock repository to return empty Optional
        when(publicCompilationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Call the service method and expect NotFoundException
        assertThrows(NotFoundException.class, () -> publicCompilationService.getCompilation(1L));

        verify(publicCompilationRepository).findById(anyLong());
    }
}
