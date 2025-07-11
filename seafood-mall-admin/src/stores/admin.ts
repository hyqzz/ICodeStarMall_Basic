/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import { defineStore } from 'pinia'
import { adminApi, AdminLoginResponse } from '@/api/admin'

interface AdminState {
  token: string | null
  adminId: number | null
  username: string | null
  realName: string | null
}

export const useAdminStore = defineStore('admin', {
  state: (): AdminState => ({
    token: localStorage.getItem('adminToken') || null,
    adminId: JSON.parse(localStorage.getItem('adminInfo') || '{}').id || null,
    username: JSON.parse(localStorage.getItem('adminInfo') || '{}').username || null,
    realName: JSON.parse(localStorage.getItem('adminInfo') || '{}').realName || null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    async login(username: string, password: string) {
      try {
        const response = await adminApi.login(username, password)
        const data: AdminLoginResponse = response.data

        this.token = data.token
        this.adminId = data.id
        this.username = data.username
        this.realName = data.realName

        localStorage.setItem('adminToken', data.token)
        localStorage.setItem('adminInfo', JSON.stringify({ id: data.id, username: data.username, realName: data.realName }))

        return true
      } catch (error) {
        console.error('Admin login failed:', error)
        this.logout() // Ensure state is cleared on login failure
        throw error
      }
    },

    logout() {
      this.token = null
      this.adminId = null
      this.username = null
      this.realName = null
      localStorage.removeItem('adminToken')
      localStorage.removeItem('adminInfo')
    }
  }
}) 