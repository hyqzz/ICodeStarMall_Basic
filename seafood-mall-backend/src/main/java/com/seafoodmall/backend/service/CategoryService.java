/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafoodmall.backend.entity.Category;

import java.util.List;

public interface CategoryService {
    Category create(Category category);
    
    Category update(Category category);
    
    void delete(Long id);
    
    Category getById(Long id);
    
    List<Category> getAllCategories();
    
    Page<Category> page(Page<Category> page, Category category);

    List<Category> getCategoriesByParentId(Long parentId);
} 