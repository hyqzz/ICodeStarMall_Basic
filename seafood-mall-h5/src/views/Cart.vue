<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="cart-container">
    <!-- 移除 CommonNavBar -->
    <!-- 其余内容保留 -->
    <template v-if="cartStore.items.length > 0">
      <div class="cart-list">
        <van-swipe-cell v-for="item in cartStore.items" :key="item.productId + (item.skuId || '')">
          <van-checkbox
            v-model="item.selected"
            class="cart-checkbox"
            @change="cartStore.toggleProductSelection(item.productId, item.skuId)"
          />
          <van-card
            :price="item.price.toFixed(2)"
            :title="''"
            :desc="(item.skuId ? `规格: ${item.skuId} ` : '') + (item.unit ? `规格: ${item.unit}` : '')"
            :thumb="item.image"
          >
            <template #title>
              <div class="card-title">{{ item.name }}</div>
            </template>
            <template #num>
              <van-stepper
                v-model="item.quantity"
                min="1"
                @change="(value) => cartStore.updateProductQuantity(item.productId, value, item.skuId)"
              />
            </template>
          </van-card>
          <template #right>
            <van-button
              square
              text="删除"
              type="danger"
              class="delete-button"
              @click="cartStore.removeProduct(item.productId, item.skuId)"
            />
          </template>
        </van-swipe-cell>
      </div>

      <van-submit-bar
        :price="cartStore.totalPrice * 100"
        button-text="提交订单"
        @submit="onSubmitOrder"
      >
        <van-checkbox
          v-model="allSelected"
          @click="cartStore.toggleAllSelection(!allSelected)"
          class="select-all-checkbox"
        >
          全选
        </van-checkbox>
        <van-button
          plain
          type="danger"
          size="small"
          round
          class="clear-cart-button"
          @click="cartStore.clearCart()"
        >
          清空购物车
        </van-button>
      </van-submit-bar>
    </template>
    <template v-else>
      <van-empty description="购物车是空的" />
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { Toast } from 'vant'; // Use Toast directly
import { useCartStore } from '../stores/cart';
import CommonNavBar from '@/components/CommonNavBar.vue';

const router = useRouter();
const cartStore = useCartStore();



// 计算属性：是否全选
const allSelected = computed(() => {
  return cartStore.items.length > 0 && cartStore.items.every(item => item.selected);
});

const onSubmitOrder = () => {
  if (cartStore.selectedItems.length === 0) {
    Toast.fail('请选择至少一件商品');
    return;
  }
  Toast.success('即将跳转到订单确认页面');
  // 导航到订单确认页面，可能需要传递选中的商品信息
  router.push({
    name: 'Order',
    // query: { selectedProductIds: cartStore.selectedItems.map(item => item.productId).join(',') }
  });
};
</script>

<style scoped>
.cart-container {
  padding-bottom: 50px; /* 为底部提交栏留出空间 */
  min-height: calc(100vh - 50px - 40px - 50px); /* 预留tabbar、版权栏、提交栏空间 */
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.common-nav-bar {
  position: sticky;
  top: 0;
  z-index: 100;
}

.cart-list {
  /* 移除 flex: 1; 和 overflow-y: auto; 让内容自适应 */
  padding-top: 5px;
  padding-bottom: 10px; /* 留出空间给提交栏 */
}

.van-swipe-cell {
  margin-bottom: 8px;
  border-radius: 8px;
  overflow: hidden;
}

.van-card {
  background-color: #fff;
  position: relative;
  padding-left: 50px; /* 为左侧选择框留出空间 */
}

.cart-checkbox {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
}

.delete-button {
  height: 100%;
}

.van-submit-bar {
  position: fixed;
  bottom: 50px; /* 底部导航栏上方 */
  width: 100%;
}

.select-all-checkbox {
  margin-left: 15px;
}

.clear-cart-button {
  margin-left: 10px;
}

.card-title {
  font-size: 18px;
  font-weight: 500;
  color: #222;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-spec {
  font-size: 14px;
  color: #888;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style> 