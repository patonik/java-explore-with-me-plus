package ru.practicum.pub.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.pub.service.PublicCategoryService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PublicCategoryController.class)
class PublicCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicCategoryService publicCategoryService;

    private CategoryDto categoryDto1;
    private CategoryDto categoryDto2;

    @BeforeEach
    void setUp() {
        categoryDto1 = new CategoryDto(1L, "Technology");
        categoryDto2 = new CategoryDto(2L, "Science");
    }

    @Test
    void testGetCategories() throws Exception {
        List<CategoryDto> categoryDtos = Arrays.asList(categoryDto1, categoryDto2);

        Mockito.when(publicCategoryService.getCategories(anyInt(), anyInt())).thenReturn(categoryDtos);

        mockMvc.perform(get("/categories")
                .param("from", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].name").value("Technology"))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].name").value("Science"));
    }

    @Test
    void testGetCategory() throws Exception {
        Long catId = 1L;

        Mockito.when(publicCategoryService.getCategory(catId)).thenReturn(categoryDto1);

        mockMvc.perform(get("/categories/{catId}", catId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Technology"));
    }
}
