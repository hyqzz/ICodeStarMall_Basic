/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardStatsResponse {
    private BigDecimal totalSalesAmount;
    private Long totalOrderCount;
    private Long totalProductCount;
    private Long totalUserCount;
    private Long monthlyNewProducts;
    private Long monthlyNewUsers;
} 