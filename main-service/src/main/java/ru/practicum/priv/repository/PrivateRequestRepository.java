package ru.practicum.priv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Request;

@Repository
public interface PrivateRequestRepository extends JpaRepository<Request, Long> {
}
