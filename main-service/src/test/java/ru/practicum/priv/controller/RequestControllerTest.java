package ru.practicum.priv.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.dto.event.request.ParticipationRequestDto;
import ru.practicum.dto.event.request.Status;
import ru.practicum.priv.service.PrivateRequestServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrivateRequestServiceImpl requestService;

    private ParticipationRequestDto participationRequestDto;

    @BeforeEach
    public void setup() {
        participationRequestDto = ParticipationRequestDto.builder()
                .id(1L)
                .requester(1L)
                .status(Status.PENDING)
                .build();

        Mockito.when(requestService.getMyRequests(anyLong()))
                .thenReturn(List.of(participationRequestDto));
        Mockito.when(requestService.addMyRequest(anyLong(), anyLong()))
                .thenReturn(participationRequestDto);
        Mockito.when(requestService.cancelMyRequest(anyLong(), anyLong()))
                .thenReturn(participationRequestDto);
    }

    @Test
    public void testGetMyRequests_whenUserIdIsValid_thenStatusIsOkAndLengthIsNotEmpty() throws Exception {
        mockMvc.perform(get("/users/{userId}/requests", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(participationRequestDto.getId()));

        Mockito.verify(requestService, Mockito.times(1)).getMyRequests(anyLong());
    }

    @Test
    public void testGetMyRequests_whenUserIdIsUnknown_thenStatusIsOkAndListIsEmpty() throws Exception {
        Mockito.when(requestService.getMyRequests(99L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/{userId}/requests", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));

        Mockito.verify(requestService, Mockito.times(1)).getMyRequests(anyLong());
    }

    @Test
    public void testAddMyRequest_whenUserIdAndEventIdAreValid_thenStatusIsOkAndRequestIsAdded() throws Exception {
        mockMvc.perform(post("/users/{userId}/requests", 1L)
                        .param("eventId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(participationRequestDto.getId()));

        Mockito.verify(requestService, Mockito.times(1)).addMyRequest(anyLong(), anyLong());
    }

    @Test
    public void testCancelMyRequest_whenUserIdAndRequestIdAreValid_thenStatusIsOkAndRequestIsCanceled() throws Exception {
        mockMvc.perform(patch("/users/{userId}/requests/{requestId}/cancel", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("PENDING"));

        Mockito.verify(requestService, Mockito.times(1)).cancelMyRequest(anyLong(), anyLong());
    }
}