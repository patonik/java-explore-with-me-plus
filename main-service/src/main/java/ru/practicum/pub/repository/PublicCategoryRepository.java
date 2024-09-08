package ru.practicum.pub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Category;

@Repository
public interface PublicCategoryRepository extends JpaRepository<Category, Long> {
}
