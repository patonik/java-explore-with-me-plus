package ru.practicum.priv.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.HttpStatsClient;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventFullDtoMapper;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventShortDtoMapper;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.NewEventDtoMapper;
import ru.practicum.dto.event.State;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.event.UpdateEventUserRequestMapper;
import ru.practicum.dto.event.request.RequestCount;
import ru.practicum.dto.event.request.Status;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.priv.repository.PrivateCategoryRepository;
import ru.practicum.priv.repository.PrivateEventRepository;
import ru.practicum.priv.repository.PrivateUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PrivateEventServiceTest {

    @Mock
    private PrivateEventRepository privateEventRepository;

    @Mock
    private PrivateUserRepository privateUserRepository;

    @Mock
    private PrivateCategoryRepository privateCategoryRepository;

    @Mock
    private NewEventDtoMapper newEventDtoMapper;

    @Mock
    private EventFullDtoMapper eventFullDtoMapper;

    @Mock
    private EventShortDtoMapper eventShortDtoMapper;

    @Mock
    private HttpStatsClient httpStatsClient;

    @InjectMocks
    private PrivateEventService privateEventService;

    private User user;
    private Category category;
    private Event event;
    private AutoCloseable closeable;
    @Mock
    private UpdateEventUserRequestMapper updateEventUserRequestMapper;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        user = new User(1L, "Test User", "test@example.com");
        category = new Category(1L, "Test Category");
        event = new Event(1L, "Test Annotation", category, LocalDateTime.now(), null, "Test Description",
            LocalDateTime.now().plusDays(1), user, null, false, 10, true,
            "Test Event", State.PENDING);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testAddEvent_Success() {
        NewEventDto newEventDto = new NewEventDto("Test Event", 1L, "Test Description", LocalDateTime.now().plusDays(1),
            new ru.practicum.dto.event.Location(0.0, 0.0), false, 10, true, "Test Title");

        when(privateUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(privateCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(newEventDtoMapper.toEvent(any(NewEventDto.class), eq(user), eq(category), eq(State.PENDING)))
            .thenReturn(event);
        when(privateEventRepository.save(event)).thenReturn(event);
        when(eventFullDtoMapper.toDto(eq(event), eq(0L), eq(0L))).thenReturn(new EventFullDto());

        EventFullDto result = privateEventService.addEvent(1L, newEventDto);

        assertNotNull(result);
        verify(privateEventRepository).save(event);
    }

    @Test
    void testGetMyEvents_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        when(privateEventRepository.findAllByInitiatorIdOrderByCreatedOnAsc(1L, pageable)).thenReturn(List.of(event));
        when(httpStatsClient.getStats(anyString(), anyString(), anyList(), eq(false)))
            .thenReturn(List.of(new StatResponseDto("main", "/events/1", 10L)));

        when(eventShortDtoMapper.toDto(any(Event.class), eq(10L)))
            .thenReturn(new EventShortDto(1L, "Test Annotation", null, 10L, LocalDateTime.now(), LocalDateTime.now(),
                null, false, "Test Event", 10L));

        List<EventShortDto> events = privateEventService.getMyEvents(1L, 0, 10);

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(10L, events.getFirst().getViews());
    }

    @Test
    void testGetMyEvent_NotFound() {
        when(privateEventRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
            privateEventService.getMyEvent(1L, 1L)
        );
        assertEquals("Event not found: 1", exception.getMessage());
    }

    @Test
    void testUpdateMyEvent_Success() {
        // Given
        UpdateEventUserRequest updateEventUserRequest =
            new UpdateEventUserRequest("Updated Annotation", 1L, "Updated Description", LocalDateTime.now().plusDays(2),
                new ru.practicum.dto.event.Location(0.0, 0.0), false, 10, true,
                ru.practicum.dto.event.UserStateAction.SEND_TO_REVIEW, "test title");

        when(privateEventRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.of(event));
        when(privateCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(updateEventUserRequestMapper.updateEvent(eq(updateEventUserRequest), eq(event), eq(category)))
            .thenReturn(event);
        when(privateEventRepository.save(event)).thenReturn(event);
        when(privateEventRepository.getRequestCountByEventAndStatus(1L, Status.CONFIRMED))
            .thenReturn(new RequestCount(10L));
        when(httpStatsClient.getStats(any(), any(), any(), any())).thenReturn(
            List.of(new StatResponseDto("", "", 10L)));
        when(eventFullDtoMapper.toDto(any(Event.class), anyLong(), anyLong())).thenReturn(new EventFullDto());

        // When
        EventFullDto result = privateEventService.updateMyEvent(1L, 1L, updateEventUserRequest);

        // Then
        assertNotNull(result);
        verify(privateEventRepository).save(event);
        verify(eventFullDtoMapper).toDto(eq(event), eq(10L), eq(10L));
    }

    @Test
    void testUpdateMyEvent_ConflictWhenPublished() {
        event.setState(State.PUBLISHED);

        when(privateEventRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.of(event));
        when(privateCategoryRepository.findById(1L)).thenReturn(Optional.ofNullable(category));
        UpdateEventUserRequest updateEventUserRequest = new UpdateEventUserRequest();
        updateEventUserRequest.setCategoryId(1L);
        ConflictException exception = assertThrows(ConflictException.class, () ->
            privateEventService.updateMyEvent(1L, 1L, updateEventUserRequest)
        );
        assertEquals("You can't update a new event: 1", exception.getMessage());
    }
}
