package ru.practicum.admin.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.HttpStatsClient;
import ru.practicum.admin.repository.AdminCategoryRepository;
import ru.practicum.admin.repository.AdminEventRepository;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.dto.event.AdminStateAction;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventFullDtoMapper;
import ru.practicum.dto.event.State;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventAdminRequestMapper;
import ru.practicum.dto.event.request.RequestCount;
import ru.practicum.dto.event.request.Status;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminEventServiceTest {

    @Mock
    private AdminEventRepository adminEventRepository;
    @Mock
    private HttpStatsClient httpStatsClient;

    @Mock
    private AdminCategoryRepository categoryRepository;

    @Mock
    private UpdateEventAdminRequestMapper updateEventAdminRequestMapper;

    @Mock
    private EventFullDtoMapper eventFullDtoMapper;

    @InjectMocks
    private AdminEventService adminEventService;

    private Event event;
    private UpdateEventAdminRequest adminRequest;
    private Category category;
    private EventFullDto eventFullDto;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        closeable = MockitoAnnotations.openMocks(this);

        // Set up test data
        event = new Event();
        event.setId(1L);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);

        category = new Category();
        category.setId(1L);

        adminRequest = new UpdateEventAdminRequest();
        adminRequest.setCategoryId(1L);
        adminRequest.setStateAction(AdminStateAction.REJECT_EVENT);

        eventFullDto = new EventFullDto();
        eventFullDto.setCreatedOn(event.getCreatedOn());
        event.setId(event.getId());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getEvents_Success() {
        // Prepare data
        Long[] users = {1L, 2L};
        String[] states = {"PUBLISHED", "PENDING"};
        Long[] categories = {1L, 2L};
        LocalDateTime rangeStart = LocalDateTime.now().minusDays(1);
        LocalDateTime rangeEnd = LocalDateTime.now();
        int from = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(from, size);
        when(adminEventRepository.getEvents(any(), any(), any(), any(), any(), eq(pageable)))
            .thenReturn(List.of(eventFullDto));
        when(httpStatsClient.getStats(any(), any(), any(), any())).thenReturn(
            List.of(new StatResponseDto("", "", 10L)));

        // Call the method
        List<EventFullDto> result =
            adminEventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);

        // Verify the result and interactions
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(adminEventRepository).getEvents(any(), any(), any(), any(), any(), eq(pageable));
    }

    @Test
    void updateEvent_Success() {
        // Prepare mock interactions
        when(adminEventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(updateEventAdminRequestMapper.updateEvent(any(), any(), any())).thenReturn(event);
        when(adminEventRepository.save(event)).thenReturn(event);
        when(adminEventRepository.getRequestCountByEventAndStatus(anyLong(), eq(Status.CONFIRMED)))
            .thenReturn(new RequestCount(100L));
        when(eventFullDtoMapper.toDto(eq(event), anyLong(), anyLong())).thenReturn(eventFullDto);
        when(httpStatsClient.getStats(any(), any(), any(), any())).thenReturn(
            List.of(new StatResponseDto("", "", 10L)));

        // Call the method
        EventFullDto result = adminEventService.updateEvent(1L, adminRequest);
        result.setState(event.getState());

        // Verify the result and interactions
        assertNotNull(result);
        AdminStateAction stateAction = adminRequest.getStateAction();
        State state = result.getState();
        assertTrue(stateAction.equals(AdminStateAction.REJECT_EVENT) ? state.equals(State.CANCELED) :
            state.equals(State.PUBLISHED));
        verify(adminEventRepository).findById(anyLong());
        verify(categoryRepository).findById(anyLong());
        verify(updateEventAdminRequestMapper).updateEvent(any(), any(), any());
        verify(adminEventRepository).save(any(Event.class));
        verify(adminEventRepository).getRequestCountByEventAndStatus(anyLong(), eq(Status.CONFIRMED));
        verify(eventFullDtoMapper).toDto(any(Event.class), anyLong(), anyLong());
    }

    @Test
    void updateEvent_EventNotFound() {
        // Mock event not found scenario
        when(adminEventRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Expect NotFoundException
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> adminEventService.updateEvent(1L, adminRequest));

        assertEquals("Event not found", exception.getMessage());
        verify(adminEventRepository).findById(anyLong());
        verify(categoryRepository, never()).findById(anyLong());
        verify(adminEventRepository, never()).save(any(Event.class));
    }

    @Test
    void updateEvent_CategoryNotFound() {
        // Mock event found but category not found scenario
        when(adminEventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Expect NotFoundException
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> adminEventService.updateEvent(1L, adminRequest));

        assertEquals("Category not found", exception.getMessage());
        verify(adminEventRepository).findById(anyLong());
        verify(categoryRepository).findById(anyLong());
        verify(adminEventRepository, never()).save(any(Event.class));
    }
}
