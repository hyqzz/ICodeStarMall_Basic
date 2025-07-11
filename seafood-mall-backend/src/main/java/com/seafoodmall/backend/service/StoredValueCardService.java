/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.service;

import com.seafoodmall.backend.entity.StoredValueCard;

public interface StoredValueCardService {
    StoredValueCard create(StoredValueCard card);
    
    StoredValueCard update(StoredValueCard card);
    
    void delete(Long id);
    
    StoredValueCard getById(Long id);
    
    StoredValueCard getByUserId(Long userId);
    
    void recharge(Long id, java.math.BigDecimal amount);
    
    void deduct(Long id, java.math.BigDecimal amount);
} 