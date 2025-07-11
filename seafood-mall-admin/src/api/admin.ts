/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import request from '../utils/request'

export interface AdminUser {
  id?: number
  username: string
  password?: string
  realName?: string
  phone?: string
  email?: string
  role?: number
  status?: number
}

export interface AdminLoginResponse {
  id: number
  username: string
  realName: string
  token: string
}

export const adminApi = {
  login(username: string, password: string) {
    return request.post<AdminLoginResponse>('/api/admin/login', {
      username,
      password
    })
  },

  register(adminUser: AdminUser) {
    return request.post<void>('/api/admin/register', adminUser)
  }
} 