<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="h5-app">
    <van-nav-bar
      title="智码星商城"
      fixed
      placeholder
      left-arrow
      @click-left="onClickLeft"
    />
    <router-view />
    <van-tabbar v-model="activeTab" fixed route>
      <van-tabbar-item icon="home-o" to="/">首页</van-tabbar-item>
      <van-tabbar-item icon="apps-o" to="/products">分类</van-tabbar-item>
      <van-tabbar-item icon="cart-o" to="/cart" :badge="cartStore.totalQuantity || ''">购物车</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/mine">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'

const activeTab = ref(0)
const router = useRouter()
const cartStore = useCartStore()

const onClickLeft = () => {
  router.back()
}
</script>

<style scoped>
.h5-app {
  padding-bottom: 50px; /* Leave space for tabbar */
}
</style> 