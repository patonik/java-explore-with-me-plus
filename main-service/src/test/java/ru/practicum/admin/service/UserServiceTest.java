package ru.practicum.admin.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.admin.repository.AdminUserRepository;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.NewUserRequestMapper;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserDtoMapper;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private NewUserRequestMapper newUserRequestMapper;

    @Mock
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private UserService userService;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGetUsers() {
        // Setup test data
        Long[] ids = {1L, 2L};
        Pageable pageable = PageRequest.of(0, 10);
        UserDto user1 = new UserDto(1L, "Alice", "alice@example.com");
        UserDto user2 = new UserDto(2L, "Bob", "bob@example.com");

        when(adminUserRepository.findUserDtosByIds(eq(ids), eq(pageable)))
            .thenReturn(Arrays.asList(user1, user2));

        // Execute service method
        List<UserDto> users = userService.getUsers(ids, 0, 10);

        // Assert results
        assertEquals(2, users.size());
        assertEquals("Alice", users.get(0).getName());
        assertEquals("Bob", users.get(1).getName());

        // Verify repository interaction
        verify(adminUserRepository, times(1)).findUserDtosByIds(eq(ids), eq(pageable));
    }

    @Test
    void testAddUser_Success() {
        // Setup test data
        NewUserRequest newUserRequest = new NewUserRequest("Charlie", "charlie@example.com");
        User newUser = new User(null, "Charlie", "charlie@example.com");
        User savedUser = new User(3L, "Charlie", "charlie@example.com");
        UserDto userDto = new UserDto(3L, "Charlie", "charlie@example.com");

        when(adminUserRepository.existsByEmail("charlie@example.com")).thenReturn(false);
        when(newUserRequestMapper.toUser(newUserRequest)).thenReturn(newUser);
        when(adminUserRepository.save(newUser)).thenReturn(savedUser);
        when(userDtoMapper.toUserDto(savedUser)).thenReturn(userDto);

        // Execute service method
        UserDto result = userService.addUser(newUserRequest);

        // Assert results
        assertNotNull(result);
        assertEquals("Charlie", result.getName());
        assertEquals("charlie@example.com", result.getEmail());

        // Verify repository and mapper interactions
        verify(adminUserRepository, times(1)).existsByEmail("charlie@example.com");
        verify(newUserRequestMapper, times(1)).toUser(newUserRequest);
        verify(adminUserRepository, times(1)).save(newUser);
        verify(userDtoMapper, times(1)).toUserDto(savedUser);
    }

    @Test
    void testAddUser_EmailAlreadyExists() {
        // Setup test data
        NewUserRequest newUserRequest = new NewUserRequest("Charlie", "charlie@example.com");

        when(adminUserRepository.existsByEmail("charlie@example.com")).thenReturn(true);

        // Execute service method and assert exception
        ConflictException exception = assertThrows(ConflictException.class, () -> userService.addUser(newUserRequest));

        assertEquals("Email already exists", exception.getMessage());

        // Verify repository interaction
        verify(adminUserRepository, times(1)).existsByEmail("charlie@example.com");
        verify(adminUserRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        // Setup test data
        Long userId = 1L;

        when(adminUserRepository.existsById(userId)).thenReturn(true);

        // Execute service method
        userService.deleteUser(userId);

        // Verify repository interaction
        verify(adminUserRepository, times(1)).existsById(userId);
        verify(adminUserRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Setup test data
        Long userId = 1L;

        when(adminUserRepository.existsById(userId)).thenReturn(false);

        // Execute service method and assert exception
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));

        assertEquals("User not found", exception.getMessage());

        // Verify repository interaction
        verify(adminUserRepository, times(1)).existsById(userId);
        verify(adminUserRepository, never()).deleteById(any(Long.class));
    }
}
