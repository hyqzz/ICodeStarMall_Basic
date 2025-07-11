/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import request from '../utils/request';

export interface CategoryItem {
  id?: number;
  name: string;
  parentId: number | null;
  level: number;
  sort: number;
  status: number;
  createdTime?: string;
  updatedTime?: string;
}

interface CategoryQuery {
  current?: number;
  size?: number;
  name?: string;
  status?: number;
  parentId?: number | null;
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

// 获取所有商品分类（不分页）
export function getAllCategories(): Promise<Result<CategoryItem[]>> {
  return request({
    url: '/api/categories/all',
    method: 'get'
  });
}

// 获取商品分类分页列表
export function getCategoryPage(params: CategoryQuery): Promise<Result<PageResponse<CategoryItem>>> {
  return request({
    url: '/api/admin/categories/page',
    method: 'get',
    params
  });
}

// 创建商品分类
export function createCategory(data: CategoryItem): Promise<Result<CategoryItem>> {
  return request({
    url: '/api/admin/categories',
    method: 'post',
    data
  });
}

// 更新商品分类
export function updateCategory(id: number, data: CategoryItem): Promise<Result<CategoryItem>> {
  return request({
    url: `/api/admin/categories/${id}`,
    method: 'put',
    data
  });
}

// 删除商品分类
export function deleteCategory(id: number): Promise<Result<void>> {
  return request({
    url: `/api/admin/categories/${id}`,
    method: 'delete'
  });
}

// 根据父分类ID获取子分类
export function getCategoriesByParentId(parentId: number): Promise<Result<CategoryItem[]>> {
  return request({
    url: `/api/categories/parent/${parentId}`,
    method: 'get'
  });
}

// TODO: Add other category APIs (e.g., createCategory, updateCategory, deleteCategory, getCategoryById, getCategoryPage) as needed 