/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

interface CartItem {
  productId: number;
  name: string;
  image: string;
  price: number;
  quantity: number;
  skuId?: string; // 如果有SKU，可以添加SKU ID
  selected: boolean; // 是否选中，用于结算
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([]);

  // 添加商品到购物车
  const addProduct = (product: { id: number; name: string; image: string; price: number; }, quantity: number, skuId?: string) => {
    const existingItem = items.value.find(
      (item) => item.productId === product.id && (skuId ? item.skuId === skuId : !item.skuId)
    );

    if (existingItem) {
      existingItem.quantity += quantity;
    } else {
      items.value.push({
        productId: product.id,
        name: product.name,
        image: product.image,
        price: product.price,
        quantity,
        skuId,
        selected: true, // 默认选中
      });
    }
  };

  // 移除商品
  const removeProduct = (productId: number, skuId?: string) => {
    items.value = items.value.filter(
      (item) => !(item.productId === productId && (skuId ? item.skuId === skuId : !item.skuId))
    );
  };

  // 更新商品数量
  const updateProductQuantity = (productId: number, newQuantity: number, skuId?: string) => {
    const item = items.value.find(
      (item) => item.productId === productId && (skuId ? item.skuId === skuId : !item.skuId)
    );
    if (item) {
      item.quantity = newQuantity > 0 ? newQuantity : 1;
    }
  };

  // 切换商品选中状态
  const toggleProductSelection = (productId: number, skuId?: string) => {
    const item = items.value.find(
      (item) => item.productId === productId && (skuId ? item.skuId === skuId : !item.skuId)
    );
    if (item) {
      item.selected = !item.selected;
    }
  };

  // 全选/取消全选
  const toggleAllSelection = (selected: boolean) => {
    items.value.forEach(item => (item.selected = selected));
  };

  // 清空购物车
  const clearCart = () => {
    items.value = [];
  };

  // 计算购物车中商品的总数量 (所有商品)
  const totalQuantity = computed(() =>
    items.value.reduce((sum, item) => sum + item.quantity, 0)
  );

  // 计算选中的商品总价格
  const totalPrice = computed(() =>
    items.value.reduce((sum, item) => (item.selected ? sum + item.price * item.quantity : sum), 0)
  );

  // 获取选中的商品列表
  const selectedItems = computed(() => items.value.filter(item => item.selected));

  return {
    items,
    addProduct,
    removeProduct,
    updateProductQuantity,
    toggleProductSelection,
    toggleAllSelection,
    clearCart,
    totalQuantity,
    totalPrice,
    selectedItems,
  };
}); 