/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import request from '../utils/request'

export interface CategoryItem {
  id: number;
  name: string;
  parentId: number | null;
  level: number;
  sort: number;
  status: number;
  createdTime: string;
  updatedTime: string;
}

export function getAllCategories() {
  console.log('Calling getAllCategories API');
  return request({
    url: '/api/categories/all',
    method: 'get'
  })
}

export function getCategoriesByParentId(parentId: number) {
  console.log('Calling getCategoriesByParentId API for parentId:', parentId);
  return request({
    url: `/api/categories/parent/${parentId}`,
    method: 'get'
  })
} 