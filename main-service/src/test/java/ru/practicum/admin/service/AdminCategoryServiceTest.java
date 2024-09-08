package ru.practicum.admin.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.admin.repository.AdminCategoryRepository;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminCategoryServiceTest {
    private AutoCloseable closeable;

    @Mock
    private AdminCategoryRepository adminCategoryRepository;

    @InjectMocks
    private AdminCategoryService adminCategoryService;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testAddCategory_Success() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("New Category");
        Category category = new Category(1L, "New Category");

        when(adminCategoryRepository.existsByName("New Category")).thenReturn(false);
        when(adminCategoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto result = adminCategoryService.addCategory(newCategoryDto);

        assertNotNull(result);
        assertEquals("New Category", result.getName());
        verify(adminCategoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testAddCategory_ConflictException() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("Existing Category");

        when(adminCategoryRepository.existsByName("Existing Category")).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> adminCategoryService.addCategory(newCategoryDto));

        assertEquals("Category with name Existing Category already exists", exception.getMessage());
        verify(adminCategoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testDeleteCategory_Success() {
        Long catId = 1L;

        when(adminCategoryRepository.existsById(catId)).thenReturn(true);

        adminCategoryService.deleteCategory(catId);

        verify(adminCategoryRepository, times(1)).deleteById(catId);
    }

    @Test
    void testDeleteCategory_NotFoundException() {
        Long catId = 1L;

        when(adminCategoryRepository.existsById(catId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> adminCategoryService.deleteCategory(catId));

        assertEquals("Category with id 1 does not exist", exception.getMessage());
        verify(adminCategoryRepository, never()).deleteById(catId);
    }

    @Test
    void testDeleteCategory_DataIntegrityViolationException() {
        Long catId = 1L;

        when(adminCategoryRepository.existsById(catId)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(adminCategoryRepository).deleteById(catId);

        ConflictException exception = assertThrows(ConflictException.class, () -> adminCategoryService.deleteCategory(catId));

        assertEquals("Cannot delete category with ID 1", exception.getMessage());
        verify(adminCategoryRepository, times(1)).deleteById(catId);
    }

    @Test
    void testUpdateCategory_Success() {
        Long catId = 1L;
        NewCategoryDto updatedCategoryDto = new NewCategoryDto("Updated Category");
        Category existingCategory = new Category(catId, "Old Category");

        when(adminCategoryRepository.findById(catId)).thenReturn(java.util.Optional.of(existingCategory));
        when(adminCategoryRepository.existsByName("Updated Category")).thenReturn(false);
        when(adminCategoryRepository.save(any(Category.class))).thenReturn(existingCategory);

        CategoryDto result = adminCategoryService.updateCategory(catId, updatedCategoryDto);

        assertNotNull(result);
        assertEquals("Updated Category", result.getName());
        verify(adminCategoryRepository, times(1)).save(existingCategory);
    }

    @Test
    void testUpdateCategory_NotFoundException() {
        Long catId = 1L;
        NewCategoryDto updatedCategoryDto = new NewCategoryDto("Updated Category");

        when(adminCategoryRepository.findById(catId)).thenReturn(java.util.Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> adminCategoryService.updateCategory(catId, updatedCategoryDto));

        assertEquals("Category with id 1 does not exist", exception.getMessage());
        verify(adminCategoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testUpdateCategory_ConflictException() {
        Long catId = 1L;
        NewCategoryDto updatedCategoryDto = new NewCategoryDto("Existing Category");
        Category existingCategory = new Category(catId, "Old Category");

        when(adminCategoryRepository.findById(catId)).thenReturn(java.util.Optional.of(existingCategory));
        when(adminCategoryRepository.existsByName("Existing Category")).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class, () -> adminCategoryService.updateCategory(catId, updatedCategoryDto));

        assertEquals("Category with name Existing Category already exists", exception.getMessage());
        verify(adminCategoryRepository, never()).save(any(Category.class));
    }
}
