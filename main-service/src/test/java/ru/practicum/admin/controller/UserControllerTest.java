package ru.practicum.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.admin.service.UserService;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        user1 = new UserDto(1L, "Alice", "alice@example.com");
        user2 = new UserDto(2L, "Bob", "bob@example.com");
    }

    @Test
    void testGetUsers_ReturnsUserList() throws Exception {
        List<Long> ids = List.of(1L, 2L);
        List<UserDto> users = Arrays.asList(user1, user2);
        Mockito.when(userService.getUsers(eq(ids), eq(0), eq(10))).thenReturn(users);

        mockMvc.perform(get("/admin/users")
                .param("ids", "1", "2")
                .param("from", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name").value("Alice"))
            .andExpect(jsonPath("$[1].name").value("Bob"));
    }

    @Test
    void testGetUsers_ReturnsEmptyList() throws Exception {
        List<Long> ids = List.of(1L, 2L);
        Mockito.when(userService.getUsers(eq(ids), eq(0), eq(10))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/users")
                .param("ids", "1", "2")
                .param("from", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testAddUser_ReturnsCreatedUser() throws Exception {
        NewUserRequest newUserRequest = new NewUserRequest("Charlie", "charlie@example.com");
        UserDto createdUser = new UserDto(3L, "Charlie", "charlie@example.com");

        Mockito.when(userService.addUser(any(NewUserRequest.class))).thenReturn(createdUser);

        mockMvc.perform(post("/admin/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUserRequest)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Charlie"))
            .andExpect(jsonPath("$.email").value("charlie@example.com"));
    }

    @Test
    void testDeleteUser_ReturnsNoContent() throws Exception {
        Long userId = 1L;

        Mockito.doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/admin/users/{userId}", userId))
            .andExpect(status().isNoContent());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(userId);
    }
}
