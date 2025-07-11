/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.controller;

import com.seafoodmall.backend.common.Result;
import com.seafoodmall.backend.dto.DashboardDataResponse;
import com.seafoodmall.backend.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public Result<DashboardDataResponse> getDashboardData() {
        log.info("Fetching dashboard data...");
        DashboardDataResponse data = dashboardService.getDashboardData();
        log.info("Dashboard data fetched: {}", data);
        Result<DashboardDataResponse> result = Result.success(data);
        log.info("Returning dashboard data result: {}", result);
        return result;
    }
} 