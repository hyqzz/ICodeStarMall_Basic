<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="home-container">
    <!-- 搜索栏 -->
    <van-search
      v-model="searchKeyword"
      placeholder="请输入搜索关键词"
      show-action
      @search="onSearch"
      @cancel="onCancelSearch"
    />

    <!-- 轮播图 -->
    <van-swipe :autoplay="3000" indicator-color="white" class="my-swipe">
      <van-swipe-item v-for="banner in banners" :key="banner.id">
        <img :src="banner.image" :alt="banner.title" class="swipe-img" @click="banner.link && $router.push(banner.link)" />
      </van-swipe-item>
    </van-swipe>

    <!-- 快捷分类入口 -->
    <div class="category-grid">
      <div v-for="category in categories" :key="category.id" class="category-item" @click="goToProductList('', category.id, category.name)">
        <van-icon name="shop-o" size="30" />
        <span>{{ category.name }}</span>
      </div>
      <div class="category-item" @click="goToProductList('', undefined, '全部分类')">
        <van-icon name="apps-o" size="30" />
        <span>全部分类</span>
      </div>
    </div>

    <!-- 推荐商品列表 -->
    <div class="recommended-products">
      <van-divider>推荐商品</van-divider>
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="loadMoreProducts"
      >
        <div class="product-card-list">
          <div
            v-for="product in products"
            :key="product.id"
            class="custom-product-card"
          >
            <div class="card-left" @click="goToProductDetail(product.id)">
              <img :src="product.image" :alt="product.name" class="card-img" />
            </div>
            <div class="card-main" @click="goToProductDetail(product.id)">
              <div class="card-title">{{ product.name }}</div>
              <div class="card-sold">已售{{ product.sold || 0 }}件</div>
              <div class="card-price">¥<span>{{ product.price.toFixed(2) }}</span></div>
            </div>
            <div class="card-cart-btn" @click.stop="addToCart(product)">
              <van-icon name="cart-o" size="28" color="#1e90ff" />
            </div>
          </div>
        </div>
      </van-list>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getAllCategories } from '@/api/category';
import { getProducts } from '@/api/product';
import { useCartStore } from '@/stores/cart';
import { Toast } from 'vant';
import request from '@/utils/request';

// 定义分类接口
interface CategoryItem {
  id: number;
  name: string;
  parentId: number | null;
  level: number;
  sort: number;
  status: number;
  createdTime: string;
  updatedTime: string;
}

// 定义Banner接口
interface Banner {
  id: number;
  title: string;
  image: string;
  link?: string;
}

const router = useRouter();
const searchKeyword = ref('');
const categories = ref<CategoryItem[]>([]);
const products = ref<any[]>([]);
const loading = ref(false);
const finished = ref(false);
let currentPage = 1;
const pageSize = 10;
const cartStore = useCartStore();
const banners = ref<Banner[]>([]);

// 搜索功能
const onSearch = (val: string) => {
  goToProductList(val);
};

const onCancelSearch = () => {
  searchKeyword.value = '';
  products.value = [];
  currentPage = 1;
  finished.value = false;
  loadMoreProducts();
};

// 跳转到商品列表页
const goToProductList = (keyword: string = '', categoryId?: number, categoryName?: string) => {
  router.push({
    name: 'ProductList',
    query: { keyword, categoryId, categoryName },
  });
};

// 跳转到商品详情页
const goToProductDetail = (productId: number) => {
  router.push({ name: 'ProductDetail', params: { id: productId } });
};

// 加载更多推荐商品
const loadMoreProducts = async () => {
  loading.value = true;
  try {
    const response = await getProducts({
      current: currentPage,
      size: pageSize,
      name: searchKeyword.value || undefined,
      status: 1,
    });
    if (response.data && response.data.records) {
      if (currentPage === 1) {
        products.value = response.data.records;
      } else {
        products.value = [...products.value, ...response.data.records];
      }
      if (products.value.length >= response.data.total) {
        finished.value = true;
      }
      currentPage++;
    } else {
      finished.value = true;
    }
  } catch (error) {
    console.error('加载商品失败:', error);
    finished.value = true;
  } finally {
    loading.value = false;
  }
};

const addToCart = (product: any) => {
  cartStore.addProduct(
    product,
    1,
    product.skuId || product.unit || undefined // 优先传skuId，其次unit
  );
  Toast.success('已加入购物车');
};

onMounted(async () => {
  try {
    // 获取轮播图
    const bannerRes = await request({ url: '/api/banners', method: 'get' });
    banners.value = bannerRes.data || [];
    // 获取分类
    const categoryRes = await getAllCategories();
    categories.value = categoryRes.data;
    // 初始化分页状态
    products.value = [];
    currentPage = 1;
    finished.value = false;
    // 不再主动调用 loadMoreProducts，交给 van-list 自动触发
  } catch (error) {
    console.error('Error fetching initial data for Home page:', error);
  }
});
</script>

<style scoped>
.home-container {
  padding-bottom: 50px; /* 为底部导航留出空间 */
}

.my-swipe {
  width: 100%;
  aspect-ratio: 16/7;
  background: #f5f5f5;
}
.my-swipe .van-swipe-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: transparent;
}
.swipe-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
  background: #fff;
}

.category-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  padding: 10px 0;
  background-color: #fff;
  margin-bottom: 10px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 20%;
  padding: 10px 0;
  font-size: 12px;
  color: #666;
}

.category-item span {
  margin-top: 5px;
}

.recommended-products {
  background-color: #fff;
  padding: 10px;
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
  font-size: 20px;
  color: #1e90ff;
  font-weight: bold;
  margin-top: auto;
}

.card-cart-btn {
  position: absolute;
  right: 18px;
  bottom: 18px;
  cursor: pointer;
}
</style>