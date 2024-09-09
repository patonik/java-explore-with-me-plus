package ru.practicum.priv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.event.request.ParticipationRequestDto;
import ru.practicum.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("""
            SELECT
                new ru.practicum.dto.event.request.ParticipationRequestDto(
                    r.id,
                    r.created,
                    r.requester.id,
                    r.status
                )
            FROM
                Request r
            WHERE
                r.requester.id = :userId
            """)
    List<ParticipationRequestDto> findByRequesterId(Long userId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);
}
