/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import request from '../utils/request'

export interface Order {
  id?: number
  orderNo: string
  userId: number
  totalAmount: number
  status: number
  createTime: string
  updateTime?: string
}

export interface OrderQuery {
  current: number
  size: number
  status?: number
  orderNo?: string
}

export const orderApi = {
  // 获取订单列表
  getOrders(params: OrderQuery) {
    return request({
      url: '/api/admin/orders',
      method: 'get',
      params
    })
  },

  // 获取订单详情
  getOrderDetail(id: number) {
    return request({
      url: `/api/admin/orders/${id}`,
      method: 'get'
    })
  },

  // 更新订单状态
  updateOrderStatus(id: number, status: number) {
    return request({
      url: `/api/admin/orders/${id}/status`,
      method: 'put',
      data: { status }
    })
  },

  // 取消订单
  cancelOrder(id: number) {
    return request({
      url: `/api/admin/orders/${id}/cancel`,
      method: 'put'
    })
  },

  deleteOrder(id: number) {
    return request({
      url: `/api/admin/orders/${id}`,
      method: 'delete'
    })
  },

  payOrder(id: number, paymentType: number) {
    return request({
      url: `/api/admin/orders/${id}/pay`,
      method: 'post',
      params: { paymentType }
    })
  },

  completeOrder(id: number) {
    return request({
      url: `/api/admin/orders/${id}/complete`,
      method: 'post'
    })
  }
} 