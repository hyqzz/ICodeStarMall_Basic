/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import request from '../utils/request'
import type { PageResult, PageQuery } from './types'

export interface OrderItem {
  id?: number;
  orderId?: number;
  productId: number;
  productName: string;
  productImage?: string;
  price: number;
  quantity: number;
  totalAmount: number;
  createdTime?: string;
  updatedTime?: string;
  deleted?: boolean;
}

export interface Order {
  id?: number;
  orderNo?: string;
  userId?: number;
  totalAmount: number;
  paymentType: number;
  paymentStatus?: number;
  status?: number;
  remark?: string;
  createdTime?: string;
  updatedTime?: string;
  deleted?: boolean;
  items?: OrderItem[]; // 订单包含的商品列表
}

// 定义订单查询参数接口
export interface OrderQuery extends PageQuery {
  orderNo?: string;
  userId?: number;
  paymentStatus?: number;
  status?: number;
}

// 定义订单项接口
export interface CreateOrderItemRequest {
  productId: number;
  productName: string;
  price: number;
  quantity: number;
  totalAmount: number;
  skuId?: string; // 可选的SKU ID
}

// 定义创建订单请求接口
export interface CreateOrderRequest {
  addressId: number; // 收货地址ID
  totalAmount: number; // 订单总金额
  paymentMethod: string; // 支付方式 (e.g., "online", "cod")
  orderItems: CreateOrderItemRequest[]; // 订单项列表
}

// 定义创建订单响应接口
export interface CreateOrderResponse {
  id: number;
  orderNo: string;
  totalAmount: number;
  status: number;
  createdTime: string;
}

/**
 * 创建订单
 * @param data 订单创建请求数据
 * @returns Promise<Result<Order>>
 */
export function createOrder(data: CreateOrderRequest) {
  return request<Order>({
    url: '/api/orders',
    method: 'POST',
    data,
  });
}

/**
 * 获取订单详情
 * @param id 订单ID
 * @returns Promise<Result<Order>>
 */
export function getOrder(id: number) {
  return request<Order>({
    url: `/api/orders/${id}`,
    method: 'GET',
  });
}

/**
 * 获取我的订单列表
 * @param params 查询参数
 * @returns Promise<Result<PageResult<Order>>>
 */
export function getMyOrders(params: OrderQuery) {
  return request<PageResult<Order>>({
    url: '/api/orders/my-orders',
    method: 'GET',
    params,
  });
}

/**
 * 获取订单列表
 * @param params 查询参数
 * @returns Promise<Result<PageResult<Order>>>
 */
export function getOrders(params: OrderQuery) {
  return request<PageResult<Order>>({
    url: '/api/orders/page',
    method: 'GET',
    params,
  });
}

/**
 * 取消订单
 * @param id 订单ID
 * @returns Promise<Result<void>>
 */
export function cancelOrder(id: number) {
  return request<void>({
    url: `/api/orders/${id}/cancel`,
    method: 'POST',
  });
}

/**
 * 支付订单
 * @param id 订单ID
 * @param paymentType 支付方式
 * @returns Promise<Result<void>>
 */
export function payOrder(id: number, paymentType: number) {
  return request<void>({
    url: `/api/orders/${id}/pay`,
    method: 'POST',
    params: { paymentType },
  });
}

/**
 * 确认收货
 * @param id 订单ID
 * @returns Promise<Result<void>>
 */
export function confirmReceipt(id: number) {
  return request<void>({
    url: `/api/orders/${id}/complete`,
    method: 'POST',
  });
} 