package ru.practicum.admin.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.admin.repository.AdminCompilationRepository;
import ru.practicum.admin.repository.AdminEventRepository;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationDtoMapper;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminCompilationServiceTest {

    @Mock
    private AdminCompilationRepository adminCompilationRepository;

    @Mock
    private AdminEventRepository adminEventRepository;

    @Mock
    private CompilationDtoMapper compilationDtoMapper;

    @InjectMocks
    private AdminCompilationService adminCompilationService;

    private Compilation compilation;
    private NewCompilationDto newCompilationDto;
    private UpdateCompilationRequest updateCompilationRequest;
    private Event event;
    private CompilationDto compilationDto;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Setup basic test data
        event = new Event();
        event.setId(1L);

        compilation = new Compilation(1L, true, "Compilation title", new HashSet<>());
        compilation.getEvents().add(event);

        newCompilationDto = NewCompilationDto.builder()
            .title("New Compilation")
            .eventIds(Set.of(1L))
            .pinned(true)
            .build();

        updateCompilationRequest = new UpdateCompilationRequest();
        updateCompilationRequest.setTitle("Updated title");
        updateCompilationRequest.setEventIds(Set.of(1L));
        updateCompilationRequest.setPinned(false);

        compilationDto = CompilationDto.builder().id(1L).title("Compilation title").build();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void addCompilation_Success() {
        // Mock successful event fetching and saving compilation
        when(adminEventRepository.findAllByIdIn(anySet())).thenReturn(Set.of(event));
        when(adminCompilationRepository.save(any(Compilation.class))).thenReturn(compilation);
        when(compilationDtoMapper.toCompilationDto(any(Compilation.class))).thenReturn(compilationDto);

        // Call the method
        CompilationDto result = adminCompilationService.addCompilation(newCompilationDto);

        // Verify the interactions and assert the result
        assertNotNull(result);
        assertEquals("Compilation title", result.getTitle());
        verify(adminEventRepository).findAllByIdIn(anySet());
        verify(adminCompilationRepository).save(any(Compilation.class));
        verify(compilationDtoMapper).toCompilationDto(any(Compilation.class));
    }

    @Test
    void addCompilation_EventNotFound() {
        // Mock event not found
        when(adminEventRepository.findAllByIdIn(anySet())).thenReturn(new HashSet<>());

        // Call the method and expect ConflictException
        assertThrows(ConflictException.class, () -> adminCompilationService.addCompilation(newCompilationDto));

        verify(adminEventRepository).findAllByIdIn(anySet());
        verify(adminCompilationRepository, never()).save(any(Compilation.class));
    }

    @Test
    void deleteCompilation_Success() {
        // Mock successful existence check and deletion
        when(adminCompilationRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(adminCompilationRepository).deleteById(anyLong());

        // Call the method
        adminCompilationService.deleteCompilation(1L);

        // Verify interactions
        verify(adminCompilationRepository).existsById(anyLong());
        verify(adminCompilationRepository).deleteById(anyLong());
    }

    @Test
    void deleteCompilation_NotFound() {
        // Mock compilation not found
        when(adminCompilationRepository.existsById(anyLong())).thenReturn(false);

        // Call the method and expect NotFoundException
        assertThrows(NotFoundException.class, () -> adminCompilationService.deleteCompilation(1L));

        verify(adminCompilationRepository).existsById(anyLong());
        verify(adminCompilationRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateCompilation_Success() {
        // Mock finding compilation and saving the updated one
        when(adminCompilationRepository.findById(anyLong())).thenReturn(Optional.of(compilation));
        when(adminCompilationRepository.save(any(Compilation.class))).thenReturn(compilation);
        when(compilationDtoMapper.updateCompilation(any(), any())).thenReturn(compilation);
        when(compilationDtoMapper.toCompilationDto(any(Compilation.class))).thenReturn(compilationDto);

        // Call the method
        CompilationDto result = adminCompilationService.updateCompilation(1L, updateCompilationRequest);

        // Verify interactions and assert the result
        assertNotNull(result);
        assertEquals("Compilation title", result.getTitle());
        verify(adminCompilationRepository).findById(anyLong());
        verify(adminCompilationRepository).save(any(Compilation.class));
        verify(compilationDtoMapper).toCompilationDto(any(Compilation.class));
    }

    @Test
    void updateCompilation_NotFound() {
        // Mock compilation not found
        when(adminCompilationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Call the method and expect NotFoundException
        assertThrows(NotFoundException.class,
            () -> adminCompilationService.updateCompilation(1L, updateCompilationRequest));

        verify(adminCompilationRepository).findById(anyLong());
        verify(adminCompilationRepository, never()).save(any(Compilation.class));
    }
}
