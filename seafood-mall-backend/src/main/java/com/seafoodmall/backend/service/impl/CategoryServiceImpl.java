/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seafoodmall.backend.entity.Category;
import com.seafoodmall.backend.mapper.CategoryMapper;
import com.seafoodmall.backend.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public Category create(Category category) {
        log.info("Creating category: {}", category.getName());
        save(category);
        log.info("Category created with ID: {}", category.getId());
        return category;
    }

    @Override
    public Category update(Category category) {
        log.info("Updating category with ID: {}", category.getId());
        updateById(category);
        log.info("Category updated: {}", category.getId());
        return category;
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting category with ID: {}", id);
        removeById(id);
        log.info("Category deleted: {}", id);
    }

    @Override
    public Category getById(Long id) {
        log.info("Getting category by ID: {}", id);
        Category category = super.getById(id);
        log.info("Category fetched: {}", category);
        return category;
    }

    @Override
    public List<Category> getAllCategories() {
        log.info("Fetching all categories.");
        List<Category> categories = list();
        log.info("Fetched {} categories.", categories.size());
        return categories;
    }

    @Override
    public Page<Category> page(Page<Category> page, Category category) {
        log.info("Fetching category page with current:{}, size:{}", page.getCurrent(), page.getSize());
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (category != null) {
            if (category.getName() != null && !category.getName().isEmpty()) {
                wrapper.like(Category::getName, category.getName());
            }
            if (category.getStatus() != null) {
                wrapper.eq(Category::getStatus, category.getStatus());
            }
            if (category.getParentId() != null) {
                wrapper.eq(Category::getParentId, category.getParentId());
            }
        }
        wrapper.orderByAsc(Category::getSort);
        Page<Category> result = page(page, wrapper);
        log.info("Category page fetched, total: {}", result.getTotal());
        return result;
    }

    @Override
    public List<Category> getCategoriesByParentId(Long parentId) {
        log.info("Fetching categories by parent ID: {}", parentId);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, parentId);
        wrapper.orderByAsc(Category::getSort);
        List<Category> categories = list(wrapper);
        log.info("Fetched {} categories for parent ID: {}", categories.size(), parentId);
        return categories;
    }
} 