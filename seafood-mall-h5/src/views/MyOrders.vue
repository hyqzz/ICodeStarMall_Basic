<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="my-orders-container">
    <!-- 顶栏 -->
    <van-nav-bar
      title="我的订单"
      left-arrow
      @click-left="onClickLeft"
    />

    <!-- 订单状态标签页 -->
    <van-tabs v-model:active="activeStatus" @change="onStatusChange">
      <van-tab title="全部" :name="-1" />
      <van-tab title="待付款" :name="0" />
      <van-tab title="待发货" :name="1" />
      <van-tab title="待收货" :name="2" />
      <van-tab title="已完成" :name="3" />
    </van-tabs>

    <!-- 订单列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="loadOrders"
      >
        <van-cell-group v-for="order in orders" :key="order.id" class="order-card">
          <van-cell :title="`订单号：${order.orderNo}`" :value="orderStatusText(order.status)" />
          <van-card
            v-for="item in order.items"
            :key="item.productId + (item.skuId || '')"
            :num="item.quantity"
            :price="item.price.toFixed(2)"
            :desc="(item.skuId ? `规格: ${item.skuId} ` : '') + (item.unit ? `单位: ${item.unit}` : '')"
            :title="item.productName"
            :thumb="item.productImage"
            @click="goToOrderDetail(order.id)"
          />
          <van-cell :value="`实付款：¥${order.totalAmount.toFixed(2)}`" class="total-amount" />
          <div class="order-actions">
            <van-button
              v-if="order.status === 0"
              size="small"
              type="danger"
              round
              @click="cancelOrder(order.id!)"
            >取消订单</van-button>
            <van-button
              v-if="order.status === 0"
              size="small"
              type="primary"
              round
              @click="payOrder(order.id!)"
            >立即支付</van-button>
            <van-button
              v-if="order.status === 1"
              size="small"
              type="primary"
              round
              @click="remindDelivery(order.id!)"
            >提醒发货</van-button>
            <van-button
              v-if="order.status === 2"
              size="small"
              type="success"
              round
              @click="confirmReceipt(order.id!)"
            >确认收货</van-button>
            <van-button
              size="small"
              round
              @click="goToOrderDetail(order.id!)"
            >查看详情</van-button>
          </div>
        </van-cell-group>
        <van-empty v-if="!orders.length && !loading" description="暂无订单" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Toast, Dialog } from 'vant';
import request from '@/utils/request';

// 定义接口
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
  remark?: string;
  createdTime?: string;
  updatedTime?: string;
  deleted?: boolean;
  items?: OrderItem[];
}

interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
}

interface Result<T> {
  code: number;
  message: string;
  data: T;
}

interface OrderQuery {
  current: number;
  size: number;
  status?: number;
}

const router = useRouter();

const orders = ref<Order[]>([]);
const loading = ref(false);
const finished = ref(false);
const refreshing = ref(false);
const currentPage = ref(1);
const pageSize = 5; // 每页加载5个订单
const activeStatus = ref<number | undefined>(-1); // 当前选中的订单状态

const onClickLeft = () => {
  router.back();
};

const orderStatusText = (status: number) => {
  switch (status) {
    case 0: return '待支付';
    case 1: return '待发货';
    case 2: return '待收货';
    case 3: return '已完成';
    case 4: return '已取消';
    default: return '未知状态';
  }
};

const onStatusChange = () => {
  // 切换状态时重置列表和分页
  orders.value = [];
  currentPage.value = 1;
  finished.value = false;
  loadOrders();
};

const onRefresh = () => {
  // 重置列表和分页
  orders.value = [];
  currentPage.value = 1;
  finished.value = false;
  loadOrders();
  refreshing.value = false;
};

const loadOrders = async () => {
  loading.value = true;
  try {
    const params: any = {
      current: currentPage.value,
      size: pageSize,
    };
    if (activeStatus.value !== -1) {
      params.status = activeStatus.value;
    }
    const res = await request<Result<PageResult<Order>>>(
      {
        url: '/api/orders/my-orders',
        method: 'GET',
        params,
      }
    );

    if (res.code === 200 && res.data && res.data.records) {
      if (refreshing.value) {
        orders.value = res.data.records;
      } else {
        orders.value = [...orders.value, ...res.data.records];
      }
      if (orders.value.length >= res.data.total) {
        finished.value = true;
      }
      currentPage.value++;
    } else {
      finished.value = true;
    }
  } catch (error) {
    console.error('Error fetching orders:', error);
    Toast.fail('加载订单失败');
    finished.value = true;
  } finally {
    loading.value = false;
  }
};

const goToOrderDetail = (orderId: number) => {
  router.push({ name: 'OrderDetail', params: { id: orderId } });
};

const cancelOrder = async (orderId: number) => {
  try {
    await Dialog.confirm({
      title: '取消订单',
      message: '确定要取消该订单吗？',
    });
    await request({
      url: `/api/orders/${orderId}/cancel`,
      method: 'POST',
    });
    Toast.success('订单已取消');
    onRefresh();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Error canceling order:', error);
      Toast.fail('取消订单失败');
    }
  }
};

const payOrder = (orderId: number) => {
  router.push({ name: 'Payment', params: { orderId: orderId.toString() } });
};

const remindDelivery = () => {
  Toast.success('已提醒商家发货');
};

const confirmReceipt = async (orderId: number) => {
  try {
    await Dialog.confirm({
      title: '确认收货',
      message: '确定已收到商品吗？',
    });
    await request({
      url: `/api/orders/${orderId}/complete`,
      method: 'POST',
    });
    Toast.success('确认收货成功');
    onRefresh();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Error confirming receipt:', error);
      Toast.fail('确认收货失败');
    }
  }
};

onMounted(() => {
  loadOrders();
});
</script>

<style scoped>
.my-orders-container {
  min-height: 100vh;
  background-color: #f7f8fa;
}

.order-card {
  margin: 10px;
  border-radius: 8px;
  overflow: hidden;
}

.total-amount {
  text-align: right;
  color: #ee0a24;
  font-weight: bold;
}

.order-actions {
  padding: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  background-color: #fff;
}

.van-button {
  margin-left: 8px;
}
</style> 