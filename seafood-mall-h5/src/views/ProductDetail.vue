<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="product-detail-container">
    <!-- 顶栏 -->
    <CommonNavBar title="商品详情" />

    <div v-if="product" class="product-content">
      <!-- 商品图片轮播 -->
      <van-swipe :autoplay="3000" indicator-color="white" class="product-swipe">
        <van-swipe-item>
          <img :src="getImageUrl(product.image)" alt="Product Image" class="product-image" />
        </van-swipe-item>
      </van-swipe>

      <!-- 商品基本信息 -->
      <div class="product-info">
        <div class="price">¥{{ product.price.toFixed(2) }}</div>
        <div class="name">{{ product.name }}</div>
      </div>

      <!-- 规格选择 -->
      <van-cell-group>
        <van-cell title="规格" :value="specs[0] || '无'" is-link @click="onShowSkuPopup" />
        <van-cell title="配送" is-link :value="selectedAddressText" @click="onSelectAddress" />
      </van-cell-group>
      <!-- 用普通弹窗代替 van-sku -->
      <van-dialog v-model:show="showSkuPopup" show-cancel-button :showConfirmButton="false" class="custom-sku-dialog">
        <div class="sku-dialog-content">
          <div class="sku-header">
            <img :src="product?.image" class="sku-image" />
            <div class="sku-info">
              <div class="sku-price">销售价 <span class="price">¥{{ product?.price }} <!-- / {{ specs[0] }} --></span></div>
              <div class="sku-stock">库存{{ product?.stock }}件</div>
              <div class="sku-tip">请选择包装规格</div>
            </div>
          </div>
          <div class="sku-section">
            <div class="sku-label">包装规格</div>
            <div class="sku-specs">
              <van-button
                type="primary"
                size="small"
                class="sku-spec-btn"
                :class="{ selected: true }"
              >{{ specs[0] }}</van-button>
            </div>
          </div>
          <div class="sku-section">
            <div class="sku-label">购买数量</div>
            <van-stepper v-model="buyCount" min="1" :max="product?.stock || 1" />
          </div>
          <div class="sku-footer">
            <van-button type="danger" block @click="onAddCartClicked({ unit: specs[0], count: buyCount })">加入购物车</van-button>
            <van-button type="primary" block @click="onBuyClicked({ unit: specs[0], count: buyCount })">立即购买</van-button>
          </div>
        </div>
      </van-dialog>

      <!-- 商品详情和推荐商品 Tabs -->
      <van-tabs v-model:active="activeTab" sticky>
        <van-tab title="商品详情">
          <div class="tab-content" v-html="fixedDescription"></div>
        </van-tab>
      </van-tabs>

      <!-- 悬浮右下角按钮 -->
      <div class="floating-action-buttons">
        <van-button type="danger" class="floating-btn" @click="onShowSkuPopup">加入购物车</van-button>
        <van-button type="primary" class="floating-btn" @click="onShowSkuPopup">立即购买</van-button>
      </div>
    </div>
    <van-empty v-else description="商品不存在或正在加载..." />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getProductDetail, getProducts } from '@/api/product';
import { getUserAddresses } from '@/api/userAddress';
import { Toast } from 'vant'; // 只引入 Toast 组件
import request from '@/utils/request';
import { useCartStore } from '@/stores/cart';

// 页面内定义 ProductItem 类型，避免外部导入
interface ProductItem {
  id: number;
  name: string;
  image: string;
  price: number;
  description: string;
  categoryId?: number;
  [key: string]: any;
}

const route = useRoute();
const router = useRouter();
const productId = ref<number | null>(null);
const product = ref<ProductItem | null>(null);
const recommendedProducts = ref<ProductItem[]>([]);

const activeTab = ref(0);
const showSkuPopup = ref(false);
const cartTotal = ref(0); // 购物车商品总数，待实际集成购物车功能时更新

const specs = ref<string[]>([]);
const defaultAddress = ref<string>('');
const selectedAddress = ref<any>(null); // 用户选中的地址
const buyCount = ref(1);

const cartStore = useCartStore();

// 计算选中的地址显示文本
const selectedAddressText = computed(() => {
  if (selectedAddress.value) {
    return `${selectedAddress.value.province}${selectedAddress.value.city}${selectedAddress.value.district}${selectedAddress.value.detailAddress}（${selectedAddress.value.consigneeName} ${selectedAddress.value.consigneePhone}）`;
  }
  return defaultAddress.value;
});

// 先定义 checkSelectedAddress
const checkSelectedAddress = () => {
  const selectedAddressData = route.query.selectedAddress;
  if (selectedAddressData) {
    try {
      selectedAddress.value = JSON.parse(selectedAddressData as string);
      // 清除查询参数，避免重复处理
      router.replace({ 
        query: { 
          ...route.query, 
          selectedAddress: undefined 
        }
      });
    } catch (error) {
      console.error('解析选中地址数据失败:', error);
    }
  }
};

const fetchProductDetail = async (id: number) => {
  try {
    const res = await getProductDetail(id);
    product.value = res.data;

    // 只查上架商品
    const recRes = await getProducts({ current: 1, size: 4, categoryId: product.value?.categoryId, status: 1 });
    recommendedProducts.value = recRes.data.records;
  } catch (error) {
    console.error('Error fetching product detail:', error);
    product.value = null; // 加载失败时清空商品数据
    Toast.fail('加载商品详情失败');
  }
};

const fetchSpecs = async (productId: number) => {
  try {
    const res = await request({
      url: `/api/products/${productId}/specs`,
      method: 'get'
    });
    specs.value = res.data || [];
  } catch (error) {
    specs.value = [];
  }
};

const fetchDefaultAddress = async () => {
  const res = await getUserAddresses();
  const list = res.data || [];
  const def = list.find((item: any) => item.isDefault);
  if (def) {
    defaultAddress.value = `${def.province}${def.city}${def.district}${def.detailAddress}（${def.consigneeName} ${def.consigneePhone}）`;
  } else if (list.length > 0) {
    const first = list[0];
    defaultAddress.value = `${first.province}${first.city}${first.district}${first.detailAddress}（${first.consigneeName} ${first.consigneePhone}）`;
  } else {
    defaultAddress.value = '暂无收货地址，请先添加';
  }
};

// 在 fetchProductDetail 之后自动查询规格
const fetchAllDetail = async (id: number) => {
  await fetchProductDetail(id);
  await fetchSpecs(id);
};

// 监听路由参数变化，自动查详情和规格
watch(() => route.params.id, (newId) => {
  if (newId) {
    productId.value = Number(newId);
    fetchAllDetail(productId.value);
  }
}, { immediate: true });

// 监听路由查询参数变化，检查是否有选中的地址返回
watch(() => route.query.selectedAddress, (newSelectedAddress) => {
  if (newSelectedAddress) {
    checkSelectedAddress();
  }
}, { immediate: true });



// 打开规格弹窗时获取规格
const onShowSkuPopup = async () => {
  await fetchSpecs(productId.value!);
  showSkuPopup.value = true;
};

onMounted(() => {
  window.scrollTo(0, 0);
  fetchDefaultAddress();
  checkSelectedAddress(); // 检查是否有选中的地址
});

// 加入购物车点击事件
const onAddCartClicked = (skuData: any) => {
  if (!product.value) return;
  cartStore.addProduct(
    {
      id: product.value.id,
      name: product.value.name,
      image: product.value.image,
      price: product.value.price
    },
    buyCount.value,
    specs.value[0] // 传递当前选中的规格
  );
  Toast.success('商品已加入购物车');
  showSkuPopup.value = false;
  cartTotal.value = cartStore.totalQuantity;
};

// 立即购买点击事件
const onBuyClicked = (skuData: any) => {
  if (!product.value) return;
  
  // 直接跳转到订单页面，传递商品信息用于直接购买
  const buyNowData = {
    productId: product.value.id,
    name: product.value.name,
    image: product.value.image,
    price: product.value.price,
    quantity: buyCount.value,
    skuId: specs.value[0] || undefined
  };
  
  showSkuPopup.value = false;
  
  // 构建查询参数
  const query: any = { 
    buyNow: 'true',
    productData: JSON.stringify(buyNowData)
  };
  
  // 如果有选中的地址，也传递过去
  if (selectedAddress.value) {
    query.selectedAddress = JSON.stringify(selectedAddress.value);
  }
  
  // 跳转到提交订单页面，传递直接购买的商品信息
  router.push({ 
    name: 'Order', 
    query
  });
};

// 跳转到购物车页面
const goToCart = () => {
  router.push({ name: 'Cart' });
};

// 跳转到地址选择页面
const onSelectAddress = () => {
  router.push({ 
    name: 'UserAddressList', 
    query: { 
      selectMode: 'true',
      returnTo: 'ProductDetail',
      productId: productId.value?.toString()
    }
  });
};

// 加入购物车（推荐tab用）
const addToCart = (product: any) => {
  cartStore.addProduct(product, 1);
  Toast.success('已加入购物车');
};

// 主图和富文本图片路径补全逻辑
const getImageUrl = (path: string) => {
  if (!path) return '';
  if (path.startsWith('http')) return path;
  // 自动适配当前页面域名，避免localhost
  const { protocol, host } = window.location;
  return `${protocol}//${host}${path}`;
};

const fixedDescription = computed(() => {
  if (!product.value?.description) return '';
  // 替换所有 src="/uploads/xxx.jpg" 为 src="getImageUrl('/uploads/xxx.jpg')"
  return product.value.description.replace(
    /<img\s+[^>]*src=['"]((?!http)[^'"]+)['"][^>]*>/gi,
    (match, src) => {
      if (/^https?:\/\//.test(src)) return match;
      return match.replace(src, getImageUrl(src));
    }
  );
});
</script>

<style scoped>
.product-detail-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.product-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 60px; /* 为底部操作栏留出空间 */
  min-height: calc(100vh - 50px - 40px - 50px); /* 预留tabbar、版权栏、底部操作栏空间 */
}

.product-swipe {
  height: 250px;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 10px;
  background-color: #fff;
  margin-bottom: 10px;
}

.product-info .price {
  color: #ee0a24;
  font-size: 22px;
  font-weight: bold;
}

.product-info .name {
  font-size: 18px;
  margin-top: 5px;
}

.van-cell-group {
  margin-bottom: 10px;
}

.tab-content {
  padding: 10px;
  background-color: #fff;
  min-height: 200px;
  word-break: break-all;
  overflow-x: hidden;
}

:deep(.tab-content img) {
  max-width: 100% !important;
  width: auto !important;
  height: auto !important;
  display: block;
  box-sizing: border-box;
}

.recommended-products {
  padding: 10px;
  background-color: #fff;
}

.product-card-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.custom-product-card {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 12px 16px;
  position: relative;
  min-height: 110px;
  margin-bottom: 16px;
  width: 100%;
  box-sizing: border-box;
}

.card-left {
  width: 100px;
  height: 100px;
  border-radius: 12px;
  overflow: hidden;
  background: #f7f7f7;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.card-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
}

.card-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
  margin-left: 16px;
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

.card-sold {
  font-size: 14px;
  color: #aaa;
  margin-bottom: 8px;
}

.card-price {
  font-size: 18px;
  color: #ee0a24;
  font-weight: bold;
}

.card-cart-btn {
  margin-left: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.spec-item {
  margin-bottom: 10px;
}

.custom-sku-dialog .sku-dialog-content {
  padding: 16px;
}
.sku-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
.sku-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 12px;
}
.sku-info {
  flex: 1;
  text-align: left;
}
.sku-price {
  font-size: 16px;
  color: #198cff;
  margin-bottom: 4px;
}
.sku-price .price {
  font-size: 20px;
  color: #ee0a24;
  font-weight: bold;
}
.sku-stock {
  font-size: 14px;
  color: #666;
}
.sku-tip {
  font-size: 12px;
  color: #999;
}
.sku-section {
  margin-bottom: 16px;
}
.sku-label {
  font-size: 14px;
  margin-bottom: 8px;
}
.sku-specs {
  display: flex;
  gap: 8px;
}
.sku-spec-btn.selected {
  background: #198cff;
  color: #fff;
  border: none;
}
.sku-footer {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}
.sku-footer .van-button {
  flex: 1;
}
.floating-action-buttons {
  position: fixed;
  right: 16px;
  bottom: 72px; /* 距离底部导航栏上方，tabbar一般高度56px，留16px间距 */
  z-index: 999;
  display: flex;
  flex-direction: row;
  gap: 12px;
  justify-content: flex-end;
  pointer-events: none;
}
.floating-btn {
  width: 120px;
  height: 40px;
  min-width: 80px;
  border-radius: 20px;
  font-size: 15px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
  pointer-events: auto;
  padding: 0 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style> 