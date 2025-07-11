/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.controller;

import com.seafoodmall.backend.common.Result;
import com.seafoodmall.backend.entity.StoredValueCard;
import com.seafoodmall.backend.entity.User;
import com.seafoodmall.backend.service.StoredValueCardService;
import com.seafoodmall.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/stored-value-cards")
public class StoredValueCardController {

    @Autowired
    private StoredValueCardService storedValueCardService;

    @Autowired
    private UserService userService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            // 通过用户名查询用户信息获取用户ID
            User currentUser = userService.getByUsername(username);
            if (currentUser == null) {
                throw new RuntimeException("无法获取当前用户信息: " + username);
            }
            return currentUser.getId();
        } else if (principal instanceof String) {
            String username = (String) principal;
            // 通过用户名查询用户信息获取用户ID
            User currentUser = userService.getByUsername(username);
            if (currentUser == null) {
                throw new RuntimeException("无法获取当前用户信息: " + username);
            }
            return currentUser.getId();
        } else {
            throw new RuntimeException("无法获取当前用户ID: " + principal.getClass().getName());
        }
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Result<StoredValueCard> create(@RequestBody StoredValueCard card) {
        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();
        
        // 如果不是管理员，只能为自己的用户ID创建储值卡
        if (!isAdmin) {
            card.setUserId(currentUserId);
        }
        
        log.info("API Call: /api/stored-value-cards - Creating stored value card for user: {} by user: {}", card.getUserId(), currentUserId);
        Result<StoredValueCard> result = Result.success(storedValueCardService.create(card));
        log.info("API Response: /api/stored-value-cards - Stored value card created with ID: {}", result.getData().getId());
        return result;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Result<StoredValueCard> update(@PathVariable Long id, @RequestBody StoredValueCard card) {
        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();
        
        // 验证储值卡是否属于当前用户或管理员
        StoredValueCard existingCard = storedValueCardService.getById(id);
        if (existingCard == null) {
            log.warn("API Response: /api/stored-value-cards/{} - Card not found", id);
            return Result.error("储值卡不存在");
        }
        
        if (!isAdmin && !existingCard.getUserId().equals(currentUserId)) {
            log.warn("API Response: /api/stored-value-cards/{} - Access denied for user: {}, card belongs to user: {}", 
                    id, currentUserId, existingCard.getUserId());
            return Result.error("无权操作此储值卡");
        }
        
        log.info("API Call: /api/stored-value-cards/{} - Updating stored value card ID: {} by user: {}", id, id, currentUserId);
        card.setId(id);
        Result<StoredValueCard> result = Result.success(storedValueCardService.update(card));
        log.info("API Response: /api/stored-value-cards/{} - Stored value card updated.", id);
        return result;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("API Call: /api/stored-value-cards/{} - Deleting stored value card ID: {}", id, id);
        storedValueCardService.delete(id);
        Result<Void> result = Result.success();
        log.info("API Response: /api/stored-value-cards/{} - Stored value card deleted.", id);
        return result;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Result<StoredValueCard> getById(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();
        
        log.info("API Call: /api/stored-value-cards/{} - Getting stored value card by ID: {} by user: {}", id, id, currentUserId);
        
        StoredValueCard card = storedValueCardService.getById(id);
        if (card == null) {
            log.warn("API Response: /api/stored-value-cards/{} - Card not found", id);
            return Result.error("储值卡不存在");
        }
        
        // 验证储值卡是否属于当前用户或管理员
        if (!isAdmin && !card.getUserId().equals(currentUserId)) {
            log.warn("API Response: /api/stored-value-cards/{} - Access denied for user: {}, card belongs to user: {}", 
                    id, currentUserId, card.getUserId());
            return Result.error("无权查看此储值卡");
        }
        
        Result<StoredValueCard> result = Result.success(card);
        log.info("API Response: /api/stored-value-cards/{} - Stored value card fetched.", id);
        return result;
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Result<StoredValueCard> getByUserId(@PathVariable Long userId) {
        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();
        
        // 如果不是管理员，只能查看自己的储值卡
        if (!isAdmin && !currentUserId.equals(userId)) {
            log.warn("API Response: /api/stored-value-cards/user/{} - Access denied for user: {}, trying to access user: {}", 
                    userId, currentUserId, userId);
            return Result.error("无权查看此用户的储值卡");
        }
        
        log.info("API Call: /api/stored-value-cards/user/{} - Getting stored value card by user ID: {} by user: {}", userId, userId, currentUserId);
        Result<StoredValueCard> result = Result.success(storedValueCardService.getByUserId(userId));
        log.info("API Response: /api/stored-value-cards/user/{} - Stored value card fetched for user ID: {}", userId, userId);
        return result;
    }

    @PostMapping("/{id}/recharge")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> recharge(@PathVariable Long id, @RequestParam BigDecimal amount) {
        log.info("API Call: /api/stored-value-cards/{}/recharge - Recharging card ID: {} with amount: {}", id, id, amount);
        storedValueCardService.recharge(id, amount);
        Result<Void> result = Result.success();
        log.info("API Response: /api/stored-value-cards/{}/recharge - Card recharged.", id);
        return result;
    }

    @PostMapping("/{id}/deduct")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Result<Void> deduct(@PathVariable Long id, @RequestParam BigDecimal amount) {
        Long currentUserId = getCurrentUserId();
        boolean isAdmin = isAdmin();
        
        // 验证储值卡是否属于当前用户或管理员
        StoredValueCard card = storedValueCardService.getById(id);
        if (card == null) {
            log.warn("API Response: /api/stored-value-cards/{}/deduct - Card not found", id);
            return Result.error("储值卡不存在");
        }
        
        if (!isAdmin && !card.getUserId().equals(currentUserId)) {
            log.warn("API Response: /api/stored-value-cards/{}/deduct - Access denied for user: {}, card belongs to user: {}", 
                    id, currentUserId, card.getUserId());
            return Result.error("无权操作此储值卡");
        }
        
        log.info("API Call: /api/stored-value-cards/{}/deduct - Deducting from card ID: {} with amount: {} by user: {}", id, id, amount, currentUserId);
        storedValueCardService.deduct(id, amount);
        Result<Void> result = Result.success();
        log.info("API Response: /api/stored-value-cards/{}/deduct - Amount deducted from card.", id);
        return result;
    }
} 