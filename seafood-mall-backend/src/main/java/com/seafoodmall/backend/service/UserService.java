/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seafoodmall.backend.entity.User;
import com.seafoodmall.backend.dto.LoginResponse;

public interface UserService {
    User register(User user);
    
    LoginResponse login(String username, String password);
    
    User getById(Long id);
    
    User getByUsername(String username);
    
    User update(User user);
    
    void delete(Long id);
    
    boolean checkUsername(String username);
    
    Page<User> page(Page<User> page, User user);
} 