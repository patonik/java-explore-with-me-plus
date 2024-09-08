package ru.practicum.priv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.event.request.ParticipationRequestDto;
import ru.practicum.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateRequestRepository extends JpaRepository<Request, Long> {

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
}
