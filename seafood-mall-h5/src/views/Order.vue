<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="order-container">
    <!-- 顶栏 -->
    <van-nav-bar
      title="订单确认"
      left-arrow
      @click-left="onClickLeft"
    />

    <div v-if="orderItems.length > 0" class="order-content">
      <!-- 地址信息 (占位，待集成用户地址管理) -->
      <van-cell-group class="address-card">
        <van-cell title="收货地址" is-link icon="location-o" @click="goToAddressList">
          <template #default>
            <div v-if="selectedAddress">
              <div>{{ selectedAddress.consigneeName }} {{ selectedAddress.consigneePhone }}</div>
              <div>{{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detailAddress }}</div>
            </div>
            <div v-else>请选择收货地址</div>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 商品列表 -->
      <van-card
        v-for="item in orderItems"
        :key="item.productId + (item.skuId || '')"
        :num="item.quantity"
        :price="item.price.toFixed(2)"
        :desc="item.skuId ? `规格: ${item.skuId}` : ''"
        :title="item.name"
        :thumb="item.image"
      />

      <!-- 订单信息概览 -->
      <van-cell-group class="order-summary">
        <van-cell title="商品总价">
          <template #default>
            <span class="price-text">¥{{ totalPrice.toFixed(2) }}</span>
          </template>
        </van-cell>
        <van-cell title="运费">
          <template #default>
            <span class="price-text">¥0.00</span>
          </template>
        </van-cell>
        <van-cell title="实付款">
          <template #default>
            <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 支付方式 (占位) -->
      <van-cell-group class="payment-method">
        <van-cell title="支付方式" is-link :value="paymentMethodText" @click="showPaymentPicker = true" />
      </van-cell-group>
    </div>
    <van-empty v-else description="没有商品信息" />

    <!-- 底部提交订单栏 -->
    <van-submit-bar
      v-if="orderItems.length > 0"
      :price="totalPrice * 100"
      button-text="提交订单"
      @submit="onSubmitOrder"
    />

    <!-- 支付方式选择弹窗 (模拟) -->
    <van-popup v-model:show="showPaymentPicker" position="bottom" round>
      <van-picker
        title="选择支付方式"
        :columns="paymentColumns"
        @confirm="onConfirmPayment"
        @cancel="showPaymentPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Toast, Dialog } from 'vant'; // 导入 Dialog
import { useCartStore } from '../stores/cart';
import { createOrder } from '@/api/order'; // 假设有订单创建API
import { getUserAddresses } from '@/api/userAddress';

// 在页面内定义 UserAddress 类型
interface UserAddress {
  id?: number;
  userId?: number;
  consigneeName: string;
  consigneePhone: string;
  province: string;
  city: string;
  district: string;
  detailAddress: string;
  isDefault: boolean;
  createdTime?: string;
  updatedTime?: string;
  deleted?: boolean;
}

// 在页面内定义 CreateOrderRequest 接口类型
interface CreateOrderRequest {
  addressId: number; // 收货地址ID
  totalAmount: number; // 订单总金额
  paymentMethod: string; // 支付方式 (e.g., "online", "cod")
  orderItems: {
    productId: number;
    quantity: number;
    price: number;
    skuId?: string; // 如果有SKU，传递SKU ID
  }[];
}

const router = useRouter();
const route = useRoute();
const cartStore = useCartStore();

const showPaymentPicker = ref(false);

// 订单商品列表
const orderItems = ref<any[]>([]);
const isBuyNow = ref(false);

// 加载真实收货地址
const selectedAddress = ref<UserAddress | null>(null);

// 模拟支付方式数据
const selectedPaymentMethod = ref('online'); // 默认在线支付
const paymentColumns = ref([
  { text: '在线支付', value: 'online' },
  { text: '货到付款', value: 'cod' },
]);

// 支付方式文本
const paymentMethodText = computed(() => {
  switch (selectedPaymentMethod.value) {
    case 'online':
      return '在线支付';
    case 'cod':
      return '货到付款';
    default:
      return '在线支付';
  }
});

// 计算总价格
const totalPrice = computed(() =>
  orderItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
);

// 初始化订单数据
const initOrderData = () => {
  const buyNow = route.query.buyNow === 'true';
  const productData = route.query.productData;
  const selectedAddressData = route.query.selectedAddress;
  
  // 处理选中的地址
  if (selectedAddressData) {
    try {
      const address = JSON.parse(selectedAddressData as string);
      selectedAddress.value = {
        id: address.id,
        consigneeName: address.consigneeName,
        consigneePhone: address.consigneePhone,
        province: address.province,
        city: address.city,
        district: address.district,
        detailAddress: address.detailAddress,
        isDefault: address.isDefault,
        createdTime: address.createdTime,
        updatedTime: address.updatedTime,
        deleted: address.deleted,
        userId: address.userId
      };
    } catch (error) {
      console.error('解析选中地址数据失败:', error);
    }
  }
  
  if (buyNow && productData) {
    // 直接购买模式
    isBuyNow.value = true;
    try {
      const product = JSON.parse(productData as string);
      orderItems.value = [product];
    } catch (error) {
      console.error('解析商品数据失败:', error);
      Toast.fail('商品数据错误');
      router.back();
    }
  } else {
    // 购物车模式
    isBuyNow.value = false;
    orderItems.value = cartStore.selectedItems.map(item => ({
      productId: item.productId,
      name: item.name,
      image: item.image,
      price: item.price,
      quantity: item.quantity,
      skuId: item.skuId,
      productImage: item.image,
      unit: item.unit
    }));
  }
};

const onClickLeft = () => {
  router.back();
};

const onConfirmPayment = (value: any) => {
  if (value && value.value) {
    selectedPaymentMethod.value = value.value;
  }
  showPaymentPicker.value = false;
};

// 跳转到地址管理页面
const goToAddressList = () => {
  router.push({ 
    name: 'UserAddressList', 
    query: { 
      selectMode: 'true',
      returnTo: 'Order',
      orderData: JSON.stringify({
        buyNow: isBuyNow.value,
        productData: route.query.productData,
        orderItems: orderItems.value
      })
    }
  });
};

const onSubmitOrder = async () => {
  if (!selectedAddress.value) {
    Toast.fail('请选择收货地址');
    return;
  }

  if (selectedPaymentMethod.value === 'online') {
    Toast('在线支付待开发...');
    return;
  }

  // 构建订单请求数据
  const orderRequestItems = orderItems.value.map(item => ({
    productId: item.productId,
    quantity: item.quantity,
    price: item.price,
    skuId: item.skuId, // 如果有SKU，传递SKU ID
  }));

  const orderData: CreateOrderRequest = {
    addressId: selectedAddress.value.id!,
    totalAmount: totalPrice.value,
    paymentMethod: selectedPaymentMethod.value,
    orderItems: orderRequestItems,
  };

  try {
    // 模拟后端订单创建API调用
    const res = await createOrder(orderData);

    if (res.code === 200) {
      Toast.success('订单提交成功！');
      // 如果是直接购买模式，不需要清空购物车
      if (!isBuyNow.value) {
        cartStore.clearCart(); // 只有购物车模式才清空购物车
      }
      // 货到付款直接跳转到订单详情页
      router.replace({ name: 'OrderDetail', params: { id: res.data.id } });
    } else {
      Dialog.alert({
        title: '订单提交失败',
        message: res.message || '请稍后再试',
      });
    }
  } catch (error) {
    console.error('Error submitting order:', error);
    Dialog.alert({
      title: '订单提交失败',
      message: '网络错误，请稍后再试',
    });
  }
};

// 加载真实收货地址
const loadAddresses = async () => {
  const res = await getUserAddresses();
  const list = res.data || [];
  // 默认选中默认地址（仅首次进入时设置）
  if (!selectedAddress.value) {
    const def = list.find((item: any) => item.isDefault);
    if (def) {
      selectedAddress.value = def;
    } else if (list.length > 0) {
      selectedAddress.value = list[0];
    } else {
      selectedAddress.value = null;
    }
  }
};

onMounted(async () => {
  await loadAddresses();
  initOrderData();
});
</script>

<style scoped>
.order-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.order-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 60px; /* 为底部提交栏留出空间 */
}

.address-card,
.order-summary,
.payment-method {
  margin-bottom: 10px;
  background-color: #fff;
}

.van-card {
  background-color: #fff;
  margin-top: 0;
  margin-bottom: 0;
}

.order-summary .price-text {
  color: #ee0a24;
}

.order-summary .total-price {
  color: #ee0a24;
  font-weight: bold;
  font-size: 16px;
}

.van-submit-bar {
  position: fixed;
  bottom: 0;
  width: 100%;
}
</style> 