<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="h5-app" :class="{ 'no-tabbar': !showTabbar }">
    <van-nav-bar
      title="智码星商城"
      fixed
      placeholder
      left-arrow
      @click-left="onClickLeft"
    />
    
    <div class="main-content">
      <router-view />
      
      <!-- 版权信息 -->
      <div v-if="showTabbar" class="copyright">
        <div class="copyright-text">
          <span>ICodeStar 智码星科技</span>
          <a href="https://www.icodestar.net" target="_blank" class="copyright-link">https://www.icodestar.net</a>
        </div>
      </div>
    </div>
    
    <van-tabbar v-if="showTabbar" v-model="activeTab" fixed route>
      <van-tabbar-item icon="home-o" to="/">首页</van-tabbar-item>
      <van-tabbar-item icon="apps-o" to="/products">分类</van-tabbar-item>
      <van-tabbar-item icon="cart-o" to="/cart" :badge="cartStore.totalQuantity || ''">购物车</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/mine">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '@/stores/cart'

const activeTab = ref(0)
const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

// 判断是否显示底部导航栏
const showTabbar = computed(() => {
  // 登录和注册页面不显示底部导航栏
  return !['/login', '/register'].includes(route.path)
})

const onClickLeft = () => {
  router.back()
}
</script>

<style scoped>
.h5-app {
  padding-bottom: 50px; /* Leave space for tabbar */
}

.h5-app.no-tabbar {
  padding-bottom: 0; /* 没有底部导航栏时不需要底部间距 */
}

.main-content {
  min-height: calc(100vh - 46px - 50px); /* 减去导航栏和tabbar的高度 */
  display: flex;
  flex-direction: column;
}

.main-content .no-tabbar {
  min-height: calc(100vh - 46px); /* 没有tabbar时只减去导航栏高度 */
}

.copyright {
  background-color: #f7f8fa;
  border-top: 1px solid #ebedf0;
  padding: 12px 16px;
  margin-top: auto; /* 推到底部 */
}

.copyright-text {
  text-align: center;
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
</style> 