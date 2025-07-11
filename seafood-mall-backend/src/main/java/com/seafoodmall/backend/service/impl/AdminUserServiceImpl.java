/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seafoodmall.backend.dto.AdminLoginRequest;
import com.seafoodmall.backend.dto.AdminLoginResponse;
import com.seafoodmall.backend.dto.AdminRegisterRequest;
import com.seafoodmall.backend.entity.AdminUser;
import com.seafoodmall.backend.mapper.AdminUserMapper;
import com.seafoodmall.backend.service.AdminUserService;
import com.seafoodmall.backend.util.JwtTokenUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("adminAuthenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public AdminUser register(AdminRegisterRequest request) {
        // 检查用户名是否已存在
        if (adminUserMapper.selectOne(new QueryWrapper<AdminUser>().eq("username", request.getUsername())) != null) {
            throw new RuntimeException("Username already exists");
        }

        AdminUser adminUser = new AdminUser();
        BeanUtils.copyProperties(request, adminUser);
        adminUser.setPassword(passwordEncoder.encode(request.getPassword())); // 密码加密
        adminUser.setRole(0); // 默认普通管理员
        adminUser.setStatus(1); // 默认启用

        adminUserMapper.insert(adminUser);
        return adminUser;
    }

    @Override
    public AdminLoginResponse login(AdminLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AdminUser adminUser = adminUserMapper.selectOne(new QueryWrapper<AdminUser>().eq("username", userDetails.getUsername()));
        
        if (adminUser == null) {
            throw new RuntimeException("Admin user not found after authentication."); // Should not happen if authentication successful
        }

        String token = jwtTokenUtil.generateToken(adminUser.getUsername());

        AdminLoginResponse response = new AdminLoginResponse();
        response.setId(adminUser.getId());
        response.setUsername(adminUser.getUsername());
        response.setRealName(adminUser.getRealName());
        response.setToken(token);

        return response;
    }

    @Override
    public AdminUser getAdminUserById(Long id) {
        return adminUserMapper.selectById(id);
    }

    @Override
    public AdminUser getAdminUserByUsername(String username) {
        return adminUserMapper.selectOne(new QueryWrapper<AdminUser>().eq("username", username));
    }
} 