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
import com.seafoodmall.backend.entity.Product;
import com.seafoodmall.backend.mapper.ProductMapper;
import com.seafoodmall.backend.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public Product create(Product product) {
        save(product);
        return product;
    }

    @Override
    public Product update(Product product) {
        updateById(product);
        return product;
    }

    @Override
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public Product getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Page<Product> page(Page<Product> page, Product product) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Product::getDeleted, 0);
        
        if (product != null) {
            if (StringUtils.hasText(product.getName())) {
                wrapper.like(Product::getName, product.getName());
            }
            if (product.getCategoryId() != null) {
                wrapper.eq(Product::getCategoryId, product.getCategoryId());
            }
            if (product.getStatus() != null) {
                wrapper.eq(Product::getStatus, product.getStatus());
            }
        }
        
        wrapper.orderByDesc(Product::getCreatedTime);
        
        Page<Product> resultPage = page(page, wrapper);
        // 为每个商品补充销量字段
        if (resultPage.getRecords() != null) {
            for (Product p : resultPage.getRecords()) {
                Integer sold = this.baseMapper.getProductSold(p.getId());
                try {
                    java.lang.reflect.Field soldField = Product.class.getDeclaredField("sold");
                    soldField.setAccessible(true);
                    soldField.set(p, sold);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return resultPage;
    }

    @Override
    @Transactional
    public void updateStock(Long id, Integer quantity) {
        Product product = getById(id);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        
        int newStock = product.getStock() - quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("商品库存不足");
        }
        
        product.setStock(newStock);
        updateById(product);
    }
} 