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
import com.seafoodmall.backend.entity.Product;
import com.seafoodmall.backend.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/products")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Product> createProduct(@RequestBody Product product) {
        log.info("API Call: /api/admin/products - Creating product: {}", product.getName());
        Result<Product> result = Result.success(productService.create(product));
        log.info("API Response: /api/admin/products - Product created: {}", result.getData().getId());
        return result;
    }

    @GetMapping("/products/detail/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        log.info("API Call: /api/products/detail/{} - Getting product by ID", id);
        Result<Product> result = Result.success(productService.getById(id));
        log.info("API Response: /api/products/detail/{} - Product fetched: {}", id, result.getData().getName());
        return result;
    }

    @GetMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Product> getProductForAdmin(@PathVariable Long id) {
        log.info("API Call: /api/admin/products/{} - Getting product by ID for admin", id);
        Result<Product> result = Result.success(productService.getById(id));
        log.info("API Response: /api/admin/products/{} - Product fetched for admin: {}", id, result.getData().getName());
        return result;
    }

    @GetMapping("/admin/products/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Product>> getProductPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {

        log.info("API Call: /api/admin/products/page - Getting product page with current:{}, size:{}, categoryId:{}, name:{}, status:{}", current, size, categoryId, name, status);
        Page<Product> page = new Page<>(current, size);
        Product queryProduct = new Product();
        if (categoryId != null) {
            queryProduct.setCategoryId(categoryId);
        }
        if (name != null && !name.isEmpty()) {
            queryProduct.setName(name);
        }
        if (status != null) {
            queryProduct.setStatus(status);
        }
        Result<Page<Product>> result = Result.success(productService.page(page, queryProduct));
        log.info("API Response: /api/admin/products/page - Product page fetched, total: {}", result.getData().getTotal());
        return result;
    }

    @PutMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        log.info("API Call: /api/admin/products/{} - Updating product: {}", id, product.getName());
        product.setId(id);
        Result<Product> result = Result.success(productService.update(product));
        log.info("API Response: /api/admin/products/{} - Product updated: {}", id, result.getData().getName());
        return result;
    }

    @DeleteMapping("/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        log.info("API Call: /api/admin/products/{} - Deleting product by ID", id);
        productService.delete(id);
        Result<Void> result = Result.success();
        log.info("API Response: /api/admin/products/{} - Product deleted.", id);
        return result;
    }

    @PutMapping("/admin/products/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        log.info("API Call: /api/admin/products/{}/stock - Updating stock for product ID: {} with quantity: {}", id, id, quantity);
        productService.updateStock(id, quantity);
        Result<Void> result = Result.success();
        log.info("API Response: /api/admin/products/{}/stock - Stock updated for product ID: {}", id, id);
        return result;
    }

    @GetMapping("/products/page")
    public Result<Page<Product>> getProductPageForH5(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {

        log.info("API Call: /api/products/page - Getting product page for H5 with current:{}, size:{}, categoryId:{}, name:{}, status:{}", current, size, categoryId, name, status);
        Page<Product> page = new Page<>(current, size);
        Product queryProduct = new Product();
        if (categoryId != null) {
            queryProduct.setCategoryId(categoryId);
        }
        if (name != null && !name.isEmpty()) {
            queryProduct.setName(name);
        }
        if (status != null) {
            queryProduct.setStatus(status);
        }
        Result<Page<Product>> result = Result.success(productService.page(page, queryProduct));
        log.info("API Response: /api/products/page - Product page fetched, total: {}", result.getData().getTotal());
        return result;
    }

    @GetMapping("/products/{id}/specs")
    public Result<List<String>> getProductSpecs(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        // 只返回一个单位，前端以数组形式接收
        return Result.success(Collections.singletonList(product.getUnit()));
    }
} 