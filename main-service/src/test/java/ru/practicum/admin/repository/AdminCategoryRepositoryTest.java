package ru.practicum.admin.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.model.Category;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AdminCategoryRepositoryTest {

    @Autowired
    private AdminCategoryRepository adminCategoryRepository;

    @BeforeEach
    void setUp() {
        // Set up test data
        Category category1 = new Category(null, "Technology");
        Category category2 = new Category(null, "Science");
        adminCategoryRepository.save(category1);
        adminCategoryRepository.save(category2);
    }

    @Test
    void testExistsByName_ReturnsTrue() {
        // Test that the category exists
        boolean exists = adminCategoryRepository.existsByName("Technology");
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByName_ReturnsFalse() {
        // Test that the category does not exist
        boolean exists = adminCategoryRepository.existsByName("NonExistentCategory");
        assertThat(exists).isFalse();
    }

    @Test
    void testSaveAndFind() {
        // Test saving a new category and checking if it exists
        Category newCategory = new Category(null, "Art");
        adminCategoryRepository.save(newCategory);

        boolean exists = adminCategoryRepository.existsByName("Art");
        assertThat(exists).isTrue();
    }
}
