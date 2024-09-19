package ru.practicum.pub.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.model.Compilation;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class PublicCompilationRepositoryTest {

    @Autowired
    private PublicCompilationRepository publicCompilationRepository;

    @Test
    void findById_Success() {
        // Save a sample Compilation
        Compilation compilation = new Compilation(null, true, "Test Compilation", Set.of());
        Compilation savedCompilation = publicCompilationRepository.save(compilation);

        // Find by ID
        Optional<Compilation> foundCompilation = publicCompilationRepository.findById(savedCompilation.getId());

        // Verify result
        assertTrue(foundCompilation.isPresent());
        assertEquals("Test Compilation", foundCompilation.get().getTitle());
    }

    @Test
    void findById_NotFound() {
        // Try to find a compilation with an ID that doesn't exist
        Optional<Compilation> foundCompilation = publicCompilationRepository.findById(999L);

        // Verify result
        assertFalse(foundCompilation.isPresent());
    }
}
