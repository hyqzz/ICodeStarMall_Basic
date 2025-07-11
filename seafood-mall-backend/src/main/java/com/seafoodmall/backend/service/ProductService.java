/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafoodmall.backend.entity.Product;

public interface ProductService {
    Product create(Product product);
    
    Product update(Product product);
    
    void delete(Long id);
    
    Product getById(Long id);
    
    Page<Product> page(Page<Product> page, Product product);
    
    void updateStock(Long id, Integer quantity);
} 