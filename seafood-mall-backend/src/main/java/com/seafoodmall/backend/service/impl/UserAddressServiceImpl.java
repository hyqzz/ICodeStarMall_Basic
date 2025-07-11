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
import com.seafoodmall.backend.entity.UserAddress;
import com.seafoodmall.backend.mapper.UserAddressMapper;
import com.seafoodmall.backend.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public List<UserAddress> getAddressesByUserId(Long userId) {
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId, userId);
        queryWrapper.orderByDesc(UserAddress::getIsDefault);
        queryWrapper.orderByDesc(UserAddress::getUpdatedTime);
        return userAddressMapper.selectList(queryWrapper);
    }

    @Override
    public UserAddress getDefaultAddress(Long userId) {
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId, userId);
        queryWrapper.eq(UserAddress::getIsDefault, true);
        return userAddressMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public UserAddress addAddress(UserAddress userAddress, Long userId) {
        // 设置用户ID
        userAddress.setUserId(userId);
        userAddress.setCreatedTime(LocalDateTime.now());
        userAddress.setUpdatedTime(LocalDateTime.now());
        userAddress.setDeleted(false);

        // 如果是新添加的地址，且设置为默认地址，则需要取消其他默认地址
        if (userAddress.getIsDefault() != null && userAddress.getIsDefault()) {
            clearDefaultAddress(userId);
        }

        userAddressMapper.insert(userAddress);
        return userAddress;
    }

    @Override
    @Transactional
    public UserAddress updateAddress(UserAddress userAddress, Long userId) {
        // 确保地址属于当前用户
        UserAddress existingAddress = userAddressMapper.selectById(userAddress.getId());
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new IllegalArgumentException("地址不存在或不属于当前用户");
        }

        userAddress.setUpdatedTime(LocalDateTime.now());

        // 如果设置为默认地址，则需要取消其他默认地址
        if (userAddress.getIsDefault() != null && userAddress.getIsDefault()) {
            clearDefaultAddress(userId);
        }

        userAddressMapper.updateById(userAddress);
        return userAddress;
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        // 确保地址属于当前用户
        UserAddress existingAddress = userAddressMapper.selectById(addressId);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new IllegalArgumentException("地址不存在或不属于当前用户");
        }
        userAddressMapper.deleteById(addressId);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long addressId, Long userId) {
        // 确保地址属于当前用户
        UserAddress userAddress = userAddressMapper.selectById(addressId);
        if (userAddress == null || !userAddress.getUserId().equals(userId)) {
            throw new IllegalArgumentException("地址不存在或不属于当前用户");
        }

        // 先取消该用户所有地址的默认状态
        clearDefaultAddress(userId);

        // 设置指定地址为默认
        userAddress.setIsDefault(true);
        userAddress.setUpdatedTime(LocalDateTime.now());
        userAddressMapper.updateById(userAddress);
    }

    private void clearDefaultAddress(Long userId) {
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId, userId);
        queryWrapper.eq(UserAddress::getIsDefault, true);
        List<UserAddress> defaultAddresses = userAddressMapper.selectList(queryWrapper);
        defaultAddresses.forEach(addr -> {
            addr.setIsDefault(false);
            userAddressMapper.updateById(addr);
        });
    }

    @Override
    public UserAddress getAddressById(Long id, Long userId) {
        UserAddress address = userAddressMapper.selectById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new IllegalArgumentException("地址不存在或不属于当前用户");
        }
        return address;
    }
} 