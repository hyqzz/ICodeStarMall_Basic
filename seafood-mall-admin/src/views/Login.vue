<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>智码星商城后台管理系统</h2>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="登录" name="login">
      <el-form
        ref="loginFormRef"
            :model="loginFormState"
            :rules="loginRules"
        label-width="0"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
                v-model="loginFormState.username"
            placeholder="用户名"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
                v-model="loginFormState.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
        </el-tab-pane>
        <el-tab-pane label="注册" name="register">
          <el-form
            ref="registerFormRef"
            :model="registerFormState"
            :rules="registerRules"
            label-width="0"
            @keyup.enter="handleRegister"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerFormState.username"
                placeholder="用户名"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="registerFormState.password"
                type="password"
                placeholder="密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerFormState.confirmPassword"
                type="password"
                placeholder="确认密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item prop="realName">
              <el-input
                v-model="registerFormState.realName"
                placeholder="真实姓名"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input
                v-model="registerFormState.phone"
                placeholder="手机号"
                prefix-icon="Phone"
              />
            </el-form-item>
            <el-form-item prop="email">
              <el-input
                v-model="registerFormState.email"
                placeholder="邮箱"
                prefix-icon="Message"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="success"
                :loading="loading"
                class="login-button"
                @click="handleRegister"
              >
                注册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <!-- 演示账号信息 -->
    <div class="demo-accounts">
      <div>后端Admin演示账号密码：<br>superadmin / admin123</div>
      <br>
      <div>
        <div>前端H5演示地址：</div>
        <a href="https://h5-mall.demo.icodestar.net" target="_blank" class="copyright-link h5-demo-link">https://h5-mall.demo.icodestar.net</a>
      </div>
    </div>
    <!-- 版权信息 -->
    <div class="copyright">
      <span>ICodeStar 智码星科技</span><br>
      <a href="https://www.icodestar.net" target="_blank" class="copyright-link">https://www.icodestar.net</a>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '../stores/admin'
import { ElMessage } from 'element-plus'
import { User, Lock, Phone, Message } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { adminApi, AdminUser } from '@/api/admin'

const router = useRouter()
const adminStore = useAdminStore()
const loading = ref(false)

const activeTab = ref('login')

const loginFormRef = ref<FormInstance>()
const registerFormRef = ref<FormInstance>()

const loginFormState = reactive({
  username: '',
  password: ''
})

const registerFormState = reactive<AdminUser>({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  email: ''
})

const validatePass2 = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerFormState.password) {
    callback(new Error('两次输入的密码不一致!'))
  } else {
    callback()
  }
}

const loginRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
})

const registerRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
})

const handleLogin = async () => {
  const isValid = await loginFormRef.value?.validate()
  if (!isValid) {
    return
  }

  loading.value = true
  try {
    const success = await adminStore.login(loginFormState.username, loginFormState.password)
    if (success) {
      ElMessage.success('登录成功')
      router.push('/')
    }
  } catch (error) {
    console.error('Login error:', error)
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  const isValid = await registerFormRef.value?.validate()
  if (!isValid) {
    return
  }

  loading.value = true
  try {
    const adminUser: AdminUser = {
      username: registerFormState.username,
      password: registerFormState.password,
      realName: registerFormState.realName,
      phone: registerFormState.phone,
      email: registerFormState.email
    }
    await adminApi.register(adminUser)
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    registerFormState.username = ''
    registerFormState.password = ''
    registerFormState.confirmPassword = ''
    registerFormState.realName = ''
    registerFormState.phone = ''
    registerFormState.email = ''
  } catch (error) {
    console.error('Register error:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: url('@/assets/login.png') no-repeat center center;
  background-size: cover;
  position: relative;
  overflow: hidden;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 400px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(2px);
  border: none;
}

.card-header {
  text-align: center;
}

.logo {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
}

.login-button {
  width: 100%;
}

.login-tabs .el-tabs__header {
  margin-bottom: 20px;
}

.copyright {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  text-align: center;
  color: #666;
}

.copyright-text {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.copyright-link {
  color: #666;
  text-decoration: none;
  margin-left: 8px;
}

.copyright-link:hover {
  text-decoration: underline;
}

.demo-accounts {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  width: 400px;
  bottom: 140px;
  text-align: left;
  color: #222;
  font-size: 14px;
  line-height: 1.7;
  z-index: 2;
  pointer-events: none;
}
@media (max-width: 500px) {
  .demo-accounts {
    width: 90vw;
    min-width: 0;
    left: 50%;
    transform: translateX(-50%);
    font-size: 13px;
  }
}
.demo-accounts a,
.demo-accounts .copyright-link {
  color: #666;
  text-decoration: none;
  pointer-events: auto;
}
.demo-accounts a:hover,
.demo-accounts .copyright-link:hover {
  text-decoration: underline;
  color: #409eff;
}
.h5-demo-link {
  display: block;
  padding-left: 0;
  margin-left: 0;
  word-break: break-all;
}
</style> 