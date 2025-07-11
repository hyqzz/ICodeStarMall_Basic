/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seafoodmall.backend.entity.User;
import com.seafoodmall.backend.mapper.UserMapper;
import com.seafoodmall.backend.service.UserService;
import com.seafoodmall.backend.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.seafoodmall.backend.dto.LoginResponse;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public User register(User user) {
        // 检查用户名是否已存在
        if (checkUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认值
        user.setStatus(1);
        user.setUserType(0);
        user.setLevel(0); // 注册时设置默认用户等级为0 (普通用户)
        
        // 保存用户
        save(user);
        return user;
    }

    @Override
    public LoginResponse login(String username, String password) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));

        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new IllegalArgumentException("账号已被禁用");
        }
        
        // 使用 JwtTokenUtil 生成 token
        String token = jwtTokenUtil.generateToken(username);

        LoginResponse response = new LoginResponse();
        response.setUsername(user.getUsername());
        response.setToken(token);
        response.setRealName(user.getRealName()); // 设置真实姓名
        response.setLevel(user.getLevel());       // 设置用户等级
        return response;
    }

    @Override
    public User getById(Long id) {
        return super.getById(id);
    }

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0));
    }

    @Override
    public User update(User user) {
        // 如果修改了密码，需要重新加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        updateById(user);
        return user;
    }

    @Override
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public boolean checkUsername(String username) {
        return count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)) > 0;
    }

    @Override
    public Page<User> page(Page<User> page, User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (user != null) {
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                queryWrapper.like(User::getUsername, user.getUsername());
            }
            if (user.getStatus() != null) {
                queryWrapper.eq(User::getStatus, user.getStatus());
            }
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(User::getCreatedTime);
        
        return page(page, queryWrapper);
    }
} 