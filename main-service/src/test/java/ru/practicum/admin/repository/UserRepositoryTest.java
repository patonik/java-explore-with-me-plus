package ru.practicum.admin.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.dto.user.UserDto;
import ru.practicum.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserRepositoryTest {
    private final UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Set up some users in the in-memory database for testing purposes
        User user1 = new User(null, "Alice", "alice@example.com");
        User user2 = new User(null, "Bob", "bob@example.com");
        User user3 = new User(null, "Charlie", "charlie@example.com");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }

    @Test
    void testFindUserDtosByIds() {
        List<User> all = userRepository.findAll();
        User first = all.getFirst();
        User last = all.getLast();
        Long[] ids = {first.getId(), last.getId()};
        Pageable pageable = PageRequest.of(0, 1);
        Pageable pageable2 = PageRequest.of(1, 1);
        Pageable pageable3 = PageRequest.of(2, 1);

        // Act
        List<UserDto> userDtos = userRepository.findUserDtosByIds(ids, pageable);
        List<UserDto> userDtos2 = userRepository.findUserDtosByIds(ids, pageable2);
        List<UserDto> userDtos3 = userRepository.findUserDtosByIds(ids, pageable3);

        // Assert
        assertThat(userDtos).hasSize(1);
        assertThat(userDtos2).hasSize(1);
        assertThat(userDtos3).hasSize(0);
        assertThat(userDtos).extracting("name").contains(first.getName());
        assertThat(userDtos2).extracting("name").contains(last.getName());
    }

    @Test
    void testExistsByEmail() {
        // Check if a user exists with a specific email
        Boolean exists = userRepository.existsByEmail("alice@example.com");

        // Assert
        assertThat(exists).isTrue();

        // Check if a user does not exist
        Boolean notExists = userRepository.existsByEmail("nonexistent@example.com");
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindById() {
        // Act
        Optional<User> user = userRepository.findById(1L);

        // Assert
        assertThat(user).isPresent();
        assertThat(user.get().getName()).isEqualTo("Alice");
    }
}
