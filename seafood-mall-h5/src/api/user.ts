/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import request from '../utils/request';

// 用户登录
export function login(data: { username: string; password: string }) {
  return request({
    url: '/api/users/login',
    method: 'post',
    data,
  });
}

// 用户注册
export function register(data: {
  username: string;
  password: string;
  realName: string;
  phone: string;
  email: string;
}) {
  return request({
    url: '/api/users/register',
    method: 'post',
    data,
  });
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/api/users/info',
    method: 'get',
  });
}

// 更新用户信息
export function updateUserInfo(data: {
  realName?: string;
  phone?: string;
  email?: string;
  password?: string;
}) {
  return request({
    url: '/api/users/update',
    method: 'put',
    data,
  });
} 