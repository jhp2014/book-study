package com.project.bookstudy.board.controller;

import com.project.bookstudy.board.dto.category.*;
import com.project.bookstudy.board.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    @PostMapping
    public CreateCategoryResponse createCategory(@RequestBody CreateCategoryRequest request) {
        Long categoryId = categoryService.createCategory(request);

        return CreateCategoryResponse.builder()
                .categoryId(categoryId)
                .build();
    }

    @GetMapping
    public CategoryResponse getRootOrChildCategory(@RequestParam(required = false) Long parentId) {
        List<CategoryDto> rootOrChildCategoryList = categoryService.getRootOrChildCategoryList(parentId);

        return CategoryResponse.builder()
                .categoryId(parentId)
                .childCategories(rootOrChildCategoryList)
                .build();
    }

    @PatchMapping
    public void updateCategory(UpdateCategoryRequest request) {
        categoryService.updateCategory(request);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable("id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
    }
}
