package ru.practicum.pub.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.pub.repository.PublicCategoryRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublicCategoryServiceTest {
    private AutoCloseable closeable;

    @Mock
    private PublicCategoryRepository publicCategoryRepository;

    @InjectMocks
    private PublicCategoryService publicCategoryService;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGetCategories() {
        Pageable pageable = PageRequest.of(0, 10);
        CategoryDto categoryDto1 = new CategoryDto(1L, "Technology");
        CategoryDto categoryDto2 = new CategoryDto(2L, "Science");

        when(publicCategoryRepository.getCategoryDtos(pageable)).thenReturn(Arrays.asList(categoryDto1, categoryDto2));

        List<CategoryDto> result = publicCategoryService.getCategories(0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Technology", result.get(0).getName());
        assertEquals("Science", result.get(1).getName());

        verify(publicCategoryRepository, times(1)).getCategoryDtos(pageable);
    }

    @Test
    void testGetCategory_Success() {
        Long catId = 1L;
        Category category = new Category(catId, "Technology");

        when(publicCategoryRepository.findById(catId)).thenReturn(Optional.of(category));

        CategoryDto result = publicCategoryService.getCategory(catId);

        assertNotNull(result);
        assertEquals("Technology", result.getName());
        verify(publicCategoryRepository, times(1)).findById(catId);
    }

    @Test
    void testGetCategory_NotFoundException() {
        Long catId = 1L;

        when(publicCategoryRepository.findById(catId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> publicCategoryService.getCategory(catId));

        assertEquals("Category not found", exception.getMessage());
        verify(publicCategoryRepository, times(1)).findById(catId);
    }
}
