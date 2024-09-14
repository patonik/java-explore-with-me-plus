package ru.practicum.admin.controller;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.admin.service.AdminCompilationService;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCompilationController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AdminCompilationControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private AdminCompilationService adminCompilationService;
    @InjectMocks
    private AdminCompilationController adminCompilationController;


    private CompilationDto compilationDto;
    AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Sample CompilationDto object for testing
        compilationDto = CompilationDto.builder()
            .id(1L)
            .title("Sample Compilation")
            .pinned(true)
            .events(new HashSet<>())
            .build();
    }

    @AfterEach
    void tearDown() throws Exception {

    }

    @Test
    void addCompilation_Success() throws Exception {
        // Mock the service call
        when(adminCompilationService.addCompilation(any(NewCompilationDto.class)))
            .thenReturn(compilationDto);

        NewCompilationDto newCompilationDto = NewCompilationDto.builder()
            .title("Sample Compilation")
            .pinned(true)
            .eventIds(new HashSet<>())
            .build();

        // Perform POST request
        mockMvc.perform(post("/admin/compilations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCompilationDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Sample Compilation"))
            .andExpect(jsonPath("$.pinned").value(true))
            .andExpect(jsonPath("$.events").isArray());
    }

    @Test
    void deleteCompilation_Success() throws Exception {
        // Mock the service call
        doNothing().when(adminCompilationService).deleteCompilation(anyLong());

        // Perform DELETE request
        mockMvc.perform(delete("/admin/compilations/{compId}", 1L))
            .andExpect(status().isNoContent());
    }

    @Test
    void updateCompilation_Success() throws Exception {
        // Mock the service call
        when(adminCompilationService.updateCompilation(anyLong(), any(UpdateCompilationRequest.class)))
            .thenReturn(compilationDto);

        UpdateCompilationRequest updateCompilationRequest = new UpdateCompilationRequest();
        updateCompilationRequest.setTitle("Updated Compilation");
        updateCompilationRequest.setPinned(false);
        updateCompilationRequest.setEventIds(new HashSet<>());

        // Perform PATCH request
        mockMvc.perform(patch("/admin/compilations/{compId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCompilationRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Sample Compilation"))
            .andExpect(jsonPath("$.pinned").value(true));
    }
}
