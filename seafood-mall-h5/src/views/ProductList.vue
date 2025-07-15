<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <div class="product-list-container">
    <!-- 搜索栏 -->
    <van-search
      v-model="searchKeyword"
      placeholder="请输入搜索关键词"
      show-action
      @search="onSearch"
      @cancel="onClearSearch"
    />

    <div class="main-content">
      <!-- 左侧分类导航 -->
      <div class="left-sidebar">
        <van-sidebar v-model="activeCategoryIndex">
          <van-sidebar-item
            v-for="category in categories"
            :key="category.id"
            :title="category.name"
            @click="selectCategory(category.id, category.name)"
          />
        </van-sidebar>
      </div>

      <!-- 右侧商品列表 -->
      <div class="right-product-display">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="loadProducts"
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
        <van-empty v-if="!products.length && !loading" description="暂无商品" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getAllCategories } from '@/api/category';
import { getProducts } from '@/api/product';
import { useCartStore } from '@/stores/cart';
import { Toast } from 'vant';
import CommonNavBar from '@/components/CommonNavBar.vue';

// 在页面内定义 ProductItem 接口类型
interface ProductItem {
  id: number;
  name: string;
  image: string;
  price: number;
  description: string;
  categoryId?: number;
  [key: string]: any;
}

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

const route = useRoute();
const router = useRouter();

const cartStore = useCartStore();

// 初始化所有响应式变量，使用空数组作为默认值
const categories = ref<CategoryItem[]>([]);
const products = ref<ProductItem[]>([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const selectedCategoryId = ref<number | null>(null);
const activeCategoryIndex = ref(-1); // 默认不选中任何项
const categoryName = ref<string | undefined>(undefined); // 当前选中的分类名称
const searchKeyword = ref<string | undefined>(undefined); // 搜索关键词
const finished = ref(false);

// 重置并加载商品
const resetAndLoadProducts = async () => {
  console.log('resetAndLoadProducts called');
  currentPage.value = 1;
  await loadProducts();
};

// 加载商品列表
const loadProducts = async () => {
  if (loading.value || finished.value) return;
  loading.value = true;
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      status: 1,
      categoryId: selectedCategoryId.value
    };
    const response = await getProducts(params);
    if (response && response.data && Array.isArray(response.data.records)) {
      if (currentPage.value === 1) {
        products.value = response.data.records;
      } else {
        products.value = [...products.value, ...response.data.records];
      }
      total.value = response.data.total;
      if (response.data.records.length === 0 || products.value.length >= response.data.total) {
        finished.value = true;
      } else {
        currentPage.value++;
      }
    } else {
      finished.value = true;
    }
  } catch (error) {
    console.error('Failed to load products:', error);
    finished.value = true;
  } finally {
    loading.value = false;
  }
};

// 加载分类列表
const loadCategories = async () => {
  console.log('loadCategories started');
  try {
    const response = await getAllCategories();
    console.log('Raw getAllCategories API response:', response);
    if (response && response.data && Array.isArray(response.data)) {
      categories.value = response.data;
    } else {
      categories.value = [];
      console.warn('getAllCategories did not return expected data structure.', response);
    }
  } catch (error) {
    console.error('Failed to load categories:', error);
    categories.value = [];
  }
};

// 监听路由参数变化，只在categoryId变化时重置并加载商品，避免死循环
watch(
  () => route.query.categoryId,
  (newCategoryId, oldCategoryId) => {
    // categories 未加载时不处理
    if (!categories.value || categories.value.length === 0) return;
    if (newCategoryId !== oldCategoryId) {
      selectedCategoryId.value = newCategoryId ? Number(newCategoryId) : null;
      if (newCategoryId) {
        const idx = categories.value.findIndex(c => c.id === Number(newCategoryId));
        activeCategoryIndex.value = idx;
      } else {
        activeCategoryIndex.value = -1;
      }
      categoryName.value = route.query.categoryName as string | undefined;
      searchKeyword.value = route.query.keyword as string | undefined;
      currentPage.value = 1;
      products.value = [];
      finished.value = false;
      loadProducts();
    }
  },
  { immediate: true }
);

// 处理分类选择
const selectCategory = (id: number, name: string) => {
  const idx = categories.value.findIndex(c => c.id === id);
  if (selectedCategoryId.value === id) return;
  selectedCategoryId.value = id;
  activeCategoryIndex.value = idx;
  categoryName.value = name;
  searchKeyword.value = undefined;
  currentPage.value = 1;
  products.value = [];
  finished.value = false;
  // 直接加载商品，不依赖路由变化
  loadProducts();
  // 可选：同步路由参数（不再依赖watch加载商品）
  router.replace({
    query: { ...route.query, categoryId: id, categoryName: name }
  });
};

// 处理页码变化
const handlePageChange = (page: number) => {
  console.log('Page changed to:', page);
  currentPage.value = page;
  loadProducts();
};

// 返回上一页


// 搜索商品
const onSearch = () => {
  resetAndLoadProducts();
};

// 清除搜索
const onClearSearch = () => {
  searchKeyword.value = undefined;
  resetAndLoadProducts();
};

onMounted(() => {
  window.scrollTo(0, 0);
  activeCategoryIndex.value = -1;
  loadCategories();
  // 页面初次加载时也要加载商品
  currentPage.value = 1;
  products.value = [];
  finished.value = false;
  loadProducts();
});

const goToProductDetail = (id?: number) => {
  if (id) {
    router.push(`/product/${id}`);
  }
};

const addToCart = (product: ProductItem) => {
  cartStore.addProduct(
    product,
    1,
    product.skuId || product.unit || undefined // 优先传skuId，其次unit
  );
  Toast.success('已加入购物车');
};
</script>

<style scoped>
.product-list-container {
  padding: 20px;
  min-height: calc(100vh - 50px - 40px - 50px); /* 预留tabbar、版权栏、底部操作栏空间 */
  box-sizing: border-box;
  overflow-x: hidden; /* 防止横向滚动条 */
  display: flex;
  flex-direction: column;
}

.main-content {
  display: flex;
  flex-direction: row;
  width: 100%;
  flex: 1;
  min-width: 0;
  box-sizing: border-box;
}

.left-sidebar {
  width: 90px;
  min-width: 70px;
  max-width: 120px;
  margin-right: 10px;
  box-sizing: border-box;
}

.right-product-display {
  flex: 1;
  min-width: 0;
  box-sizing: border-box;
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
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 8px 10px;
  position: relative;
  min-height: 80px;
  margin-bottom: 10px;
  width: 100%;
  box-sizing: border-box;
}

.card-left {
  width: 60px;
  height: 60px;
  border-radius: 8px;
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
  border-radius: 8px;
}

.card-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
  margin-left: 10px;
}

.card-title {
  font-size: 15px;
  font-weight: 500;
  color: #222;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-sold {
  font-size: 12px;
  color: #aaa;
  margin-bottom: 4px;
}

.card-price {
  font-size: 16px;
  color: #1e90ff;
  font-weight: bold;
  margin-top: auto;
}

.card-cart-btn {
  position: absolute;
  right: 10px;
  bottom: 10px;
  cursor: pointer;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.pagination button:hover:not(:disabled) {
  background-color: #0056b3;
}

.pagination span {
  font-size: 14px;
  color: #666;
}
</style>