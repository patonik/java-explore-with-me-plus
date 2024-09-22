package ru.practicum.admin.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.model.Compilation;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
class AdminCompilationRepositoryTest {

    @Autowired
    private AdminCompilationRepository adminCompilationRepository;

    @Test
    void saveCompilation_Success() {
        // Create and save a Compilation
        Compilation compilation = new Compilation(null, true, "Test Compilation", new HashSet<>());
        Compilation savedCompilation = adminCompilationRepository.save(compilation);

        // Assert the result
        assertNotNull(savedCompilation.getId());
        assertEquals("Test Compilation", savedCompilation.getTitle());
    }

    @Test
    void findCompilationById_Success() {
        // Save a Compilation
        Compilation compilation = new Compilation(null, true, "Test Compilation", new HashSet<>());
        Compilation savedCompilation = adminCompilationRepository.save(compilation);

        // Find it by ID
        Compilation foundCompilation = adminCompilationRepository.findById(savedCompilation.getId()).orElse(null);

        // Assert the result
        assertNotNull(foundCompilation);
        assertEquals(savedCompilation.getId(), foundCompilation.getId());
    }

    @Test
    void deleteCompilation_Success() {
        // Save a Compilation
        Compilation compilation = new Compilation(null, true, "Test Compilation", new HashSet<>());
        Compilation savedCompilation = adminCompilationRepository.save(compilation);

        // Delete it
        adminCompilationRepository.deleteById(savedCompilation.getId());

        // Assert it no longer exists
        assertFalse(adminCompilationRepository.findById(savedCompilation.getId()).isPresent());
    }
}
