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
public class LoginResponse {
    private String username;
    private String token;
    private String realName;
    private Integer level;
} 