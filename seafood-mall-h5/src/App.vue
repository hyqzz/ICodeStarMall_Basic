<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="h5-app" :class="{ 'no-tabbar': !showTabbar }">
    <div class="main-content">
      <CommonNavBar v-if="showHeader" :title="headerTitle" />
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
import CommonNavBar from '@/components/CommonNavBar.vue'

const activeTab = ref(0)
const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

// 判断是否显示底部导航栏
const showTabbar = computed(() => {
  // 登录和注册页面不显示底部导航栏
  return !['/login', '/register'].includes(route.path)
})

// 全局头部标题映射
const headerTitleMap: Record<string, string> = {
  '/': '首页',
  '/products': '商品列表',
  '/cart': '购物车',
  '/order': '订单确认',
  '/mine': '我的',
  '/my-orders': '我的订单',
  '/addresses': '收货地址',
  '/register': '注册',
  '/login': '登录',
}

const showHeader = computed(() => {
  // 首页也显示头部
  return true;
})

const headerTitle = computed(() => {
  // 动态路由如 /product/123、/order-detail/1
  if (route.path.startsWith('/product/')) return '商品详情';
  if (route.path.startsWith('/order-detail/')) return '订单详情';
  if (route.path.startsWith('/address-edit/')) return '编辑地址';
  if (route.path.startsWith('/order-success/')) return '下单成功';
  return headerTitleMap[route.path] || '';
})

</script>

<style scoped>
.h5-app {
  padding-bottom: 50px; /* Leave space for tabbar */
}

.h5-app.no-tabbar {
  padding-bottom: 0; /* 没有底部导航栏时不需要底部间距 */
}

.main-content {
  min-height: calc(100vh - 50px - 40px); /* 减去tabbar和版权栏的高度 */
  display: flex;
  flex-direction: column;
}

.main-content .no-tabbar {
  min-height: calc(100vh - 40px); /* 没有tabbar时也要减去版权栏 */
}

.copyright {
  background-color: #f7f8fa;
  border-top: 1px solid #ebedf0;
  padding: 12px 16px;
  margin-top: auto; /* 推到底部 */
  height: 40px;
  box-sizing: border-box;
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