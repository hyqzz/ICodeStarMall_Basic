/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long categoryId;
    
    private String name;
    
    @TableField("description")
    private String description;
    
    private BigDecimal price;
    
    private Integer stock;
    
    private String unit;
    
    @TableField("image")
    private String image;
    
    @TableField("status")
    private Integer status;
    
    @TableField("created_time")
    private LocalDateTime createdTime;
    
    @TableField("updated_time")
    private LocalDateTime updatedTime;
    
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private Integer sold;
} 