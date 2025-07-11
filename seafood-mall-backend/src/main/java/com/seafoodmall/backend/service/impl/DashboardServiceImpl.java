/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seafoodmall.backend.dto.DashboardDataResponse;
import com.seafoodmall.backend.dto.DashboardStatsResponse;
import com.seafoodmall.backend.dto.HotProductDTO;
import com.seafoodmall.backend.dto.RecentOrderDTO;
import com.seafoodmall.backend.mapper.OrderMapper;
import com.seafoodmall.backend.mapper.ProductMapper;
import com.seafoodmall.backend.mapper.UserMapper;
import com.seafoodmall.backend.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public DashboardDataResponse getDashboardData() {
        log.info("Building dashboard data response...");
        DashboardDataResponse response = new DashboardDataResponse();
        DashboardStatsResponse stats = new DashboardStatsResponse();

        // 1. 获取总销售额和总订单量
        BigDecimal totalSalesAmount = orderMapper.getTotalSalesAmount();
        Long totalOrderCount = orderMapper.getTotalOrderCount();
        log.debug("Total sales amount: {}, Total order count: {}", totalSalesAmount, totalOrderCount);

        stats.setTotalSalesAmount(totalSalesAmount != null ? totalSalesAmount : BigDecimal.ZERO);
        stats.setTotalOrderCount(totalOrderCount != null ? totalOrderCount : 0L);

        // 2. 获取商品总数
        Long totalProductCount = productMapper.getTotalProductCount();
        log.debug("Total product count: {}", totalProductCount);
        stats.setTotalProductCount(totalProductCount != null ? totalProductCount : 0L);

        // 3. 获取用户总数
        Long totalUserCount = userMapper.getTotalUserCount();
        log.debug("Total user count: {}", totalUserCount);
        stats.setTotalUserCount(totalUserCount != null ? totalUserCount : 0L);

        // 4. 获取本月新增商品和用户
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        
        Long monthlyNewProducts = productMapper.getMonthlyNewProducts(startOfMonth);
        Long monthlyNewUsers = userMapper.getMonthlyNewUsers(startOfMonth);
        log.debug("Monthly new products: {}, Monthly new users: {}", monthlyNewProducts, monthlyNewUsers);
        stats.setMonthlyNewProducts(monthlyNewProducts != null ? monthlyNewProducts : 0L);
        stats.setMonthlyNewUsers(monthlyNewUsers != null ? monthlyNewUsers : 0L);

        response.setStats(stats);
        log.debug("Dashboard stats: {}", stats);

        // 5. 获取最近订单
        List<RecentOrderDTO> recentOrders = orderMapper.getRecentOrders();
        log.debug("Recent orders: {}", recentOrders);
        response.setRecentOrders(recentOrders != null ? recentOrders : new ArrayList<>());

        // 6. 获取热销商品
        List<HotProductDTO> hotProducts = productMapper.getHotProducts();
        log.debug("Hot products: {}", hotProducts);
        response.setHotProducts(hotProducts != null ? hotProducts : new ArrayList<>());

        log.info("Finished building dashboard data response: {}", response);
        return response;
    }
} 