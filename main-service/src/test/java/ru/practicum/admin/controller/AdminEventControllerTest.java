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
import ru.practicum.admin.service.AdminEventService;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminEventController.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AdminEventControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private AdminEventService adminEventService;
    @InjectMocks
    private AdminEventController adminEventController;

    private EventFullDto eventFullDto;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        // Sample EventFullDto object for testing
        eventFullDto = new EventFullDto();
        eventFullDto.setId(1L);
        eventFullDto.setTitle("Sample Event");
        eventFullDto.setAnnotation("This is a sample event");
        eventFullDto.setEventDate(LocalDateTime.now().plusDays(1));
        eventFullDto.setCreatedOn(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getEvents_Success() throws Exception {
        // Mock the service call
        when(adminEventService.getEvents(any(Long[].class), any(String[].class), any(Long[].class),
            any(LocalDateTime.class), any(LocalDateTime.class), any(Integer.class), any(Integer.class)))
            .thenReturn(List.of(eventFullDto));

        mockMvc.perform(get("/admin/events")
                .param("users", "1", "2")
                .param("states", "PUBLISHED", "PENDING")
                .param("categories", "1", "2")
                .param("rangeStart", "2023-09-01 00:00:00")
                .param("rangeEnd", "2023-09-10 00:00:00")
                .param("from", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].title").value("Sample Event"))
            .andExpect(jsonPath("$[0].annotation").value("This is a sample event"))
            .andExpect(jsonPath("$[0].eventDate").isNotEmpty());
    }

    @Test
    void updateEvent_Success() throws Exception {
        // Mock the service call
        when(adminEventService.updateEvent(anyLong(), any(UpdateEventAdminRequest.class)))
            .thenReturn(eventFullDto);

        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setTitle("Updated Event");

        mockMvc.perform(patch("/admin/events/{eventId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("Sample Event"))
            .andExpect(jsonPath("$.annotation").value("This is a sample event"));
    }
}
