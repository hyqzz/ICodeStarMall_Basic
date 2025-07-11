/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.config;

import com.seafoodmall.backend.entity.User;
import com.seafoodmall.backend.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminAccountInitializer {

    @Bean
    public CommandLineRunner initAdminAccount(UserService userService, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if superadmin exists
            if (!userService.checkUsername("superadmin")) {
                User superAdmin = new User();
                superAdmin.setUsername("superadmin");
                superAdmin.setPassword(passwordEncoder.encode("admin123")); // Default password
                superAdmin.setRealName("超级管理员");
                superAdmin.setPhone("13800000000");
                superAdmin.setEmail("superadmin@example.com");
                superAdmin.setUserType(1); // 1 for super admin
                superAdmin.setStatus(1); // 1 for active
                
                userService.register(superAdmin);
                System.out.println("Super administrator account 'superadmin' created automatically.");
            }
        };
    }
} 