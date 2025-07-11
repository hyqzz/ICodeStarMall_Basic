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
import com.seafoodmall.backend.dto.CreateOrderRequest;
import com.seafoodmall.backend.entity.Order;
import com.seafoodmall.backend.entity.User;
import com.seafoodmall.backend.service.OrderService;
import com.seafoodmall.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<Order> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        // 获取当前认证用户的用户名，然后查询用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        // 通过用户名查询用户信息获取用户ID
        User currentUser = userService.getByUsername(username);
        if (currentUser == null) {
            log.warn("API Response: /api/orders - Current user not found: {}", username);
            return Result.error("用户信息不存在");
        }
        
        Long userId = currentUser.getId();

        log.info("API Call: /api/orders - Creating order for user: {} (ID: {})", username, userId);
        Order createdOrder = orderService.createOrder(createOrderRequest, userId);
        Result<Order> result = Result.success(createdOrder);
        log.info("API Response: /api/orders - Order created with ID: {}", result.getData().getId());
        return result;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Result<Order> getOrder(@PathVariable Long id) {
        // 获取当前认证用户的用户名，然后查询用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        // 通过用户名查询用户信息获取用户ID
        User currentUser = userService.getByUsername(username);
        if (currentUser == null) {
            log.warn("API Response: /api/orders/{} - Current user not found: {}", id, username);
            return Result.error("用户信息不存在");
        }
        
        Long currentUserId = currentUser.getId();
        
        log.info("API Call: /api/orders/{} - Getting order by ID for user: {} (ID: {})", id, username, currentUserId);
        
        Order order = orderService.getById(id);
        if (order == null) {
            log.warn("API Response: /api/orders/{} - Order not found", id);
            return Result.error("订单不存在");
        }
        
        // 验证订单是否属于当前用户
        if (!order.getUserId().equals(currentUserId)) {
            log.warn("API Response: /api/orders/{} - Access denied for user: {}, order belongs to user: {}", 
                    id, currentUserId, order.getUserId());
            return Result.error("无权访问此订单");
        }
        
        Result<Order> result = Result.success(order);
        log.info("API Response: /api/orders/{} - Order fetched, status: {}", id, result.getData().getStatus());
        return result;
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('USER')")
    public Result<Page<Order>> getOrderPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        
        // 获取当前认证用户的用户名，然后查询用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        // 通过用户名查询用户信息获取用户ID
        User currentUser = userService.getByUsername(username);
        if (currentUser == null) {
            log.warn("API Response: /api/orders/page - Current user not found: {}", username);
            return Result.error("用户信息不存在");
        }
        
        Long currentUserId = currentUser.getId();
        
        log.info("API Call: /api/orders/page - Getting order page with current:{}, size:{}, status:{}, userId:{}", 
                current, size, status, currentUserId);
        
        Page<Order> page = new Page<>(current, size);
        Order queryOrder = new Order();
        
        // 普通用户只能查看自己的订单
        queryOrder.setUserId(currentUserId);
        log.info("User {} (ID: {}) accessing orders, filtering by user ID: {}", username, currentUserId, currentUserId);
        
        if (status != null) {
            queryOrder.setStatus(status);
        }
        
        Result<Page<Order>> result = Result.success(orderService.page(page, queryOrder));
        log.info("API Response: /api/orders/page - Order page fetched, total: {}", result.getData().getTotal());
        return result;
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> pay(@PathVariable Long id, @RequestParam Integer paymentType) {
        // 获取当前认证用户的用户名，然后查询用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        // 通过用户名查询用户信息获取用户ID
        User currentUser = userService.getByUsername(username);
        if (currentUser == null) {
            log.warn("API Response: /api/orders/{}/pay - Current user not found: {}", id, username);
            return Result.error("用户信息不存在");
        }
        
        Long currentUserId = currentUser.getId();
        
        log.info("API Call: /api/orders/{}/pay - Paying for order ID: {} with payment type: {} by user: {} (ID: {})", id, id, paymentType, username, currentUserId);
        
        Order order = orderService.getById(id);
        if (order == null) {
            log.warn("API Response: /api/orders/{}/pay - Order not found", id);
            return Result.error("订单不存在");
        }
        
        // 验证订单是否属于当前用户
        if (!order.getUserId().equals(currentUserId)) {
            log.warn("API Response: /api/orders/{}/pay - Access denied for user: {}, order belongs to user: {}", 
                    id, currentUserId, order.getUserId());
            return Result.error("无权操作此订单");
        }
        
        orderService.pay(id, paymentType);
        Result<Void> result = Result.success();
        log.info("API Response: /api/orders/{}/pay - Order paid.", id);
        return result;
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> cancel(@PathVariable Long id) {
        // 获取当前认证用户的用户名，然后查询用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        // 通过用户名查询用户信息获取用户ID
        User currentUser = userService.getByUsername(username);
        if (currentUser == null) {
            log.warn("API Response: /api/orders/{}/cancel - Current user not found: {}", id, username);
            return Result.error("用户信息不存在");
        }
        
        Long currentUserId = currentUser.getId();
        
        log.info("API Call: /api/orders/{}/cancel - Cancelling order ID: {} by user: {} (ID: {})", id, id, username, currentUserId);
        
        Order order = orderService.getById(id);
        if (order == null) {
            log.warn("API Response: /api/orders/{}/cancel - Order not found", id);
            return Result.error("订单不存在");
        }
        
        // 验证订单是否属于当前用户
        if (!order.getUserId().equals(currentUserId)) {
            log.warn("API Response: /api/orders/{}/cancel - Access denied for user: {}, order belongs to user: {}", 
                    id, currentUserId, order.getUserId());
            return Result.error("无权操作此订单");
        }
        
        orderService.cancel(id);
        Result<Void> result = Result.success();
        log.info("API Response: /api/orders/{}/cancel - Order cancelled.", id);
        return result;
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> complete(@PathVariable Long id) {
        // 获取当前认证用户的用户名，然后查询用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        // 通过用户名查询用户信息获取用户ID
        User currentUser = userService.getByUsername(username);
        if (currentUser == null) {
            log.warn("API Response: /api/orders/{}/complete - Current user not found: {}", id, username);
            return Result.error("用户信息不存在");
        }
        
        Long currentUserId = currentUser.getId();
        
        log.info("API Call: /api/orders/{}/complete - Completing order ID: {} by user: {} (ID: {})", id, id, username, currentUserId);
        
        Order order = orderService.getById(id);
        if (order == null) {
            log.warn("API Response: /api/orders/{}/complete - Order not found", id);
            return Result.error("订单不存在");
        }
        
        // 验证订单是否属于当前用户
        if (!order.getUserId().equals(currentUserId)) {
            log.warn("API Response: /api/orders/{}/complete - Access denied for user: {}, order belongs to user: {}", 
                    id, currentUserId, order.getUserId());
            return Result.error("无权操作此订单");
        }
        
        orderService.complete(id);
        Result<Void> result = Result.success();
        log.info("API Response: /api/orders/{}/complete - Order completed.", id);
        return result;
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public Result<Page<Order>> getMyOrders(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        
        // 获取当前认证用户的用户名，然后查询用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        // 通过用户名查询用户信息获取用户ID
        User currentUser = userService.getByUsername(username);
        if (currentUser == null) {
            log.warn("API Response: /api/orders/my-orders - Current user not found: {}", username);
            return Result.error("用户信息不存在");
        }
        
        Long currentUserId = currentUser.getId();
        
        log.info("API Call: /api/orders/my-orders - Getting my orders with current:{}, size:{}, orderStatus:{}, user:{} (ID: {})", 
                current, size, status, username, currentUserId);
        
        Page<Order> page = new Page<>(current, size);
        Order queryOrder = new Order();
        queryOrder.setUserId(currentUserId); // 强制设置为当前用户的订单
        
        if (status != null) {
            queryOrder.setStatus(status);
        }
        
        Result<Page<Order>> result = Result.success(orderService.page(page, queryOrder));
        log.info("API Response: /api/orders/my-orders - My orders fetched, total: {}", result.getData().getTotal());
        return result;
    }
} 