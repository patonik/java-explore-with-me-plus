package ru.practicum.priv.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.Location;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.State;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.event.UserStateAction;
import ru.practicum.dto.event.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.request.ParticipationRequestDto;
import ru.practicum.priv.service.PrivateEventService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PrivateEventController.class)
class PrivateEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PrivateEventService privateEventService;

    private EventFullDto eventFullDto;
    private EventShortDto eventShortDto;
    private NewEventDto newEventDto;

    @BeforeEach
    void setUp() {
        eventFullDto =
            new EventFullDto(1L,
                "pff",
                new CategoryDto(),
                LocalDateTime.now(),
                null,
                "pff",
                null,
                null,
                null,
                false,
                10,
                false,
                "pff",
                State.PENDING,
                3L, null);  // Initialize EventFullDto with your fields
        eventShortDto = new EventShortDto();  // Initialize EventShortDto with your fields
        newEventDto = new NewEventDto();  // Initialize NewEventDto with your fields
        LocalDateTime eventDate = LocalDateTime.now().plusDays(1);
        newEventDto.setEventDate(eventDate.truncatedTo(ChronoUnit.SECONDS));
        newEventDto.setAnnotation("test annotation test annotation");
        newEventDto.setDescription("description test annotation test annotation");
        newEventDto.setLocation(new Location(0.00, 0.00));
        newEventDto.setTitle("test title");
        newEventDto.setCategoryId(1L);
    }

    @Test
    void testGetMyEvents() throws Exception {
        List<EventShortDto> eventShortDtos = Collections.singletonList(eventShortDto);

        when(privateEventService.getMyEvents(1L, 0, 10)).thenReturn(eventShortDtos);

        mockMvc.perform(get("/users/1/events")
                .param("from", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray());

        verify(privateEventService, times(1)).getMyEvents(1L, 0, 10);
    }

    @Test
    void testAddEvent() throws Exception {
        System.out.println(newEventDto);
        when(privateEventService.addEvent(1L, newEventDto)).thenReturn(eventFullDto);
        mockMvc.perform(post("/users/1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEventDto)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists());

        verify(privateEventService, times(1)).addEvent(1L, newEventDto);
    }

    @Test
    void testGetMyEvent() throws Exception {
        when(privateEventService.getMyEvent(1L, 1L)).thenReturn(eventFullDto);

        mockMvc.perform(get("/users/1/events/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists());

        verify(privateEventService, times(1)).getMyEvent(1L, 1L);
    }

    @Test
    void testUpdateMyEvent() throws Exception {
        UpdateEventUserRequest updateEventUserRequest =
            new UpdateEventUserRequest("01234567890123456789",
                1L, "01234567890123456789",
                LocalDateTime.now().plusDays(1),
                new Location(0.0, 0.0),
                false,
                10,
                false,
                UserStateAction.SEND_TO_REVIEW, "0123456789");  // Initialize with fields

        when(privateEventService.updateMyEvent(1L, 1L, updateEventUserRequest)).thenReturn(eventFullDto);

        mockMvc.perform(patch("/users/1/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateEventUserRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists());

        verify(privateEventService, times(1)).updateMyEvent(1L, 1L, updateEventUserRequest);
    }

    @Test
    void testGetMyEventRequests() throws Exception {
        List<ParticipationRequestDto> requestDtos = List.of(new ParticipationRequestDto());  // Populate list

        when(privateEventService.getMyEventRequests(1L, 1L)).thenReturn(requestDtos);

        mockMvc.perform(get("/users/1/events/1/requests"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray());

        verify(privateEventService, times(1)).getMyEventRequests(1L, 1L);
    }

    @Test
    void testUpdateMyEventRequests() throws Exception {
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();  // Initialize fields
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();  // Initialize result

        when(privateEventService.updateMyEventRequests(1L, 1L, updateRequest)).thenReturn(result);

        mockMvc.perform(patch("/users/1/events/1/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists());

        verify(privateEventService, times(1)).updateMyEventRequests(1L, 1L, updateRequest);
    }
}
