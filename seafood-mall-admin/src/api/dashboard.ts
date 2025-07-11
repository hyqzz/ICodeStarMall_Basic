/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

import request from '@/utils/request'

export function getDashboardData() {
  return request({
    url: '/api/dashboard',
    method: 'get'
  })
} 