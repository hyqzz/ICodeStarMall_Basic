/*
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

package com.seafoodmall.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seafoodmall.backend.entity.UserAddress;

import java.util.List;

public interface UserAddressService extends IService<UserAddress> {

    /**
     * 获取用户的所有地址
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    List<UserAddress> getAddressesByUserId(Long userId);

    /**
     * 获取用户的默认地址
     *
     * @param userId 用户ID
     * @return 默认地址
     */
    UserAddress getDefaultAddress(Long userId);

    /**
     * 添加新地址
     *
     * @param userAddress 地址信息
     * @param userId 用户ID
     * @return 添加成功的地址
     */
    UserAddress addAddress(UserAddress userAddress, Long userId);

    /**
     * 更新地址
     *
     * @param userAddress 地址信息
     * @param userId 用户ID
     * @return 更新成功的地址
     */
    UserAddress updateAddress(UserAddress userAddress, Long userId);

    /**
     * 删除地址
     *
     * @param addressId 地址ID
     * @param userId 用户ID
     */
    void deleteAddress(Long addressId, Long userId);

    /**
     * 设置默认地址
     *
     * @param addressId 地址ID
     * @param userId 用户ID
     */
    void setDefaultAddress(Long addressId, Long userId);

    /**
     * 获取当前用户的指定地址
     * @param id 地址ID
     * @param userId 用户ID
     * @return 地址详情
     */
    UserAddress getAddressById(Long id, Long userId);
} 