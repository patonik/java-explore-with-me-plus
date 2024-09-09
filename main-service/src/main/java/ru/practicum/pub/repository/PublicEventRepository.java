package ru.practicum.pub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;

import java.util.List;

@Repository
public interface PublicEventRepository extends JpaRepository<Event, Long> {

    List<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);
}
