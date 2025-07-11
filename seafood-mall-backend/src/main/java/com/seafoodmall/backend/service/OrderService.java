/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafoodmall.backend.dto.CreateOrderRequest;
import com.seafoodmall.backend.entity.Order;

public interface OrderService {
    /**
     * 创建订单
     *
     * @param createOrderRequest 订单创建请求
     * @param userId 用户ID
     * @return 创建成功的订单
     */
    Order createOrder(CreateOrderRequest createOrderRequest, Long userId);
    
    Order update(Order order);
    
    void delete(Long id);
    
    Order getById(Long id);
    
    Page<Order> page(Page<Order> page, Order order);
    
    void pay(Long id, Integer paymentType);
    
    void cancel(Long id);
    
    void complete(Long id);
    
    /**
     * 统计订单数量
     *
     * @param wrapper 查询条件
     * @return 订单数量
     */
    long count(Wrapper<Order> wrapper);
} 