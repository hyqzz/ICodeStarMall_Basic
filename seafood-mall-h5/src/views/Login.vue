<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="login-container">
    <h2 class="login-title">登录</h2>
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
          v-model="username"
          name="username"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请填写用户名' }]"
        />
        <van-field
          v-model="password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请填写密码' }]"
        />
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          登录
        </van-button>
        <van-button round block plain type="primary" style="margin-top: 10px" @click="goToRegister">
          注册
        </van-button>
      </div>
    </van-form>
    
    <!-- 演示账号信息 -->
    <div class="demo-accounts">
      <div class="demo-section">
        <div class="demo-title">该项目已MIT开源，欢迎学习使用：</div>
        <div class="demo-title">gitee地址：</div>
        <div class="demo-content">
          <a href="https://gitee.com/heyqzz/icodestarmall_basic" target="_blank" class="demo-link">https://gitee.com/heyqzz/icodestarmall_basic</a>
        </div>
        <div class="demo-title">github地址：</div>
        <div class="demo-content">
          <a href="https://github.com/hyqzz/ICodeStarMall_Basic" target="_blank" class="demo-link">https://github.com/hyqzz/ICodeStarMall_Basic</a>
        </div>
      </div>
      <br>
      <div class="demo-section">
        <div class="demo-title">前端H5商城演示账号密码：</div>
        <div class="demo-content">user / user123</div>
      </div>
      <br>
      <div class="demo-section">
        <div class="demo-title">后端管理演示地址：</div>
        <div class="demo-content">
          <a href="https://admin-mall.demo.icodestar.net" target="_blank" class="demo-link">https://admin-mall.demo.icodestar.net</a>
        </div>
      </div>
    </div>
    
    <!-- 版权信息 -->
    <div class="copyright">
      <div class="copyright-text">
        <span>ICodeStar 智码星科技</span>
        <a href="https://www.icodestar.net" target="_blank" class="copyright-link">https://www.icodestar.net</a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { login } from '../api/user';
import { Toast } from 'vant';
import { useUserStore } from '../stores/user';

const router = useRouter();
const username = ref('');
const password = ref('');
const userStore = useUserStore();

const onSubmit = async () => {
  try {
    const res = await login({
      username: username.value,
      password: password.value,
    });
    
    if (res.code === 200 && res.data) {
      const { token, username: userResUsername, realName, level } = res.data;
      userStore.login(token, userResUsername, realName || '', level || 0);
      Toast.success('登录成功');
      router.push('/');
    } else {
      Toast.fail(res.message || '登录失败');
    }
  } catch (error: any) {
    Toast.fail(error.message || '登录失败');
  }
};

const goToRegister = () => {
  router.push('/register');
};
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  overflow: hidden;
  position: relative;
  background: url('@/assets/login.png') no-repeat center center;
  background-size: cover;
  padding-top: 80px;
}

.login-title {
  color: #222;
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 32px;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.logo-container {
  width: 100%;
  margin-bottom: 20px;
}

.logo-image {
  width: 100%;
  height: auto;
  object-fit: cover;
}

.demo-accounts {
  margin-top: 20px;
  padding: 16px;
  background-color: #f7f8fa;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
}

.demo-section {
  margin-bottom: 12px;
}

.demo-section:last-child {
  margin-bottom: 0;
}

.demo-title {
  font-size: 14px;
  color: #323233;
  font-weight: 500;
  margin-bottom: 4px;
}

.demo-content {
  font-size: 13px;
  color: #646566;
  word-break: break-all;
}

.demo-link {
  color: #1989fa;
  text-decoration: none;
}

.demo-link:hover {
  text-decoration: underline;
}

.copyright {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  text-align: center;
}

.copyright-text {
  font-size: 12px;
  color: #969799;
  line-height: 1.4;
}

.copyright-link {
  color: #1989fa;
  text-decoration: none;
  margin-left: 4px;
}

.copyright-link:hover {
  text-decoration: underline;
}

/* 输入框区域完全透明 */
.van-cell-group,
.van-cell,
.van-field,
.van-field__control,
.van-cell__value,
.van-cell__title {
  background: transparent !important;
  box-shadow: none !important;
}

/* 注册下方演示账号信息区域完全透明 */
.demo-accounts {
  background: transparent !important;
  box-shadow: none !important;
}
</style> 