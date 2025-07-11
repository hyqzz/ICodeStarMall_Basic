/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seafoodmall.backend.dto.HotProductDTO;
import com.seafoodmall.backend.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
 
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT COUNT(*) FROM product WHERE deleted = 0")
    Long getTotalProductCount();

    @Select("SELECT COUNT(*) FROM product WHERE deleted = 0 AND created_time >= #{startOfMonth}")
    Long getMonthlyNewProducts(@Param("startOfMonth") LocalDateTime startOfMonth);

    @Select("" +
            "SELECT " +
            "  p.name as name, " +
            "  c.name as category, " +
            "  SUM(oi.quantity) as sales, " +
            "  p.stock as stock " +
            "FROM product p " +
            "JOIN category c ON p.category_id = c.id " +
            "JOIN order_item oi ON p.id = oi.product_id " +
            "WHERE p.deleted = 0 " +
            "GROUP BY p.id, p.name, c.name, p.stock " +
            "ORDER BY sales DESC " +
            "LIMIT 4")
    List<HotProductDTO> getHotProducts();

    @Select("SELECT COALESCE(SUM(quantity),0) FROM order_item WHERE product_id = #{productId} AND deleted = 0")
    Integer getProductSold(@Param("productId") Long productId);
} 