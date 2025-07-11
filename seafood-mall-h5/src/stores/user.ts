/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useUserStore = defineStore('user', () => {
  const isLoggedIn = ref(localStorage.getItem('isLoggedIn') === 'true');
  const username = ref(localStorage.getItem('username') || '');
  const realName = ref(localStorage.getItem('realName') || '');
  const userLevel = ref(Number(localStorage.getItem('userLevel')) || 0);
  const token = ref(localStorage.getItem('token') || '');

  function login(userToken: string, userUsername: string, userRealName: string, level: number) {
    token.value = userToken;
    username.value = userUsername;
    realName.value = userRealName;
    userLevel.value = level;
    isLoggedIn.value = true;
    localStorage.setItem('token', userToken);
    localStorage.setItem('username', userUsername);
    localStorage.setItem('realName', userRealName);
    localStorage.setItem('userLevel', String(level));
    localStorage.setItem('isLoggedIn', 'true');
  }

  function logout() {
    token.value = '';
    username.value = '';
    realName.value = '';
    userLevel.value = 0;
    isLoggedIn.value = false;
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('realName');
    localStorage.removeItem('userLevel');
    localStorage.removeItem('isLoggedIn');
  }

  return {
    isLoggedIn,
    username,
    realName,
    userLevel,
    token,
    login,
    logout
  };
}); 