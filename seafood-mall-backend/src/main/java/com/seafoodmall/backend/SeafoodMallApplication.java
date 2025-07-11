/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import com.seafoodmall.backend.service.impl.UserServiceImpl;

@SpringBootApplication
@MapperScan("com.seafoodmall.backend.mapper")
public class SeafoodMallApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeafoodMallApplication.class, args);
    }
} 