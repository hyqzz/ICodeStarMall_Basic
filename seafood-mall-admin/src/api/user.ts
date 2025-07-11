/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import request from '../utils/request'

export interface User {
  id?: number
  username: string
  password?: string
  real_name?: string
  phone?: string
  email?: string
  userType?: number // 0-普通用户，1-月结用户，2-储值卡用户
  status?: number // 0-禁用，1-启用
  level?: number // 0-普通用户，1-银牌用户，2-金牌用户
}

interface UserQuery {
  current?: number;
  size?: number;
  username?: string;
  status?: number;
}

interface PageResponse<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

interface Result<T> {
  code: number;
  message: string;
  data: T;
}

export const userApi = {
  login(username: string, password: string): Promise<Result<any>> {
    return request({
      url: '/api/admin/users/login',
      method: 'post',
      data: { username, password }
    });
  },

  register(user: User): Promise<Result<User>> {
    return request({
      url: '/api/admin/users/register',
      method: 'post',
      data: user
    });
  },

  getUsersPage(params: UserQuery): Promise<Result<PageResponse<User>>> {
    return request({
      url: '/api/admin/users/page',
      method: 'get',
      params
    });
  },

  deleteUser(id: number): Promise<Result<void>> {
    return request({
      url: `/api/admin/users/${id}`,
      method: 'delete'
    });
  },

  getUserInfo(id: number): Promise<Result<User>> {
    return request({
      url: `/api/admin/users/${id}`,
      method: 'get'
    });
  },

  // TODO: Add other user APIs (e.g., getUserById, updateUser, checkUsername) as needed
}

// 导出userApi中的各个函数
export const { login, register, getUsersPage, deleteUser } = userApi; 