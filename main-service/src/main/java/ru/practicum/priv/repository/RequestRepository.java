package ru.practicum.priv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
