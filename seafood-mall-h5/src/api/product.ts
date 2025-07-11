/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import request from '../utils/request';

interface ProductQuery {
  current?: number;
  size?: number;
  categoryId?: number;
  name?: string;
  status?: number;
}

export function getProducts(params: ProductQuery) {
  return request({
    url: '/api/products/page',
    method: 'get',
    params
  });
}

export function getProductDetail(id: number) {
  return request({
    url: `/api/products/detail/${id}`,
    method: 'get'
  });
}