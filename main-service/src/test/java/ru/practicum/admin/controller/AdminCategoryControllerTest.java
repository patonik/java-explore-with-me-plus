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
import ru.practicum.admin.service.AdminCategoryService;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminCategoryController.class)
class AdminCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminCategoryService adminCategoryService;

    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        categoryDto = new CategoryDto(1L, "Technology");
    }

    @Test
    void testAddCategory() throws Exception {
        NewCategoryDto newCategoryDto = new NewCategoryDto("Technology");

        Mockito.when(adminCategoryService.addCategory(any(NewCategoryDto.class)))
            .thenReturn(categoryDto);

        mockMvc.perform(post("/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategoryDto)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Technology"));
    }

    @Test
    void testDeleteCategory() throws Exception {
        Long catId = 1L;

        Mockito.doNothing().when(adminCategoryService).deleteCategory(catId);

        mockMvc.perform(delete("/admin/categories/{catId}", catId))
            .andExpect(status().isNoContent());

        Mockito.verify(adminCategoryService, Mockito.times(1)).deleteCategory(catId);
    }

    @Test
    void testUpdateCategory() throws Exception {
        NewCategoryDto newCategoryDto = new NewCategoryDto("Updated Technology");

        Mockito.when(adminCategoryService.updateCategory(eq(1L), any(NewCategoryDto.class)))
            .thenReturn(new CategoryDto(1L, "Updated Technology"));

        mockMvc.perform(patch("/admin/categories/{catId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategoryDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Updated Technology"));
    }
}
