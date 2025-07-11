<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="user-address-list-container">
    <!-- 顶栏 -->
    <van-nav-bar
      :title="isSelectMode ? '选择收货地址' : '我的收货地址'"
      left-arrow
      @click-left="onClickLeft"
    />

    <van-address-list
      v-model="chosenAddressId"
      :list="addressList"
      default-tag-text="默认"
      @add="onAdd"
      @edit="onEdit"
      @select="onSelectAddress"
    />

    <van-empty v-if="!addressList.length && !loading" description="暂无收货地址" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Toast, Dialog } from 'vant';
import { getUserAddresses, deleteUserAddress, setDefaultUserAddress } from '@/api/userAddress';

// 在页面内定义 UserAddress 接口类型
interface UserAddress {
  id?: number;
  name: string;
  tel: string;
  address: string;
  isDefault: boolean;
  rawData?: any; // 原始地址数据，用于选择模式时返回
}

const router = useRouter();
const route = useRoute();

const addressList = ref<UserAddress[]>([]);
const chosenAddressId = ref<number | undefined>(undefined); // 默认选中的地址ID
const loading = ref(true);

// 判断是否为选择模式
const isSelectMode = computed(() => route.query.selectMode === 'true');

const onClickLeft = () => {
  router.back();
};

const fetchAddresses = async () => {
  loading.value = true;
  try {
    const res = await getUserAddresses();
    if (res.code === 200 && res.data) {
      addressList.value = res.data.map(addr => ({
        id: addr.id,
        name: addr.consigneeName,
        tel: addr.consigneePhone,
        address: `${addr.province || ''}${addr.city || ''}${addr.district || ''}${addr.detailAddress || ''}`,
        isDefault: addr.isDefault || false,
        rawData: addr
      })) as UserAddress[]; // 强制类型转换以匹配 van-address-list 的类型
      const defaultAddr = addressList.value.find(addr => addr.isDefault);
      if (defaultAddr) {
        chosenAddressId.value = defaultAddr.id;
      }
    } else {
      Toast.fail(res.message || '获取地址列表失败');
    }
  } catch (error) {
    console.error('Error fetching user addresses:', error);
    Toast.fail('加载地址列表失败');
  } finally {
    loading.value = false;
  }
};

const onAdd = () => {
  router.push({ name: 'UserAddressEdit', params: { id: 'new' } });
};

const onEdit = (item: UserAddress) => {
  router.push({ name: 'UserAddressEdit', params: { id: item.id } });
};

const onSelectAddress = async (item: UserAddress) => {
  if (isSelectMode.value) {
    // 选择模式：直接返回选中的地址
    const selectedAddressData = (item as any).rawData;
    if (selectedAddressData) {
      const returnTo = route.query.returnTo as string;
      const productId = route.query.productId as string;
      const orderData = route.query.orderData as string;
      
      if (returnTo === 'Order' && orderData) {
        // 返回到订单确认页，传递选中的地址
        router.replace({
          name: 'Order',
          query: { 
            selectedAddress: JSON.stringify(selectedAddressData),
            buyNow: JSON.parse(orderData).buyNow,
            productData: JSON.parse(orderData).productData
          }
        });
      } else if (returnTo === 'ProductDetail' && productId) {
        // 返回到商品详情页，传递选中的地址
        router.replace({
          name: 'ProductDetail',
          params: { id: productId },
          query: { 
            selectedAddress: JSON.stringify(selectedAddressData)
          }
        });
      } else {
        // 默认返回上一页
        router.back();
      }
    }
  } else {
    // 普通模式：设置默认地址
    if (!item.isDefault) {
      Dialog.confirm({
        title: '设置默认地址',
        message: '确定将该地址设置为默认地址吗？',
      }).then(async () => {
        try {
          const res = await setDefaultUserAddress(item.id!);
          if (res.code === 200) {
            Toast.success('默认地址设置成功');
            fetchAddresses(); // 刷新列表以更新默认状态
          } else {
            Toast.fail(res.message || '设置默认地址失败');
          }
        } catch (error) {
          console.error('Error setting default address:', error);
          Toast.fail('设置默认地址失败');
        }
      }).catch(() => {
        // on cancel
        chosenAddressId.value = addressList.value.find(addr => addr.isDefault)?.id; // 取消选择，恢复原来的默认地址
      });
    }
  }
};

onMounted(() => {
  fetchAddresses();
});
</script>

<style scoped>
.user-address-list-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.van-nav-bar {
  position: sticky;
  top: 0;
  z-index: 100;
}

.van-address-list {
  flex: 1;
  overflow-y: auto;
  padding-top: 10px;
}

.van-address-item {
  margin-bottom: 10px;
}
</style> 