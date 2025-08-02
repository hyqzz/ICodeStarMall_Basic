# Seafood Mall Backend API 接口文档

## 项目概述

**项目名称**: 海鲜商城后端系统  
**技术栈**: Spring Boot + MyBatis Plus + Spring Security + JWT  
**服务端口**: 9000  
**基础URL**: http://localhost:9000  
**数据库**: MySQL  

## 通用响应格式

所有接口都使用统一的响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

- `code`: 状态码，200表示成功，500表示失败
- `message`: 响应消息
- `data`: 响应数据，可能为null

## 认证说明

- 使用JWT Token进行身份认证
- Token有效期：24小时
- 需要认证的接口在请求头中添加：`Authorization: Bearer {token}`
- 角色权限：
  - `USER`: 普通用户
  - `ADMIN`: 管理员

## 接口分类

### 1. 用户认证模块

#### 1.1 用户注册
- **接口**: `POST /api/users/register`
- **权限**: 无需认证
- **描述**: 用户注册
- **请求体**:
```json
{
  "username": "string",
  "password": "string",
  "realName": "string",
  "phone": "string",
  "email": "string"
}
```
- **响应**: 返回用户信息

#### 1.2 用户登录
- **接口**: `POST /api/users/login`
- **权限**: 无需认证
- **描述**: 用户登录
- **请求体**:
```json
{
  "username": "string",
  "password": "string"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "string",
    "username": "string",
    "realName": "string"
  }
}
```

#### 1.3 检查用户名可用性
- **接口**: `GET /api/users/check-username`
- **权限**: 无需认证
- **参数**: `username` (string, required)
- **响应**: 返回布尔值，true表示可用

### 2. 用户管理模块

#### 2.1 获取用户信息
- **接口**: `GET /api/users/{id}`
- **权限**: USER/ADMIN
- **描述**: 获取指定用户信息，普通用户只能查看自己的信息
- **响应**: 返回用户详细信息

#### 2.2 用户分页查询
- **接口**: `GET /api/users/page`
- **权限**: ADMIN
- **参数**:
  - `current` (int, default=1): 当前页
  - `size` (int, default=10): 页大小
  - `username` (string, optional): 用户名筛选
  - `status` (int, optional): 状态筛选
- **响应**: 返回分页用户列表

#### 2.3 更新用户信息
- **接口**: `PUT /api/users/{id}`
- **权限**: USER/ADMIN
- **描述**: 更新用户信息，普通用户只能更新自己的信息
- **请求体**: 用户信息对象
- **响应**: 返回更新后的用户信息

#### 2.4 删除用户
- **接口**: `DELETE /api/users/{id}`
- **权限**: ADMIN
- **描述**: 删除指定用户

### 3. 管理员认证模块

#### 3.1 管理员注册
- **接口**: `POST /api/admin/register`
- **权限**: 无需认证
- **请求体**:
```json
{
  "username": "string",
  "password": "string",
  "realName": "string"
}
```

#### 3.2 管理员登录
- **接口**: `POST /api/admin/login`
- **权限**: 无需认证
- **请求体**:
```json
{
  "username": "string",
  "password": "string"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "string",
    "username": "string",
    "realName": "string"
  }
}
```

### 4. 商品管理模块

#### 4.1 创建商品
- **接口**: `POST /api/admin/products`
- **权限**: ADMIN
- **请求体**:
```json
{
  "categoryId": "long",
  "name": "string",
  "description": "string",
  "price": "decimal",
  "stock": "int",
  "unit": "string",
  "image": "string",
  "status": "int"
}
```

#### 4.2 获取商品详情（H5端）
- **接口**: `GET /api/products/detail/{id}`
- **权限**: 无需认证
- **响应**: 返回商品详细信息

#### 4.3 获取商品详情（管理端）
- **接口**: `GET /api/admin/products/{id}`
- **权限**: ADMIN
- **响应**: 返回商品详细信息

#### 4.4 商品分页查询（管理端）
- **接口**: `GET /api/admin/products/page`
- **权限**: ADMIN
- **参数**:
  - `current` (int, default=1): 当前页
  - `size` (int, default=10): 页大小
  - `categoryId` (long, optional): 分类ID
  - `name` (string, optional): 商品名称
  - `status` (int, optional): 状态

#### 4.5 商品分页查询（H5端）
- **接口**: `GET /api/products/page`
- **权限**: 无需认证
- **参数**: 同管理端分页查询

#### 4.6 更新商品
- **接口**: `PUT /api/admin/products/{id}`
- **权限**: ADMIN
- **请求体**: 商品信息对象

#### 4.7 删除商品
- **接口**: `DELETE /api/admin/products/{id}`
- **权限**: ADMIN

#### 4.8 更新商品库存
- **接口**: `PUT /api/admin/products/{id}/stock`
- **权限**: ADMIN
- **参数**: `quantity` (int, required)

#### 4.9 获取商品规格
- **接口**: `GET /api/products/{id}/specs`
- **权限**: 无需认证
- **响应**: 返回商品规格数组

### 5. 分类管理模块

#### 5.1 创建分类
- **接口**: `POST /api/admin/categories`
- **权限**: ADMIN
- **请求体**:
```json
{
  "name": "string",
  "parentId": "long",
  "sort": "int",
  "status": "int"
}
```

#### 5.2 更新分类
- **接口**: `PUT /api/admin/categories/{id}`
- **权限**: ADMIN
- **请求体**: 分类信息对象

#### 5.3 删除分类
- **接口**: `DELETE /api/admin/categories/{id}`
- **权限**: ADMIN

#### 5.4 获取分类详情
- **接口**: `GET /api/categories/{id}`
- **权限**: 无需认证

#### 5.5 获取所有分类
- **接口**: `GET /api/categories/all`
- **权限**: 无需认证

#### 5.6 分类分页查询
- **接口**: `GET /api/admin/categories/page`
- **权限**: ADMIN
- **参数**:
  - `current` (int, default=1): 当前页
  - `size` (int, default=10): 页大小
  - `name` (string, optional): 分类名称
  - `status` (int, optional): 状态
  - `parentId` (long, optional): 父分类ID

#### 5.7 根据父分类获取子分类
- **接口**: `GET /api/categories/parent/{parentId}`
- **权限**: 无需认证

### 6. 订单管理模块

#### 6.1 创建订单
- **接口**: `POST /api/orders`
- **权限**: USER
- **请求体**:
```json
{
  "addressId": "long",
  "totalAmount": "decimal",
  "paymentMethod": "string",
  "orderItems": [
    {
      "productId": "long",
      "quantity": "int",
      "price": "decimal"
    }
  ]
}
```

#### 6.2 获取订单详情
- **接口**: `GET /api/orders/{id}`
- **权限**: USER
- **描述**: 用户只能查看自己的订单

#### 6.3 订单分页查询
- **接口**: `GET /api/orders/page`
- **权限**: USER
- **参数**:
  - `current` (int, default=1): 当前页
  - `size` (int, default=10): 页大小
  - `status` (int, optional): 订单状态

#### 6.4 获取我的订单
- **接口**: `GET /api/orders/my-orders`
- **权限**: USER
- **参数**: 同订单分页查询

#### 6.5 支付订单
- **接口**: `POST /api/orders/{id}/pay`
- **权限**: USER
- **参数**: `paymentType` (int, required)

#### 6.6 取消订单
- **接口**: `POST /api/orders/{id}/cancel`
- **权限**: USER

#### 6.7 完成订单
- **接口**: `POST /api/orders/{id}/complete`
- **权限**: USER

### 7. 管理员订单管理模块

#### 7.1 管理员订单分页查询
- **接口**: `GET /api/admin/orders/page`
- **权限**: ADMIN
- **参数**:
  - `current` (int, default=1): 当前页
  - `size` (int, default=10): 页大小
  - `userId` (long, optional): 用户ID
  - `status` (int, optional): 订单状态
  - `orderNo` (string, optional): 订单号

#### 7.2 管理员获取订单详情
- **接口**: `GET /api/admin/orders/{id}`
- **权限**: ADMIN

#### 7.3 管理员更新订单
- **接口**: `PUT /api/admin/orders/{id}`
- **权限**: ADMIN
- **请求体**: 订单信息对象

#### 7.4 管理员更新订单状态
- **接口**: `PUT /api/admin/orders/{id}/status`
- **权限**: ADMIN
- **参数**: `status` (int, required)
- **状态值说明**:
  - 0: 待付款
  - 1: 待发货
  - 2: 已发货
  - 3: 已完成
  - 4: 已取消

#### 7.5 管理员删除订单
- **接口**: `DELETE /api/admin/orders/{id}`
- **权限**: ADMIN

#### 7.6 批量更新订单状态
- **接口**: `PUT /api/admin/orders/batch/status`
- **权限**: ADMIN
- **参数**:
  - `orderIds` (long[], required): 订单ID数组
  - `status` (int, required): 目标状态

#### 7.7 获取订单统计信息
- **接口**: `GET /api/admin/orders/statistics`
- **权限**: ADMIN
- **响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalOrders": "long",
    "pendingPayment": "long",
    "pendingShipment": "long",
    "shipped": "long",
    "completed": "long",
    "cancelled": "long"
  }
}
```

### 8. Banner管理模块

#### 8.1 获取Banner列表（H5端）
- **接口**: `GET /api/banners`
- **权限**: 无需认证
- **描述**: 获取所有启用的Banner，按排序字段排序

#### 8.2 Banner分页查询（管理端）
- **接口**: `GET /api/admin/banners/page`
- **权限**: ADMIN
- **参数**:
  - `current` (int, default=1): 当前页
  - `size` (int, default=10): 页大小
  - `title` (string, optional): 标题筛选
  - `status` (int, optional): 状态筛选

#### 8.3 创建Banner
- **接口**: `POST /api/admin/banners`
- **权限**: ADMIN
- **请求体**:
```json
{
  "title": "string",
  "image": "string",
  "link": "string",
  "sort": "int",
  "status": "int"
}
```

#### 8.4 更新Banner
- **接口**: `PUT /api/admin/banners/{id}`
- **权限**: ADMIN
- **请求体**: Banner信息对象

#### 8.5 删除Banner
- **接口**: `DELETE /api/admin/banners/{id}`
- **权限**: ADMIN

### 9. 文件上传模块

#### 9.1 文件上传
- **接口**: `POST /api/admin/files/upload`
- **权限**: ADMIN
- **请求**: multipart/form-data
- **参数**: `file` (MultipartFile, required)
- **响应**: 返回文件访问URL
- **配置**:
  - 最大文件大小: 10MB
  - 最大请求大小: 100MB
  - 上传路径: C:/Users/heyq/Projects/icodestarmall_basic/seafood-mall-backend/uploads
  - URL前缀: /uploads/

### 10. 用户地址管理模块

#### 10.1 获取我的地址列表
- **接口**: `GET /api/user/addresses`
- **权限**: USER

#### 10.2 获取默认地址
- **接口**: `GET /api/user/addresses/default`
- **权限**: USER

#### 10.3 获取指定地址
- **接口**: `GET /api/user/addresses/{id}`
- **权限**: USER

#### 10.4 添加地址
- **接口**: `POST /api/user/addresses`
- **权限**: USER
- **请求体**:
```json
{
  "consigneeName": "string",
  "consigneePhone": "string",
  "province": "string",
  "city": "string",
  "district": "string",
  "detail": "string",
  "isDefault": "boolean"
}
```

#### 10.5 更新地址
- **接口**: `PUT /api/user/addresses/{id}`
- **权限**: USER
- **请求体**: 地址信息对象

#### 10.6 删除地址
- **接口**: `DELETE /api/user/addresses/{id}`
- **权限**: USER

#### 10.7 设置默认地址
- **接口**: `PUT /api/user/addresses/set-default/{id}`
- **权限**: USER

### 11. 储值卡管理模块

#### 11.1 创建储值卡
- **接口**: `POST /api/stored-value-cards`
- **权限**: USER/ADMIN
- **描述**: 普通用户只能为自己创建，管理员可以为任何用户创建

#### 11.2 更新储值卡
- **接口**: `PUT /api/stored-value-cards/{id}`
- **权限**: USER/ADMIN
- **描述**: 普通用户只能更新自己的储值卡

#### 11.3 删除储值卡
- **接口**: `DELETE /api/stored-value-cards/{id}`
- **权限**: ADMIN

#### 11.4 获取储值卡详情
- **接口**: `GET /api/stored-value-cards/{id}`
- **权限**: USER/ADMIN
- **描述**: 普通用户只能查看自己的储值卡

#### 11.5 根据用户ID获取储值卡
- **接口**: `GET /api/stored-value-cards/user/{userId}`
- **权限**: USER/ADMIN
- **描述**: 普通用户只能查看自己的储值卡

#### 11.6 储值卡充值
- **接口**: `POST /api/stored-value-cards/{id}/recharge`
- **权限**: ADMIN
- **参数**: `amount` (decimal, required)

#### 11.7 储值卡扣费
- **接口**: `POST /api/stored-value-cards/{id}/deduct`
- **权限**: USER/ADMIN
- **参数**: `amount` (decimal, required)
- **描述**: 普通用户只能操作自己的储值卡

### 12. 仪表板模块

#### 12.1 获取仪表板数据
- **接口**: `GET /api/dashboard`
- **权限**: 无需认证
- **响应**: 返回仪表板统计数据

## 数据模型

### 用户实体 (User)
```json
{
  "id": "long",
  "username": "string",
  "password": "string",
  "realName": "string",
  "phone": "string",
  "email": "string",
  "userType": "int",
  "level": "int",
  "status": "int",
  "createdTime": "datetime",
  "updatedTime": "datetime",
  "deleted": "boolean"
}
```

### 商品实体 (Product)
```json
{
  "id": "long",
  "categoryId": "long",
  "name": "string",
  "description": "string",
  "price": "decimal",
  "stock": "int",
  "unit": "string",
  "image": "string",
  "status": "int",
  "createdTime": "datetime",
  "updatedTime": "datetime",
  "deleted": "int",
  "sold": "int"
}
```

### 订单实体 (Order)
```json
{
  "id": "long",
  "orderNo": "string",
  "userId": "long",
  "totalAmount": "decimal",
  "addressId": "long",
  "paymentType": "int",
  "paymentStatus": "int",
  "status": "int",
  "remark": "string",
  "createdTime": "datetime",
  "updatedTime": "datetime",
  "deleted": "boolean",
  "items": "OrderItem[]",
  "addressDetail": "string",
  "consigneeName": "string",
  "consigneePhone": "string",
  "username": "string"
}
```

## 错误码说明

- **200**: 操作成功
- **500**: 服务器内部错误
- **401**: 未授权访问
- **403**: 权限不足
- **404**: 资源不存在

## 安全配置

- JWT密钥配置在application.yml中
- Token过期时间：24小时
- 支持跨域访问
- 文件上传大小限制：单文件10MB，总请求100MB

## 日志配置

- 项目日志级别：DEBUG
- Spring Security日志级别：DEBUG
- 所有API调用都有详细的日志记录

## 数据库配置

- 数据库：MySQL
- 连接地址：localhost:3306/seafood_mall_db
- 字符编码：UTF-8
- 时区：Asia/Shanghai
- 逻辑删除支持

---

**文档版本**: 1.0  
**生成时间**: 2025-01-27  
**项目版权**: ICodeStar 智码星科技  
**许可证**: MIT License