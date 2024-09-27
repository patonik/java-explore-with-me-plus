package ru.practicum.priv.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.dto.event.request.RequestCount;
import ru.practicum.dto.event.request.Status;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class PrivateEventRepositoryTest {

    private final PrivateEventRepository privateEventRepository;
    private final PrivateUserRepository privateUserRepository;
    private final PrivateCategoryRepository privateCategoryRepository;
    private static long initialId;

    private Event event;
    private User initiator;
    private Category category;

    @BeforeEach
    void setUp() {
        // Set up initial data (this will be automatically saved to the in-memory database)
        initiator = new User();
        long id = ++initialId;
        initiator.setId(id);
        initiator.setName("Test User");
        initiator.setEmail("test%d@example.com".formatted(id));
        initiator = privateUserRepository.save(initiator);
        Category category = new Category();
        id = ++initialId;
        category.setId(id);
        category.setName("Test Category %d".formatted(id));
        category = privateCategoryRepository.save(category);
        event = Event.builder()
            .id(++initialId)
            .annotation("Test Event Annotation")
            .category(category)
            .createdOn(LocalDateTime.now())
            .eventDate(LocalDateTime.now().plusDays(1))
            .description("Test Event Description")
            .initiator(initiator)
            .location(new ru.practicum.dto.event.Location(0.0f, 0.0f))
            .paid(false)
            .participantLimit(10)
            .requestModeration(true)
            .title("Test Event")
            .state(ru.practicum.dto.event.State.PENDING)
            .build();
        event = privateEventRepository.save(event);
    }

    @Test
    void testGetRequestCountByEventAndStatus() {
        // This assumes there is another entity (Request) in your database, but for simplicity we'll simulate it
        Long eventId = event.getId();
        Status status = Status.PENDING;

        RequestCount requestCount = privateEventRepository.getRequestCountByEventAndStatus(eventId, status);

        // You might expect 0 or a different value based on your setup
        assertThat(requestCount.getConfirmedRequests()).isEqualTo(0);  // Simulate no requests
    }

    @Test
    void testFindAllByInitiatorIdOrderByCreatedOnAsc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Event> events =
            privateEventRepository.findAllByInitiatorIdOrderByCreatedOnAsc(initiator.getId(), pageable);

        assertThat(events).hasSize(1);  // Since we only inserted one event in setup
        assertThat(events.getFirst().getTitle()).isEqualTo("Test Event");
    }

    @Test
    void testFindByIdAndInitiatorId() {
        Optional<Event> foundEvent = privateEventRepository.findByIdAndInitiatorId(event.getId(), initiator.getId());

        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getId()).isEqualTo(event.getId());
        assertThat(foundEvent.get().getInitiator().getId()).isEqualTo(initiator.getId());
    }

    @Test
    void testExistsByIdAndInitiatorId() {
        boolean exists = privateEventRepository.existsByIdAndInitiatorId(event.getId(), initiator.getId());

        assertThat(exists).isTrue();
    }
}
