/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seafoodmall.backend.dto.CreateOrderRequest;
import com.seafoodmall.backend.entity.Order;
import com.seafoodmall.backend.entity.OrderItem;
import com.seafoodmall.backend.entity.Product;
import com.seafoodmall.backend.entity.UserAddress;
import com.seafoodmall.backend.entity.User;
import com.seafoodmall.backend.mapper.OrderMapper;
import com.seafoodmall.backend.mapper.OrderItemMapper;
import com.seafoodmall.backend.mapper.ProductMapper;
import com.seafoodmall.backend.mapper.UserAddressMapper;
import com.seafoodmall.backend.mapper.UserMapper;
import com.seafoodmall.backend.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Order createOrder(CreateOrderRequest createOrderRequest, Long userId) {
        // 1. 创建主订单
        Order order = new Order();
        order.setOrderNo("SN" + System.currentTimeMillis());
        order.setUserId(userId);
        order.setTotalAmount(createOrderRequest.getTotalAmount());
        // 设置支付方式
        if ("online".equals(createOrderRequest.getPaymentMethod())) {
            order.setPaymentType(1); // 在线支付
            order.setStatus(0); // 待付款
        } else if ("cod".equals(createOrderRequest.getPaymentMethod())) {
            order.setPaymentType(3); // 货到付款
            order.setStatus(1); // 货到付款直接待发货
        }
        order.setPaymentStatus(0); // 未支付
        order.setCreatedTime(LocalDateTime.now());
        order.setUpdatedTime(LocalDateTime.now());
        order.setAddressId(createOrderRequest.getAddressId());

        orderMapper.insert(order);

        // 2. 创建订单项
        List<OrderItem> orderItems = createOrderRequest.getOrderItems().stream()
                .map(itemDTO -> {
                    OrderItem orderItem = new OrderItem();
                    // 获取商品信息
                    Product product = productMapper.selectById(itemDTO.getProductId());
                    if (product == null) {
                        throw new IllegalArgumentException("商品不存在: " + itemDTO.getProductId());
                    }
                    
                    orderItem.setOrderId(order.getId());
                    orderItem.setProductId(product.getId());
                    orderItem.setProductName(product.getName());
                    orderItem.setPrice(product.getPrice());
                    orderItem.setQuantity(itemDTO.getQuantity());
                    orderItem.setTotalAmount(product.getPrice().multiply(new BigDecimal(itemDTO.getQuantity())));
                    orderItem.setCreatedTime(LocalDateTime.now());
                    orderItem.setUpdatedTime(LocalDateTime.now());
                    // 新增：商品图片和单位
                    orderItem.setProductImage(product.getImage());
                    orderItem.setUnit(product.getUnit());
                    
                    return orderItem;
                }).collect(Collectors.toList());

        // 批量插入订单项
        orderItems.forEach(orderItemMapper::insert);

        // 返回带明细的Order
        order.setItems(orderItems);
        return order;
    }

    @Override
    public Order update(Order order) {
        updateById(order);
        return order;
    }

    @Override
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public Order getById(Long id) {
        Order order = super.getById(id);
        if (order != null) {
            List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));
            order.setItems(items);
            
            // 查询并设置用户名
            if (order.getUserId() != null) {
                try {
                    User user = userMapper.selectById(order.getUserId());
                    if (user != null) {
                        order.setUsername(user.getUsername());
                    }
                } catch (Exception e) {
                    log.warn("查询用户信息失败，用户ID: {}, 错误: {}", order.getUserId(), e.getMessage());
                }
            }
            
            // 查询并拼接详细地址
            if (order.getAddressId() != null) {
                UserAddress address = null;
                try {
                    address = userAddressMapper.selectById(order.getAddressId());
                } catch (Exception e) {
                    log.warn("查询用户地址失败: {}", e.getMessage());
                }
                if (address != null) {
                    // 设置收货人信息
                    order.setConsigneeName(address.getConsigneeName());
                    order.setConsigneePhone(address.getConsigneePhone());
                    
                    // 拼接详细地址
                    StringBuilder sb = new StringBuilder();
                    if (address.getProvince() != null) sb.append(address.getProvince());
                    if (address.getCity() != null) sb.append(address.getCity());
                    if (address.getDistrict() != null) sb.append(address.getDistrict());
                    if (address.getDetailAddress() != null) sb.append(address.getDetailAddress());
                    order.setAddressDetail(sb.toString());
                }
            }
        }
        return order;
    }

    @Override
    public Page<Order> page(Page<Order> page, Order order) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        log.info("OrderServiceImpl.page - Starting query with order: {}", order);
        
        if (order != null) {
            if (StringUtils.hasText(order.getOrderNo())) {
                wrapper.like(Order::getOrderNo, order.getOrderNo());
                log.info("Added orderNo filter: {}", order.getOrderNo());
            }
            if (order.getUserId() != null) {
                wrapper.eq(Order::getUserId, order.getUserId());
                log.info("Added userId filter: {}", order.getUserId());
            } else {
                log.info("No userId filter applied - will query all orders");
            }
            if (order.getPaymentStatus() != null) {
                wrapper.eq(Order::getPaymentStatus, order.getPaymentStatus());
                log.info("Added paymentStatus filter: {}", order.getPaymentStatus());
            }
            if (order.getStatus() != null) {
                wrapper.eq(Order::getStatus, order.getStatus());
                log.info("Added status filter: {}", order.getStatus());
            }
        } else {
            log.info("No order filter applied - will query all orders");
        }
        
        wrapper.orderByDesc(Order::getCreatedTime);
        
        Page<Order> result = page(page, wrapper);
        log.info("OrderServiceImpl.page - Query completed, found {} orders, total: {}", 
                result.getRecords().size(), result.getTotal());
        
        // 为每个订单设置用户名
        for (Order orderItem : result.getRecords()) {
            if (orderItem.getUserId() != null) {
                try {
                    User user = userMapper.selectById(orderItem.getUserId());
                    if (user != null) {
                        orderItem.setUsername(user.getUsername());
                    }
                } catch (Exception e) {
                    log.warn("查询用户信息失败，用户ID: {}, 错误: {}", orderItem.getUserId(), e.getMessage());
                }
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public void pay(Long id, Integer paymentType) {
        Order order = getById(id);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        if (order.getPaymentStatus() == 1) {
            throw new IllegalArgumentException("订单已支付");
        }
        
        if (order.getStatus() != 0) {
            throw new IllegalArgumentException("订单状态不正确");
        }
        
        order.setPaymentType(paymentType);
        order.setPaymentStatus(1);
        order.setStatus(1);
        updateById(order);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Order order = getById(id);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new IllegalArgumentException("订单状态不正确");
        }
        
        order.setStatus(4);
        updateById(order);
    }

    @Override
    @Transactional
    public void complete(Long id) {
        Order order = getById(id);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        if (order.getStatus() != 2) {
            throw new IllegalArgumentException("订单状态不正确");
        }
        
        order.setStatus(3);
        updateById(order);
    }

    @Override
    public long count(Wrapper<Order> wrapper) {
        return super.count(wrapper);
    }
} 