/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.common;

import lombok.Data;
 
@Data
public class PageRequest {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
} 