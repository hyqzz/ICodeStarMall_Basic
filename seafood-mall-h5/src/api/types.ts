/**
 * ICodeStar 智码星科技
 * https://www.icodestar.net
 * 
 * Copyright (c) 2025 ICodeStar. All rights reserved.
 * Licensed under the MIT License.
 */

export interface Result<T = any> {
  code: number; // 业务状态码，例如 200 表示成功
  message: string; // 提示信息
  data: T; // 业务数据
}

export interface PageResult<T> extends Result<PageData<T>> {}

export interface PageData<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  orders: any[];
  optimizeCountSql: boolean;
  searchCount: boolean;
  pages: number;
}

export interface PageQuery {
  current: number;
  size: number;
  [key: string]: any; // 允许其他查询参数
} 