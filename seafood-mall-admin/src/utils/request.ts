/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAdminStore } from '../stores/admin'
import router from '../router'

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,  // 设置后端服务器地址
  timeout: 5000 // request timeout
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const adminStore = useAdminStore()
    if (adminStore.token) {
      config.headers['Authorization'] = 'Bearer ' + adminStore.token
    }
    return config
  },
  error => {
    console.log(error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    console.log('Axios Response received:', response)
    const res = response.data

    // if the custom code is not 200, it is judged as an error.
    if (res.code !== 200) {
      ElMessage({
        message: res.message || 'Error',
        type: 'error',
        duration: 5 * 1000
      })

      // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
        // to re-login
        ElMessageBox.confirm('You have been logged out, you can cancel to stay on this page, or log in again',
          'Confirm logout',
          {
            confirmButtonText: 'Re-Login',
            cancelButtonText: 'Cancel',
            type: 'warning'
          }
        ).then(() => {
          const adminStore = useAdminStore()
          adminStore.logout()
          router.push('/login')
        })
      }
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.log('Axios Error in response interceptor: ', error)
    if (error.response && error.response.status === 401) {
      const adminStore = useAdminStore()
      adminStore.logout()
      router.push('/login')
      ElMessage.error('认证失败或会话过期，请重新登录')
    } else {
      ElMessage({
        message: error.message,
        type: 'error',
        duration: 5 * 1000
      })
    }
    return Promise.reject(error)
  }
)

export default service 