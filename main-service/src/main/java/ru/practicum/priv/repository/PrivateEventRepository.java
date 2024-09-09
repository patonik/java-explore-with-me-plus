package ru.practicum.priv.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.event.request.RequestCount;
import ru.practicum.dto.event.request.Status;
import ru.practicum.model.Event;

import java.util.List;


@Repository
public interface PrivateEventRepository extends JpaRepository<Event, Long> {
    @Query("""
        select new ru.practicum.dto.event.request.RequestCount(count(r.id))
        from Request r
        where r.event.id=:eventId and r.status=:status
        """)
    RequestCount getRequestCountByEventAndStatus(Long eventId, Status status);

    List<Event> findAllByInitiatorIdOrderByCreatedOnAsc(Long userId, Pageable pageable);
}
