/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardDataResponse {
    private DashboardStatsResponse stats;
    private List<RecentOrderDTO> recentOrders;
    private List<HotProductDTO> hotProducts;
} 