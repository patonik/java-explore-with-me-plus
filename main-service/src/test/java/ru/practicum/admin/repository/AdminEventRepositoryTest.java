package ru.practicum.admin.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.State;
import ru.practicum.dto.event.request.RequestCount;
import ru.practicum.dto.event.request.Status;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.priv.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class AdminEventRepositoryTest {
    private final AdminEventRepository adminEventRepository;
    private final AdminUserRepository adminUserRepository;
    private final AdminCategoryRepository adminCategoryRepository;
    private final RequestRepository requestRepository;
    private Event event;
    private User user;
    private Category category;
    private Long id;
    private static long counter = 1;
    private String title;

    @BeforeEach
    void setUp() {
        id = counter++;
        user = new User(null, "Test User %d".formatted(id), "test%d@example.com".formatted(id));
        user = adminUserRepository.save(user);
        System.out.printf("initiator id: %d%n", user.getId());
        Long requestId = counter++;
        User requester =
            new User(null, "Test User %d".formatted(requestId), "test%d@example.com".formatted(requestId));
        requester = adminUserRepository.save(requester);
        System.out.printf("requester id: %d%n", requester.getId());
        Long categoryId = counter++ * 16;
        category = new Category(null, "Test Category %d".formatted(categoryId));
        category = adminCategoryRepository.save(category);
        System.out.printf("category id: %d%n", category.getId());
        title = "Test Event%d".formatted(id);
        event = Event.builder()
            .annotation("Test Annotation%d".formatted(id))
            .category(category)
            .createdOn(LocalDateTime.now())
            .description("Test Description%d".formatted(id))
            .eventDate(LocalDateTime.now().plusDays(1))
            .initiator(user)
            .location(new ru.practicum.dto.event.Location(0.0, 0.0))
            .paid(false)
            .participantLimit(10)
            .requestModeration(true)
            .title(title)
            .state(State.PENDING)
            .build();
        event = adminEventRepository.save(event);
        System.out.printf("event id: %d%n", event.getId());
        Request request = new Request(null, LocalDateTime.now(), requester, event, Status.CONFIRMED);
        request = requestRepository.save(request);
        System.out.printf("request id: %d%n", request.getId());
    }

    @Test
    void testGetRequestCountByEventAndStatus() {
        // This assumes that there is a 'Request' entity with a relation to 'Event'.
        RequestCount requestCount =
            adminEventRepository.getRequestCountByEventAndStatus(event.getId(), Status.CONFIRMED);
        assertThat(requestCount).isNotNull();
        assertThat(requestCount.getConfirmedRequests()).isEqualTo(
            1L);  // Replace with expected count based on data setup
    }

    @Test
    void testFindEventById() {
        Optional<Event> foundEvent = adminEventRepository.findById(event.getId());

        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getId()).isEqualTo(event.getId());
        assertThat(foundEvent.get().getTitle()).isEqualTo("Test Event%d".formatted(id));
    }

    @Test
    void testGetEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EventFullDto> eventFullDtos = adminEventRepository.getEventsOrderedById(
            new Long[] {user.getId()},
            new String[] {State.PENDING.name()},
            new Long[] {category.getId()},
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(2),
            pageable
        );

        assertThat(eventFullDtos).hasSize(1);
        System.out.println(eventFullDtos.getFirst().getCategory().getId());
        assertThat(eventFullDtos.getFirst().getTitle()).isEqualTo(title);
    }

    @Test
    void save() {
        event.setState(State.CANCELED);
        assertEquals(event.getState(), adminEventRepository.save(event).getState());
    }
}
