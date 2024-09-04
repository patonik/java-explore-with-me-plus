package ru.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.dto.HitListElementDto;
import ru.practicum.model.ServiceHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StatRepositoryTest {

    @Autowired
    private StatRepository statRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        // Set up test data with formatted LocalDateTime values
        ServiceHit hit1 = ServiceHit.builder()
            .app("app1")
            .uri("/endpoint1")
            .ip("192.168.1.1")
            .created(LocalDateTime.parse("2023-01-01 12:00:00", formatter))
            .build();

        ServiceHit hit2 = ServiceHit.builder()
            .app("app1")
            .uri("/endpoint1")
            .ip("192.168.1.2")
            .created(LocalDateTime.parse("2023-01-02 12:00:00", formatter))
            .build();

        ServiceHit hit3 = ServiceHit.builder()
            .app("app1")
            .uri("/endpoint2")
            .ip("192.168.1.1")
            .created(LocalDateTime.parse("2023-01-02 12:00:00", formatter))
            .build();

        statRepository.save(hit1);
        statRepository.save(hit2);
        statRepository.save(hit3);
    }

    @Test
    void testGetHitListElementDtosDistinctIp() {
        LocalDateTime start = LocalDateTime.parse("2023-01-01 00:00:00", formatter);
        LocalDateTime end = LocalDateTime.parse("2023-01-02 23:59:59", formatter);
        String[] uris = {"/endpoint1", "/endpoint2"};

        List<HitListElementDto> result = statRepository.getHitListElementDtosDistinctIp(start, end, uris);

        assertThat(result).hasSize(2);
        assertThat(result).extracting("app").containsOnly("app1");
        assertThat(result).extracting("uri").contains("/endpoint1", "/endpoint2");
        assertThat(result).extracting("hits").containsExactly(2L, 1L);
    }

    @Test
    void testGetHitListElementDtos() {
        LocalDateTime start = LocalDateTime.parse("2023-01-01 00:00:00", formatter);
        LocalDateTime end = LocalDateTime.parse("2023-01-02 23:59:59", formatter);
        String[] uris = {"/endpoint1", "/endpoint2"};

        List<HitListElementDto> result = statRepository.getHitListElementDtos(start, end, uris);
        System.out.println(statRepository.findAll());
        assertThat(result).hasSize(2);
        assertThat(result).extracting("app").containsOnly("app1");
        assertThat(result).extracting("uri").contains("/endpoint1", "/endpoint2");
        assertThat(result).extracting("hits").containsExactly(2L, 1L);
    }
}
