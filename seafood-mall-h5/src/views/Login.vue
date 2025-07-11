<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="login-container">
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
  padding-top: 10vh; /* 调整此值以控制内容离顶部的距离 */
}
</style> 