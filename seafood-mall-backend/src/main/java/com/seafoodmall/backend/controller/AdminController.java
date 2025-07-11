/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.controller;

import com.seafoodmall.backend.common.Result;
import com.seafoodmall.backend.dto.AdminLoginRequest;
import com.seafoodmall.backend.dto.AdminLoginResponse;
import com.seafoodmall.backend.dto.AdminRegisterRequest;
import com.seafoodmall.backend.service.AdminUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminUserService adminUserService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody AdminRegisterRequest request) {
        log.info("API Call: /api/admin/register - Admin registration attempt for username: {}", request.getUsername());
        adminUserService.register(request);
        Result<Void> result = Result.success();
        log.info("API Response: /api/admin/register - Admin registration successful.");
        return result;
    }

    @PostMapping("/login")
    public Result<AdminLoginResponse> login(@RequestBody AdminLoginRequest request) {
        log.info("API Call: /api/admin/login - Admin login attempt for username: {}", request.getUsername());
        AdminLoginResponse response = adminUserService.login(request);
        Result<AdminLoginResponse> result = Result.success(response);
        log.info("API Response: /api/admin/login - Admin login successful for username: {}", response.getUsername());
        return result;
    }
} 