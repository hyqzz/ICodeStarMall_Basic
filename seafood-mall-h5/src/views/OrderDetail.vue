<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="order-detail-container">
    <!-- 顶栏 -->
    <!-- 移除 <van-nav-bar title="订单详情" /> -->

    <div v-if="order" class="order-content">
      <!-- 订单状态 -->
      <div class="order-status-panel">
        <div class="order-status-title">订单状态：{{ orderStatusText }}</div>
        <div class="status-description">{{ getStatusDescription(order.status!) }}</div>
      </div>

      <!-- 订单信息 -->
      <van-cell-group title="订单信息" class="order-info-group">
        <van-cell title="订单编号" :value="order.orderNo" />
        <van-cell title="下单时间" :value="formatDate(order.createdTime!)" />
        <van-cell title="支付方式" :value="paymentMethodText" />
        <van-cell title="订单金额" :value="`¥${order.totalAmount.toFixed(2)}`" />
        <van-cell title="收货地址">
          <template #default>
            <div v-if="addressDetail">
              {{ addressDetail.consigneeName }} {{ addressDetail.consigneePhone }}<br>
              {{ addressDetail.province }}{{ addressDetail.city }}{{ addressDetail.district }}{{ addressDetail.detailAddress }}
            </div>
            <div v-else>暂无收货地址信息</div>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 商品清单 -->
      <van-cell-group title="商品清单" class="product-list-group">
        <template v-if="order.items && order.items.length">
          <van-card
            v-for="item in order.items"
            :key="item.productId + (item.skuId || '')"
            :num="item.quantity"
            :price="item.price.toFixed(2)"
            :desc="(item.skuId ? `规格: ${item.skuId} ` : '') + (item.unit ? `规格: ${item.unit}` : '')"
            :title="''"
            :thumb="item.productImage"
          >
            <template #title>
              <div class="card-title">{{ item.productName }}</div>
            </template>
          </van-card>
        </template>
        <template v-else>
          <van-empty description="暂无商品信息" />
        </template>
      </van-cell-group>

      <!-- 底部操作按钮 -->
      <van-action-bar v-if="order.status === 0">
        <van-action-bar-button type="warning" text="取消订单" @click="cancelOrder" />
        <van-action-bar-button type="danger" text="立即支付" @click="payOrder" />
      </van-action-bar>
      <van-action-bar v-else-if="order.status === 1">
        <van-action-bar-button type="primary" text="提醒发货" @click="remindDelivery" />
      </van-action-bar>
      <van-action-bar v-else-if="order.status === 2">
        <van-action-bar-button type="success" text="确认收货" @click="confirmReceipt" />
      </van-action-bar>
    </div>
    <van-empty v-else description="订单不存在或正在加载..." />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getOrder } from '@/api/order';
import { Toast, Dialog } from 'vant';

// 在页面内定义 Order 接口类型
interface OrderItem {
  id?: number;
  orderId?: number;
  productId: number;
  productName: string;
  productImage?: string;
  price: number;
  quantity: number;
  totalAmount: number;
  createdTime?: string;
  updatedTime?: string;
  deleted?: boolean;
  unit?: string;
}

interface Order {
  id?: number;
  orderNo?: string;
  userId?: number;
  totalAmount: number;
  paymentType: number;
  paymentStatus?: number;
  status?: number;
  addressId?: number;
  remark?: string;
  createdTime?: string;
  updatedTime?: string;
  deleted?: boolean;
  items?: OrderItem[]; // 订单包含的商品列表
}

const route = useRoute();
const router = useRouter();
const orderId = ref<number | null>(null);
const order = ref<Order | null>(null);
const addressDetail = ref<any>(null);

// 获取订单详情
const fetchOrderDetail = async (id: number) => {
  try {
    const res = await getOrder(id);
    if (res.code === 200 && res.data) {
      order.value = res.data;
    } else {
      Toast.fail(res.message || '获取订单详情失败');
      order.value = null;
    }
  } catch (error) {
    console.error('Error fetching order detail:', error);
    Toast.fail('加载订单详情失败');
    order.value = null;
  }
};

// 监听路由参数变化，重新加载订单详情
watch(() => route.params.id, (newId) => {
  if (newId) {
    orderId.value = Number(newId);
    fetchOrderDetail(orderId.value);
  }
}, { immediate: true });

onMounted(() => {
  window.scrollTo(0, 0);
});


// 格式化日期时间
const formatDate = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString();
};

// 在页面内直接实现request方法
const request = async (options: any) => {
  const { url, method = 'GET', data, params } = options;
  let fetchUrl = url;
  if (params) {
    const query = Object.entries(params).map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`).join('&');
    fetchUrl += (fetchUrl.includes('?') ? '&' : '?') + query;
  }
  const res = await fetch(fetchUrl, {
    method,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': localStorage.getItem('token') ? `Bearer ${localStorage.getItem('token')}` : ''
    },
    body: method === 'GET' ? undefined : JSON.stringify(data)
  });
  return await res.json();
};

// 监听地址ID变化，获取地址详情
watch(() => order.value && order.value.addressId, async (newId) => {
  if (newId) {
    const res = await request({
      url: `/api/user/addresses/${newId}`,
      method: 'GET',
    });
    addressDetail.value = res.data || null;
  } else {
    addressDetail.value = null;
  }
}, { immediate: true });

// 订单状态文本
const orderStatusText = computed(() => {
  if (!order.value) return '';
  switch (order.value.status) {
    case 0: return '待支付';
    case 1: return '待发货';
    case 2: return '待收货';
    case 3: return '已完成';
    case 4: return '已取消';
    default: return '未知状态';
  }
});

// 支付方式文本
const paymentMethodText = computed(() => {
  if (!order.value) return '';
  switch (order.value.paymentType) {
    case 1: return '在线支付';
    case 3: return '货到付款';
    default: return '未知支付方式';
  }
});

// 获取订单状态描述
const getStatusDescription = (status: number) => {
  switch (status) {
    case 0: return '您的订单已提交，等待您的支付';
    case 1: return '您的订单已支付/货到付款，等待商家发货';
    case 2: return '商家已发货，请耐心等待收货';
    case 3: return '订单已完成，感谢您的购买';
    case 4: return '订单已取消';
    default: return '订单状态未知';
  }
};

// 模拟订单操作
const cancelOrder = () => {
  Dialog.confirm({
    title: '取消订单',
    message: '确定要取消该订单吗？',
  }).then(() => {
    Toast.success('订单已取消 (模拟)');
    // TODO: 调用后端API取消订单
  }).catch(() => {
    // on cancel
  });
};

const payOrder = () => {
  Toast.success('立即支付 (模拟)');
  // TODO: 调用后端API进行支付
};

const remindDelivery = () => {
  Toast.success('已提醒商家发货');
};

const confirmReceipt = () => {
  Dialog.confirm({
    title: '确认收货',
    message: '确定已收到商品吗？',
  }).then(() => {
    Toast.success('确认收货成功 (模拟)');
    // TODO: 调用后端API确认收货
  }).catch(() => {
    // on cancel
  });
};
</script>

<style scoped>
.order-detail-container {
  min-height: calc(100vh - 50px - 40px - 50px); /* 预留tabbar、版权栏、底部操作栏空间 */
  display: flex;
  flex-direction: column;
}

.order-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 50px; /* 为底部操作栏留出空间 */
}

.order-status-panel {
  margin-bottom: 10px;
}

.order-status-title {
  font-size: 16px;
  font-weight: bold;
  padding: 12px 16px 0 16px;
  color: #222;
}

.status-description {
  padding: 10px 16px;
  color: #666;
  font-size: 14px;
  background-color: #f7f8fa;
}

.order-info-group,
.product-list-group {
  margin-bottom: 10px;
}

.van-cell-group__title {
  padding-left: 16px;
}

.van-card {
  background-color: #fff;
  margin-top: 0;
  margin-bottom: 0;
}

.card-title {
  font-size: 18px;
  font-weight: 500;
  color: #222;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style> 