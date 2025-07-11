/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seafoodmall.backend.entity.StoredValueCard;
import com.seafoodmall.backend.mapper.StoredValueCardMapper;
import com.seafoodmall.backend.service.StoredValueCardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class StoredValueCardServiceImpl extends ServiceImpl<StoredValueCardMapper, StoredValueCard> implements StoredValueCardService {

    @Override
    public StoredValueCard create(StoredValueCard card) {
        // 生成卡号
        card.setCardNo(generateCardNo());
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(1);
        
        save(card);
        return card;
    }

    @Override
    public StoredValueCard update(StoredValueCard card) {
        updateById(card);
        return card;
    }

    @Override
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public StoredValueCard getById(Long id) {
        return super.getById(id);
    }

    @Override
    public StoredValueCard getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<StoredValueCard>()
                .eq(StoredValueCard::getUserId, userId));
    }

    @Override
    @Transactional
    public void recharge(Long id, BigDecimal amount) {
        StoredValueCard card = getById(id);
        if (card == null) {
            throw new IllegalArgumentException("储值卡不存在");
        }
        
        if (card.getStatus() == 0) {
            throw new IllegalArgumentException("储值卡已禁用");
        }
        
        card.setBalance(card.getBalance().add(amount));
        updateById(card);
    }

    @Override
    @Transactional
    public void deduct(Long id, BigDecimal amount) {
        StoredValueCard card = getById(id);
        if (card == null) {
            throw new IllegalArgumentException("储值卡不存在");
        }
        
        if (card.getStatus() == 0) {
            throw new IllegalArgumentException("储值卡已禁用");
        }
        
        if (card.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("储值卡余额不足");
        }
        
        card.setBalance(card.getBalance().subtract(amount));
        updateById(card);
    }
    
    private String generateCardNo() {
        // 生成16位卡号
        StringBuilder cardNo = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNo.append((int) (Math.random() * 10));
        }
        return cardNo.toString();
    }
} 