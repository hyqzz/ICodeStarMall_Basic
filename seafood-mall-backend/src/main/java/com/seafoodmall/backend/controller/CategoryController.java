/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafoodmall.backend.common.Result;
import com.seafoodmall.backend.entity.Category;
import com.seafoodmall.backend.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Category> createCategory(@RequestBody Category category) {
        log.info("API Call: /api/admin/categories - Creating category: {}", category.getName());
        Result<Category> result = Result.success(categoryService.create(category));
        log.info("API Response: /api/admin/categories - Category created with ID: {}", result.getData().getId());
        return result;
    }

    @PutMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        log.info("API Call: /api/admin/categories/{} - Updating category ID: {}", id, id);
        category.setId(id);
        Result<Category> result = Result.success(categoryService.update(category));
        log.info("API Response: /api/admin/categories/{} - Category updated.", id);
        return result;
    }

    @DeleteMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        log.info("API Call: /api/admin/categories/{} - Deleting category ID: {}", id, id);
        categoryService.delete(id);
        Result<Void> result = Result.success();
        log.info("API Response: /api/admin/categories/{} - Category deleted.", id);
        return result;
    }

    @GetMapping("/categories/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        log.info("API Call: /api/categories/{} - Getting category by ID: {}", id, id);
        Result<Category> result = Result.success(categoryService.getById(id));
        log.info("API Response: /api/categories/{} - Category fetched: {}", id, result.getData().getName());
        return result;
    }

    @GetMapping("/categories/all")
    public Result<List<Category>> getAllCategories() {
        log.info("API Call: /api/categories/all - Getting all categories.");
        Result<List<Category>> result = Result.success(categoryService.getAllCategories());
        log.info("API Response: /api/categories/all - Fetched {} categories.", result.getData().size());
        return result;
    }

    @GetMapping("/admin/categories/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Category>> getCategoryPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long parentId) {

        log.info("API Call: /api/admin/categories/page - Getting category page with current:{}, size:{}, name:{}, status:{}, parentId:{}", current, size, name, status, parentId);
        Page<Category> page = new Page<>(current, size);
        Category queryCategory = new Category();
        queryCategory.setName(name);
        queryCategory.setStatus(status);
        queryCategory.setParentId(parentId);
        Result<Page<Category>> result = Result.success(categoryService.page(page, queryCategory));
        log.info("API Response: /api/admin/categories/page - Category page fetched, total: {}", result.getData().getTotal());
        return result;
    }

    @GetMapping("/categories/parent/{parentId}")
    public Result<List<Category>> getCategoriesByParentId(@PathVariable Long parentId) {
        log.info("API Call: /api/categories/parent/{} - Getting categories by parent ID: {}", parentId, parentId);
        Result<List<Category>> result = Result.success(categoryService.getCategoriesByParentId(parentId));
        log.info("API Response: /api/categories/parent/{} - Fetched {} categories.", parentId, result.getData().size());
        return result;
    }
} 