package ru.practicum.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

@RestController
@RequestMapping("/admin/categories")
@Validated
public class CategoryController {
    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@RequestBody NewCategoryDto newCategoryDto) {
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable int catId) {
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable int catId,
                                                   @RequestBody NewCategoryDto newCategoryDto) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
