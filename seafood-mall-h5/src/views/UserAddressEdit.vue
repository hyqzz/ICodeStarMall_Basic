<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="user-address-edit-container">
    <!-- 顶栏 -->
    <van-address-edit
      :area-list="areaList"
      :address-info="addressInfo"
      show-set-default
      :show-delete="isEdit"
      show-search-result
      :search-result="searchResult"
      :area-columns-placeholder="['请选择', '请选择', '请选择']"
      @save="onSave"
      @delete="onDelete"
      @change-detail="onChangeDetail"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Toast, Dialog } from 'vant';
import { getUserAddressDetail, createUserAddress, updateUserAddress, deleteUserAddress } from '@/api/userAddress';
import { areaList } from '@vant/area-data'; // 导入 Vant 的省市区数据

// 在页面内定义 UserAddress 接口类型
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

const route = useRoute();
const router = useRouter();

const isEdit = computed(() => route.params.id !== 'new');
const addressId = computed(() => (isEdit.value ? Number(route.params.id) : undefined));

const addressInfo = ref<Partial<UserAddress>>({});
const searchResult = ref([]);

// 初始加载地址详情 (如果是编辑模式)
watch(addressId, (newId) => {
  if (isEdit.value && newId) {
    fetchAddressDetail(newId);
  } else {
    addressInfo.value = {}; // 重置表单以便添加新地址
  }
}, { immediate: true });



const fetchAddressDetail = async (id: number) => {
  try {
    const res = await getUserAddressDetail(id);
    if (res.code === 200 && res.data) {
      const data = res.data;
      addressInfo.value = {
        id: data.id,
        name: data.consigneeName,
        tel: data.consigneePhone,
        province: data.province,
        city: data.city,
        county: data.district,
        addressDetail: data.detailAddress,
        areaCode: getAreaCode(data.province, data.city, data.district), // 需要一个函数来获取areaCode
        isDefault: data.isDefault,
      };
    } else {
      Toast.fail(res.message || '获取地址详情失败');
      router.replace({ name: 'UserAddressList' }); // 获取失败返回列表页
    }
  } catch (error) {
    console.error('Error fetching address detail:', error);
    Toast.fail('加载地址详情失败');
    router.replace({ name: 'UserAddressList' }); // 获取失败返回列表页
  }
};

// 辅助函数：根据省市区名称获取 areaCode
const getAreaCode = (provinceName?: string, cityName?: string, districtName?: string) => {
  if (!provinceName || !cityName || !districtName) return '';

  const province = Object.values(areaList.province_list).find(p => p === provinceName);
  const provinceCode = Object.keys(areaList.province_list).find(key => areaList.province_list[key] === province);

  const city = Object.values(areaList.city_list).find(c => c === cityName);
  const cityCode = Object.keys(areaList.city_list).find(key => areaList.city_list[key] === city);

  const county = Object.values(areaList.county_list).find(d => d === districtName);
  const countyCode = Object.keys(areaList.county_list).find(key => areaList.county_list[key] === county);

  if (provinceCode && cityCode && countyCode) {
    return countyCode; // Vant 地址编辑要求最小层级的areaCode
  }
  return '';
};

const onSave = async (content: any) => {
  const addressData: UserAddress = {
    id: isEdit.value ? addressId.value : undefined,
    consigneeName: content.name,
    consigneePhone: content.tel,
    province: content.province,
    city: content.city,
    district: content.county,
    detailAddress: content.addressDetail,
    isDefault: content.isDefault,
  };

  try {
    if (isEdit.value) {
      const res = await updateUserAddress(addressData.id!, addressData);
      if (res.code === 200) {
        Toast.success('地址更新成功');
        router.back();
      } else {
        Toast.fail(res.message || '地址更新失败');
      }
    } else {
      const res = await createUserAddress(addressData);
      if (res.code === 200) {
        Toast.success('地址添加成功');
        router.back();
      } else {
        Toast.fail(res.message || '地址添加失败');
      }
    }
  } catch (error) {
    console.error('Error saving address:', error);
    Toast.fail('保存地址失败');
  }
};

const onDelete = async () => {
  if (!isEdit.value || !addressId.value) return;
  Dialog.confirm({
    title: '删除地址',
    message: '确定删除该收货地址吗？',
  }).then(async () => {
    try {
      const res = await deleteUserAddress(addressId.value!);
      if (res.code === 200) {
        Toast.success('地址删除成功');
        router.back();
      } else {
        Toast.fail(res.message || '地址删除失败');
      }
    } catch (error) {
      console.error('Error deleting address:', error);
      Toast.fail('删除地址失败');
    }
  }).catch(() => {
    // on cancel
  });
};

const onChangeDetail = (val: string) => {
  if (val) {
    // 模拟搜索结果，实际项目中可能需要调用高德/百度地图API进行地址联想
    searchResult.value = [
      { name: '模拟地址结果1', address: '上海市XX区XX路1号' },
      { name: '模拟地址结果2', address: '上海市XX区XX路2号' },
    ];
  } else {
    searchResult.value = [];
  }
};

onMounted(() => {
  window.scrollTo(0, 0);
  if (isEdit.value) {
    fetchAddressDetail(addressId.value!);
  }
});
</script>

<style scoped>
.user-address-edit-container {
  min-height: calc(100vh - 50px - 40px - 50px); /* 预留tabbar、版权栏、底部操作栏空间 */
  display: flex;
  flex-direction: column;
}

.common-nav-bar {
  position: sticky;
  top: 0;
  z-index: 100;
}
</style> 