/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import request from '../utils/request';

// 定义 UserAddress 接口，与后端实体对应
export interface UserAddress {
  id?: number;
  userId?: number;
  consigneeName: string;
  consigneePhone: string;
  province: string;
  city: string;
  district: string;
  detailAddress: string;
  isDefault: boolean;
  createdTime?: string;
  updatedTime?: string;
  deleted?: boolean;
}

/**
 * 获取当前用户的所有地址
 * @returns Promise<Result<UserAddress[]>>
 */
export function getUserAddresses() {
  return request<UserAddress[]>({
    url: '/api/user/addresses',
    method: 'GET',
  });
}

/**
 * 获取指定地址详情
 * @param id 地址ID
 * @returns Promise<Result<UserAddress>>
 */
export function getUserAddressDetail(id: number) {
  return request<UserAddress>({
    url: `/api/user/addresses/${id}`,
    method: 'GET'
  });
}

/**
 * 添加新地址
 * @param data 地址信息
 * @returns Promise<Result<UserAddress>>
 */
export function createUserAddress(data: UserAddress) {
  return request<UserAddress>({
    url: '/api/user/addresses',
    method: 'POST',
    data,
  });
}

/**
 * 更新地址
 * @param id 地址ID
 * @param data 地址信息
 * @returns Promise<Result<UserAddress>>
 */
export function updateUserAddress(id: number, data: UserAddress) {
  return request<UserAddress>({
    url: `/api/user/addresses/${id}`,
    method: 'PUT',
    data,
  });
}

/**
 * 删除地址
 * @param id 地址ID
 * @returns Promise<Result<void>>
 */
export function deleteUserAddress(id: number) {
  return request<void>({
    url: `/api/user/addresses/${id}`,
    method: 'DELETE',
  });
}

/**
 * 设置默认地址
 * @param id 地址ID
 * @returns Promise<Result<void>>
 */
export function setDefaultUserAddress(id: number) {
  return request<void>({
    url: `/api/user/addresses/set-default/${id}`,
    method: 'PUT',
  });
} 