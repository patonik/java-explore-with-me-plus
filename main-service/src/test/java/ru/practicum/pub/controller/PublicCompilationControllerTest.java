package ru.practicum.pub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.pub.service.PublicCompilationService;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicCompilationController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class PublicCompilationControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private PublicCompilationService publicCompilationService;

    @InjectMocks
    private PublicCompilationController publicCompilationController;


    private CompilationDto compilationDto;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Sample CompilationDto for testing
        compilationDto = CompilationDto.builder()
            .id(1L)
            .title("Sample Compilation")
            .pinned(true)
            .events(Set.of()) // Empty set for now
            .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getCompilations_Success() throws Exception {
        // Mock the service call
        when(publicCompilationService.getCompilations(anyBoolean(), anyInt(), anyInt()))
            .thenReturn(List.of(compilationDto));

        // Perform GET request
        mockMvc.perform(get("/compilations")
                .param("pinned", "true")
                .param("from", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].title").value("Sample Compilation"))
            .andExpect(jsonPath("$[0].pinned").value(true));
    }

    @Test
    void getCompilation_Success() throws Exception {
        // Mock the service call
        when(publicCompilationService.getCompilation(anyLong())).thenReturn(compilationDto);

        // Perform GET request for a specific compilation by id
        mockMvc.perform(get("/compilations/{compId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Sample Compilation"))
            .andExpect(jsonPath("$.pinned").value(true));
    }
}
