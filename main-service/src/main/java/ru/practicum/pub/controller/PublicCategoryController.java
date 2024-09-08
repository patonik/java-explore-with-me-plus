package ru.practicum.pub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.DataTransferConvention;
import ru.practicum.dto.category.CategoryDto;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class PublicCategoryController {
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(
        @RequestParam(required = false, defaultValue = DataTransferConvention.FROM) Integer from,
        @RequestParam(required = false, defaultValue = DataTransferConvention.SIZE) Integer size) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Integer catId) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
