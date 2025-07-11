/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.controller;

import com.seafoodmall.backend.common.Result;
import com.seafoodmall.backend.entity.UserAddress;
import com.seafoodmall.backend.entity.User;
import com.seafoodmall.backend.service.UserAddressService;
import com.seafoodmall.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/addresses")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private UserService userService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            // 兼容 Spring Security UserDetails（如 org.springframework.security.core.userdetails.User）
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            // 通过用户名查询用户信息获取用户ID
            User currentUser = userService.getByUsername(username);
            if (currentUser == null) {
                throw new RuntimeException("无法获取当前用户信息: " + username);
            }
            return currentUser.getId();
        } else if (principal instanceof String) {
            String username = (String) principal;
            // 通过用户名查询用户信息获取用户ID
            User currentUser = userService.getByUsername(username);
            if (currentUser == null) {
                throw new RuntimeException("无法获取当前用户信息: " + username);
            }
            return currentUser.getId();
        } else {
            throw new RuntimeException("无法获取当前用户ID: " + principal.getClass().getName());
        }
    }

    /**
     * 获取当前用户的所有地址
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Result<List<UserAddress>> getMyAddresses() {
        Long userId = getCurrentUserId();
        List<UserAddress> addresses = userAddressService.getAddressesByUserId(userId);
        return Result.success(addresses);
    }

    /**
     * 获取当前用户的默认地址
     */
    @GetMapping("/default")
    @PreAuthorize("hasRole('USER')")
    public Result<UserAddress> getDefaultAddress() {
        Long userId = getCurrentUserId();
        UserAddress defaultAddress = userAddressService.getDefaultAddress(userId);
        return Result.success(defaultAddress);
    }

    /**
     * 添加新地址
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<UserAddress> addAddress(@RequestBody UserAddress userAddress) {
        Long userId = getCurrentUserId();
        UserAddress newAddress = userAddressService.addAddress(userAddress, userId);
        return Result.success(newAddress);
    }

    /**
     * 更新地址
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Result<UserAddress> updateAddress(@PathVariable Long id, @RequestBody UserAddress userAddress) {
        Long userId = getCurrentUserId();
        userAddress.setId(id);
        UserAddress updatedAddress = userAddressService.updateAddress(userAddress, userId);
        return Result.success(updatedAddress);
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deleteAddress(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        userAddressService.deleteAddress(id, userId);
        return Result.success();
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/set-default/{id}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> setDefaultAddress(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        userAddressService.setDefaultAddress(id, userId);
        return Result.success();
    }

    /**
     * 获取当前用户的指定地址
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Result<UserAddress> getAddressById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        UserAddress address = userAddressService.getAddressById(id, userId);
        return Result.success(address);
    }
} 