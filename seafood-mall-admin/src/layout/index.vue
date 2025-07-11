<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">
        <img src="../assets/logo.png" alt="Logo" />
        <span>智码星商城后台</span>
      </div>
      <el-menu
        :default-active="route.path"
        class="el-menu-vertical"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/">
          <el-icon><Monitor /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><List /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/banners">
          <el-icon><Picture /></el-icon>
          <span>轮播图管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header>
        <div class="header-left">
          <el-icon class="toggle-sidebar" @click="toggleSidebar">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              {{ userStore.userInfo.username }}
              <el-icon><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view></router-view>
      </el-main>
      
      <!-- 版权信息 -->
      <el-footer class="footer">
        <div class="copyright">
          <span>智码星科技</span>
          <a href="https://www.icodestar.net" target="_blank" class="copyright-link">https://www.icodestar.net</a>
        </div>
      </el-footer>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import {
  Monitor,
  Goods,
  List,
  User,
  Fold,
  Expand,
  CaretBottom,
  Picture
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  color: #fff;
}

.logo img {
  width: 32px;
  height: 32px;
  margin-right: 12px;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.el-menu-vertical {
  border-right: none;
}

.toggle-sidebar {
  font-size: 20px;
  cursor: pointer;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.footer {
  background-color: #fff;
  border-top: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  padding: 0;
}

.copyright {
  color: #666;
  font-size: 14px;
  text-align: center;
}

.copyright-link {
  color: #409EFF;
  text-decoration: none;
  margin-left: 8px;
}

.copyright-link:hover {
  text-decoration: underline;
}
</style> 