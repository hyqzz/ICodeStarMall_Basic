/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userApi, type User } from '../api/user'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<User>({
    username: '',
    real_name: ''
  })

  const login = async (username: string, password: string) => {
    try {
      const response = await userApi.login(username, password)
      const { data } = response.data
      if (data) {
        token.value = data.token
        userInfo.value = data
        localStorage.setItem('token', data.token)
        return true
      }
      return false
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '登录失败')
      return false
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {
      username: '',
      real_name: ''
    }
    localStorage.removeItem('token')
  }

  const getUserInfo = async () => {
    try {
      const response = await userApi.getUserInfo(userInfo.value.id!)
      const { data } = response
      if (data) {
        userInfo.value = data
      }
    } catch (error: any) {
      ElMessage.error(error.response?.data?.message || '获取用户信息失败')
    }
  }

  return {
    token,
    userInfo,
    login,
    logout,
    getUserInfo
  }
}) 