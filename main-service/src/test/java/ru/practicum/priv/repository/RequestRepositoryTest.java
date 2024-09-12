package ru.practicum.priv.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;
import ru.practicum.admin.repository.AdminUserRepository;
import ru.practicum.dto.event.Location;
import ru.practicum.dto.event.State;
import ru.practicum.dto.event.request.Status;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Profile("test")
public class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private AdminUserRepository userRepository;

    @Autowired
    private PrivateEventRepository eventRepository;

    @Test
    public void testSaveRequest() {
        User save = userRepository.save(new User(1L, "Alice", "EMAIL@ya.ru"));

        var event = new Event();
        event.setId(1L);
        event.setAnnotation("annotation");
        event.setDescription("description");
        event.setEventDate(LocalDateTime.now());
        event.setPaid(false);
        event.setParticipantLimit(5);
        event.setRequestModeration(false);
        event.setState(State.PUBLISHED);
        event.setLocation(new Location(555.0, 555.0));
        event.setTitle("Some Event Title");
        event.setInitiator(save);
        eventRepository.save(event);

        var user = userRepository.findById(1L).orElseThrow(RuntimeException::new);
        eventRepository.findById(1L).orElseThrow(RuntimeException::new);

        var request = new Request(null, LocalDateTime.now(), user, event, Status.CONFIRMED);
        var savedRequest = requestRepository.save(request);

        assertNotNull(savedRequest);
        assertNotNull(savedRequest.getId());
    }
}