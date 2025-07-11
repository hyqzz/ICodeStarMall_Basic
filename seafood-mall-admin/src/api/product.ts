/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import request from '../utils/request';

interface ProductItem {
  id?: number;
  categoryId?: number;
  name?: string;
  description?: string;
  price?: number;
  stock?: number;
  unit?: string;
  image?: string;
  status?: number;
  createdTime?: string;
  updatedTime?: string;
  deleted?: boolean;
}

interface ProductQuery {
  current?: number;
  size?: number;
  categoryId?: number;
  name?: string;
  status?: number;
}

interface PageResponse<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

interface Result<T> {
  code: number;
  message: string;
  data: T;
}

// 获取商品分页列表
export function getProductsPage(params: ProductQuery): Promise<Result<PageResponse<ProductItem>>> {
  return request({
    url: '/api/admin/products/page',
    method: 'get',
    params
  });
}

// 删除商品
export function deleteProduct(id: number): Promise<Result<void>> {
  return request({
    url: `/api/admin/products/${id}`,
    method: 'delete'
  });
}

// 创建商品
export function createProduct(data: ProductItem): Promise<Result<ProductItem>> {
  return request({
    url: '/api/admin/products',
    method: 'post',
    data
  });
}

// 更新商品
export function updateProduct(id: number, data: ProductItem): Promise<Result<ProductItem>> {
  return request({
    url: `/api/admin/products/${id}`,
    method: 'put',
    data
  });
}

// 根据ID获取商品详情
export function getProductById(id: number): Promise<Result<ProductItem>> {
  return request({
    url: `/api/admin/products/${id}`,
    method: 'get'
  });
}

// TODO: Add other product APIs (e.g., updateProduct, getProductById) as needed 