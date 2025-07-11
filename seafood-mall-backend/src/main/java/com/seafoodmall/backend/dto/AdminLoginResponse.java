/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.dto;

import lombok.Data;

@Data
public class AdminLoginResponse {
    private Long id;
    private String username;
    private String realName;
    private String token;
} 