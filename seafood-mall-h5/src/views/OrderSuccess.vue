<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="order-success-container">
    <van-nav-bar title="订单成功" fixed placeholder left-arrow @click-left="router.replace('/')" />

    <van-empty image="success" description="订单已成功提交！">
      <p v-if="orderId">您的订单号是：{{ orderId }}</p>
      <van-button round type="primary" class="bottom-button" @click="viewOrderDetail">
        查看订单详情
      </van-button>
      <van-button round plain type="default" class="bottom-button" @click="router.replace('/')">
        返回首页
      </van-button>
    </van-empty>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const orderId = ref<string | null>(null)

onMounted(() => {
  orderId.value = route.params.orderId ? String(route.params.orderId) : null
})

const viewOrderDetail = () => {
  if (orderId.value) {
    router.replace({ name: 'OrderDetail', params: { id: orderId.value } })
  } else {
    router.replace('/my-orders')
  }
}
</script>

<style scoped>
.order-success-container {
  padding-top: 46px; /* Adjust for fixed van-nav-bar */
  text-align: center;
}

.bottom-button {
  margin: 10px;
}
</style> 