/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafoodmall.backend.common.Result;
import com.seafoodmall.backend.entity.User;
import com.seafoodmall.backend.service.UserService;
import com.seafoodmall.backend.dto.LoginRequest;
import com.seafoodmall.backend.dto.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        log.info("API Call: /api/users/register - Registering user: {}", user.getUsername());
        Result<User> result = Result.success(userService.register(user));
        log.info("API Response: /api/users/register - User registered: {}", result.getData().getUsername());
        return result;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("API Call: /api/users/login - User login attempt: {}", loginRequest.getUsername());
        Result<LoginResponse> result = Result.success(userService.login(loginRequest.getUsername(), loginRequest.getPassword()));
        log.info("API Response: /api/users/login - User logged in: {}", result.getData().getUsername());
        return result;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Result<User> getById(@PathVariable Long id) {
        // 获取当前认证用户的角色
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        log.info("API Call: /api/users/{} - Getting user by ID, isAdmin:{}", id, isAdmin);
        
        // 如果不是管理员，只能查看自己的信息
        if (!isAdmin) {
            // 获取当前用户的用户名，然后查询用户ID
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            
            // 通过用户名查询用户信息获取用户ID
            User currentUser = userService.getByUsername(username);
            if (currentUser == null) {
                log.warn("API Response: /api/users/{} - Current user not found: {}", id, username);
                return Result.error("用户信息不存在");
            }
            
            Long currentUserId = currentUser.getId();
            if (!currentUserId.equals(id)) {
                log.warn("API Response: /api/users/{} - Access denied for user: {}, trying to access user: {}", id, currentUserId, id);
                return Result.error("无权访问此用户信息");
            }
        }
        
        User user = userService.getById(id);
        if (user == null) {
            log.warn("API Response: /api/users/{} - User not found", id);
            return Result.error("用户不存在");
        }
        
        Result<User> result = Result.success(user);
        log.info("API Response: /api/users/{} - User fetched: {}", id, result.getData().getUsername());
        return result;
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<User>> getUserPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        
        log.info("API Call: /api/users/page - Getting user page with current:{}, size:{}, username:{}, status:{}", current, size, username, status);
        Page<User> page = new Page<>(current, size);
        User queryUser = new User();
        if (username != null && !username.isEmpty()) {
            queryUser.setUsername(username);
        }
        if (status != null) {
            queryUser.setStatus(status);
        }
        
        Result<Page<User>> result = Result.success(userService.page(page, queryUser));
        log.info("API Response: /api/users/page - User page fetched, total: {}", result.getData().getTotal());
        return result;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Result<User> update(@PathVariable Long id, @RequestBody User user) {
        // 获取当前认证用户的角色
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        log.info("API Call: /api/users/{} - Updating user: {}, isAdmin:{}", id, user.getUsername(), isAdmin);
        
        // 如果不是管理员，只能更新自己的信息
        if (!isAdmin) {
            // 获取当前用户的用户名，然后查询用户ID
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            
            // 通过用户名查询用户信息获取用户ID
            User currentUser = userService.getByUsername(username);
            if (currentUser == null) {
                log.warn("API Response: /api/users/{} - Current user not found: {}", id, username);
                return Result.error("用户信息不存在");
            }
            
            Long currentUserId = currentUser.getId();
            if (!currentUserId.equals(id)) {
                log.warn("API Response: /api/users/{} - Access denied for user: {}, trying to update user: {}", id, currentUserId, id);
                return Result.error("无权更新此用户信息");
            }
        }
        
        user.setId(id);
        Result<User> result = Result.success(userService.update(user));
        log.info("API Response: /api/users/{} - User updated: {}", id, result.getData().getUsername());
        return result;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("API Call: /api/users/{} - Deleting user by ID", id);
        userService.delete(id);
        Result<Void> result = Result.success();
        log.info("API Response: /api/users/{} - User deleted.", id);
        return result;
    }

    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        log.info("API Call: /api/users/check-username - Checking username: {}", username);
        Result<Boolean> result = Result.success(userService.checkUsername(username));
        log.info("API Response: /api/users/check-username - Username available: {}", result.getData());
        return result;
    }
} 