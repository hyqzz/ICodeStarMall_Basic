/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import axios from 'axios'
import type { InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { Toast, Dialog } from 'vant'
import router from '../router'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API, // 从环境变量获取后端服务器地址
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    // 后端约定的成功状态码是 200
    if (res.code !== 200) {
      Toast.fail(res.message || '请求失败')
      // 可以根据后端返回的特定错误码进行更多处理，例如：
      // if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
      //   Dialog.confirm({
      //     title: '警告',
      //     message: '您已被登出，可以取消停留在当前页面，或者重新登录',
      //   }).then(() => {
      //     localStorage.removeItem('token'); // 清除本地token
      //     router.push('/login');
      //   }).catch(() => {
      //     // on cancel
      //   });
      // }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    console.error('Response error:', error)
    if (error.response) {
      console.error('Response status:', error.response.status);
      console.error('Response data:', error.response.data);
    }
    if (error.response && error.response.status === 401) {
      // 认证失败或会话过期
      Dialog.confirm({
        title: '警告',
        message: '认证失败或会话过期，请重新登录',
      }).then(() => {
        localStorage.removeItem('token'); // 清除本地token
        router.push('/login');
      }).catch(() => {
        // on cancel
      });
    } else {
      Toast.fail(error.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export default service 