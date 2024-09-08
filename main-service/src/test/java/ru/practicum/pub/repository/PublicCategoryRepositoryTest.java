package ru.practicum.pub.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.model.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PublicCategoryRepositoryTest {

    @Autowired
    private PublicCategoryRepository publicCategoryRepository;

    @BeforeEach
    void setUp() {
        // Set up test data
        Category category1 = new Category(null, "Technology");
        Category category2 = new Category(null, "Science");
        publicCategoryRepository.save(category1);
        publicCategoryRepository.save(category2);
    }

    @Test
    void testGetCategoryDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        // Test fetching categories as DTOs
        List<CategoryDto> categoryDtos = publicCategoryRepository.getCategoryDtos(pageable);

        // Assert that two categories are returned
        assertThat(categoryDtos).hasSize(2);
        assertThat(categoryDtos).extracting("name").contains("Technology", "Science");
    }

    @Test
    void testGetCategoryDtosWithPagination() {
        // Save an additional category
        publicCategoryRepository.save(new Category(null, "Art"));

        Pageable pageable = PageRequest.of(0, 3); // Requesting first two items
        List<CategoryDto> categoryDtos = publicCategoryRepository.getCategoryDtos(pageable);

        // Assert that only two categories are returned (due to pagination)
        assertThat(categoryDtos).hasSize(3);
        assertThat(categoryDtos).extracting("name").contains("Technology", "Science", "Art");
    }
}
