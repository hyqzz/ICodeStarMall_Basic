/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.service;

import com.seafoodmall.backend.dto.AdminLoginRequest;
import com.seafoodmall.backend.dto.AdminLoginResponse;
import com.seafoodmall.backend.dto.AdminRegisterRequest;
import com.seafoodmall.backend.entity.AdminUser;

public interface AdminUserService {
    AdminUser register(AdminRegisterRequest request);
    AdminLoginResponse login(AdminLoginRequest request);
    AdminUser getAdminUserById(Long id);
    AdminUser getAdminUserByUsername(String username);
} 