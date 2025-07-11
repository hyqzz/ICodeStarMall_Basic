/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafoodmall.backend.common.Result;
import com.seafoodmall.backend.entity.Order;
import com.seafoodmall.backend.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 管理员订单管理控制器
 * 专门为管理员提供订单管理功能，路径以 /api/admin/ 开头
 * 确保只有管理员可以访问这些接口
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 管理员分页查询订单
     * 可以查看所有订单，也可以按用户ID或状态筛选
     */
    @GetMapping("/page")
    public Result<Page<Order>> getOrderPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String orderNo) {
        
        log.info("Admin API Call: /api/admin/orders/page - Getting order page with current:{}, size:{}, userId:{}, status:{}, orderNo:{}", 
                current, size, userId, status, orderNo);
        
        Page<Order> page = new Page<>(current, size);
        Order queryOrder = new Order();
        
        // 设置查询条件
        if (userId != null) {
            queryOrder.setUserId(userId);
            log.info("Admin filtering orders by user ID: {}", userId);
        }
        
        if (status != null) {
            queryOrder.setStatus(status);
            log.info("Admin filtering orders by status: {}", status);
        }
        
        if (orderNo != null && !orderNo.trim().isEmpty()) {
            queryOrder.setOrderNo(orderNo.trim());
            log.info("Admin filtering orders by order number: {}", orderNo);
        }
        
        Result<Page<Order>> result = Result.success(orderService.page(page, queryOrder));
        log.info("Admin API Response: /api/admin/orders/page - Order page fetched, total: {}", result.getData().getTotal());
        return result;
    }

    /**
     * 管理员查看订单详情
     */
    @GetMapping("/{id}")
    public Result<Order> getOrder(@PathVariable Long id) {
        log.info("Admin API Call: /api/admin/orders/{} - Getting order details", id);
        
        Order order = orderService.getById(id);
        if (order == null) {
            log.warn("Admin API Response: /api/admin/orders/{} - Order not found", id);
            return Result.error("订单不存在");
        }
        
        Result<Order> result = Result.success(order);
        log.info("Admin API Response: /api/admin/orders/{} - Order details fetched, status: {}", id, result.getData().getStatus());
        return result;
    }

    /**
     * 管理员更新订单信息
     */
    @PutMapping("/{id}")
    public Result<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        log.info("Admin API Call: /api/admin/orders/{} - Updating order", id);
        
        Order existingOrder = orderService.getById(id);
        if (existingOrder == null) {
            log.warn("Admin API Response: /api/admin/orders/{} - Order not found", id);
            return Result.error("订单不存在");
        }
        
        // 设置ID，确保更新的是正确的订单
        order.setId(id);
        
        // 验证状态值是否有效
        if (order.getStatus() != null && (order.getStatus() < 0 || order.getStatus() > 4)) {
            log.warn("Admin API Response: /api/admin/orders/{} - Invalid status value: {}", id, order.getStatus());
            return Result.error("无效的订单状态");
        }
        
        // 验证金额是否有效
        if (order.getTotalAmount() != null && order.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Admin API Response: /api/admin/orders/{} - Invalid total amount: {}", id, order.getTotalAmount());
            return Result.error("无效的订单金额");
        }
        
        Result<Order> result = Result.success(orderService.update(order));
        log.info("Admin API Response: /api/admin/orders/{} - Order updated successfully", id);
        return result;
    }

    /**
     * 管理员更新订单状态
     */
    @PutMapping("/{id}/status")
    public Result<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        log.info("Admin API Call: /api/admin/orders/{}/status - Updating order status to: {}", id, status);
        
        Order order = orderService.getById(id);
        if (order == null) {
            log.warn("Admin API Response: /api/admin/orders/{}/status - Order not found", id);
            return Result.error("订单不存在");
        }
        
        // 验证状态值是否有效
        if (status < 0 || status > 4) {
            log.warn("Admin API Response: /api/admin/orders/{}/status - Invalid status value: {}", id, status);
            return Result.error("无效的订单状态");
        }
        
        order.setStatus(status);
        Result<Order> result = Result.success(orderService.update(order));
        log.info("Admin API Response: /api/admin/orders/{}/status - Order status updated successfully", id);
        return result;
    }

    /**
     * 管理员删除订单
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(@PathVariable Long id) {
        log.info("Admin API Call: /api/admin/orders/{} - Deleting order", id);
        
        Order order = orderService.getById(id);
        if (order == null) {
            log.warn("Admin API Response: /api/admin/orders/{} - Order not found", id);
            return Result.error("订单不存在");
        }
        
        orderService.delete(id);
        Result<Void> result = Result.success();
        log.info("Admin API Response: /api/admin/orders/{} - Order deleted successfully", id);
        return result;
    }

    /**
     * 管理员批量更新订单状态
     */
    @PutMapping("/batch/status")
    public Result<Void> batchUpdateOrderStatus(
            @RequestParam Long[] orderIds,
            @RequestParam Integer status) {
        log.info("Admin API Call: /api/admin/orders/batch/status - Batch updating {} orders to status: {}", orderIds.length, status);
        
        // 验证状态值是否有效
        if (status < 0 || status > 4) {
            log.warn("Admin API Response: /api/admin/orders/batch/status - Invalid status value: {}", status);
            return Result.error("无效的订单状态");
        }
        
        int updatedCount = 0;
        for (Long orderId : orderIds) {
            Order order = orderService.getById(orderId);
            if (order != null) {
                order.setStatus(status);
                orderService.update(order);
                updatedCount++;
            }
        }
        
        Result<Void> result = Result.success();
        log.info("Admin API Response: /api/admin/orders/batch/status - Successfully updated {} out of {} orders", updatedCount, orderIds.length);
        return result;
    }

    /**
     * 管理员获取订单统计信息
     */
    @GetMapping("/statistics")
    public Result<Object> getOrderStatistics() {
        log.info("Admin API Call: /api/admin/orders/statistics - Getting order statistics");
        
        // 使用LambdaQueryWrapper来构建查询条件
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        // 统计总订单数
        long totalCount = orderService.count(wrapper);
        
        // 统计各状态订单数量
        wrapper.clear();
        wrapper.eq(Order::getStatus, 0); // 待付款
        long pendingPaymentCount = orderService.count(wrapper);
        
        wrapper.clear();
        wrapper.eq(Order::getStatus, 1); // 待发货
        long pendingShipmentCount = orderService.count(wrapper);
        
        wrapper.clear();
        wrapper.eq(Order::getStatus, 2); // 已发货
        long shippedCount = orderService.count(wrapper);
        
        wrapper.clear();
        wrapper.eq(Order::getStatus, 3); // 已完成
        long completedCount = orderService.count(wrapper);
        
        wrapper.clear();
        wrapper.eq(Order::getStatus, 4); // 已取消
        long cancelledCount = orderService.count(wrapper);
        
        var statistics = new Object() {
            public final long totalOrders = totalCount;
            public final long pendingPayment = pendingPaymentCount;
            public final long pendingShipment = pendingShipmentCount;
            public final long shipped = shippedCount;
            public final long completed = completedCount;
            public final long cancelled = cancelledCount;
        };
        
        Result<Object> result = Result.success(statistics);
        log.info("Admin API Response: /api/admin/orders/statistics - Statistics fetched successfully");
        return result;
    }
} 