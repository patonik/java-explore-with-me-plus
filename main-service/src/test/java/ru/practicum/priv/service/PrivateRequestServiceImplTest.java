package ru.practicum.priv.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import ru.practicum.dto.event.State;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.priv.repository.PrivateEventRepository;
import ru.practicum.priv.repository.PrivateUserRepository;
import ru.practicum.priv.repository.RequestRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Profile("test")
public class PrivateRequestServiceImplTest {

    @Autowired
    private PrivateRequestService requestService;

    @MockBean
    private RequestRepository requestRepository;

    @MockBean
    private PrivateEventRepository eventRepository;

    @MockBean
    private PrivateUserRepository userRepository;

    @Test
    public void addMyRequest_shouldThrowNotFoundException_whenUserNotFound() {
        var userId = 1L;
        var eventId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.addMyRequest(userId, eventId));
    }

    @Test
    public void addMyRequest_whenEventNotFound_thenShouldThrowNotFoundException() {
        var userId = 1L;
        var eventId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User(userId, "Alice", "EMAIL@ya.ru")));
        when(eventRepository.findById(eventId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.addMyRequest(userId, eventId));
    }

    @Test
    public void addMyRequest_whenRequestAlreadyExists_thenShouldThrowConflictException() {
        var userId = 1L;
        var eventId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(new Event()));
        when(requestRepository.findByRequesterIdAndEventId(userId, eventId)).thenReturn(Optional.of(new Request()));

        assertThrows(ConflictException.class, () -> requestService.addMyRequest(userId, eventId));
    }

    @Test
    public void addMyRequest_whenUserIsInitiatorOfEvent_thenShouldThrowConflictException() {
        var userId = 1L;
        var eventId = 1L;
        var event = mock(Event.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(requestRepository.findByRequesterIdAndEventId(userId, eventId)).thenReturn(Optional.of(new Request()));
        when(eventRepository.findByIdAndInitiatorId(eventId, userId)).thenReturn(List.of(event));

        assertThrows(ConflictException.class, () -> requestService.addMyRequest(userId, eventId));
    }

    @Test
    public void addMyRequest_whenEventUnpublished_thenShouldThrowConflictException() {
        var userId = 1L;
        var eventId = 1L;
        var event = mock(Event.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(requestRepository.findByRequesterIdAndEventId(userId, eventId)).thenReturn(Optional.of(new Request()));
        when(eventRepository.findByIdAndInitiatorId(eventId, userId)).thenReturn(List.of());
        when(event.getState()).thenReturn(State.PUBLISHED);

        assertThrows(ConflictException.class, () -> requestService.addMyRequest(userId, eventId));
    }

    @Test
    public void addMyRequest_whenParticipantLimitIsReached_thenShouldThrowConflictException() {
        Long userId = 1L;
        Long eventId = 1L;
        Event event = mock(Event.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(requestRepository.findByRequesterIdAndEventId(userId, eventId)).thenReturn(Optional.of(new Request()));
        when(eventRepository.findByIdAndInitiatorId(eventId, userId)).thenReturn(List.of());
        when(event.getState()).thenReturn(State.PENDING);
        when(event.getParticipantLimit()).thenReturn(1);
        when(requestRepository.countByEventId(eventId)).thenReturn(1);

        assertThrows(ConflictException.class, () -> requestService.addMyRequest(userId, eventId));
    }
}