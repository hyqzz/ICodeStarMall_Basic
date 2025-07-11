# 商城系统 (Seafood Mall System)

## 项目简介

本项目由 ICodeStar 智码星科技 制作，MIT真开源，可以任意使用但请保留版权信息

https://www.icodestar.net


该商城系统是一个基于 Spring Boot + Vue.js 的现代化电商平台，专为产品销售而设计。系统采用前后端分离架构，包含管理后台、H5移动端和RESTful API后端服务，提供完整的电商解决方案。

这个项目非常适合作为学习全栈开发的示例，或者作为小型电商项目的起点。代码结构清晰，文档完善，部署方案多样，是一个很好的全栈项目示例。

## 技术栈

### 后端技术栈
- **框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus 3.5.3.1
- **安全**: Spring Security + JWT
- **工具库**: Hutool 5.8.20, Lombok 1.18.38
- **Java版本**: JDK 17

### 前端技术栈
- **管理后台**: Vue 3 + TypeScript + Element Plus + Vite
- **H5移动端**: Vue 3 + TypeScript + Vant + Vite
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP客户端**: Axios

## 功能特性

### 🏪 商城核心功能
- **商品管理**: 商品分类、商品信息、库存管理、多图展示
- **用户系统**: 用户注册、登录、个人信息管理、地址管理
- **购物车**: 商品添加、数量修改、价格计算
- **订单系统**: 订单创建、状态跟踪、订单详情
- **支付系统**: 多种支付方式（在线支付、储值卡、月结）
- **储值卡**: 储值卡管理、余额查询、消费记录

### 📱 移动端特性
- **响应式设计**: 适配各种移动设备
- **用户友好**: 直观的购物流程和界面
- **地址管理**: 收货地址的增删改查
- **订单跟踪**: 实时订单状态更新
- **个人中心**: 用户信息、订单历史、地址管理

### 🛠️ 管理后台功能
- **数据统计**: 销售数据、用户统计、订单分析
- **商品管理**: 商品上架下架、分类管理、库存调整
- **订单管理**: 订单处理、状态更新、发货管理
- **用户管理**: 用户信息查看、状态管理
- **轮播图管理**: 首页轮播图配置
- **储值卡管理**: 储值卡发放、余额管理

### 🔐 安全特性
- **JWT认证**: 基于Token的身份验证
- **权限控制**: 管理员权限分级
- **数据加密**: 密码BCrypt加密
- **接口安全**: RESTful API安全防护

## 项目结构

```
icodestarmall_basic/
├── seafood-mall-backend/          # 后端服务
│   ├── src/main/java/com/seafoodmall/backend/
│   │   ├── common/                # 公共组件
│   │   ├── config/                # 配置类
│   │   ├── controller/            # 控制器层
│   │   ├── dto/                   # 数据传输对象
│   │   ├── entity/                # 实体类
│   │   ├── filter/                # 过滤器
│   │   ├── mapper/                # 数据访问层
│   │   ├── service/               # 业务逻辑层
│   │   └── util/                  # 工具类
│   ├── src/main/resources/
│   │   ├── db/                    # 数据库脚本
│   │   └── application.yml        # 配置文件
│   └── uploads/                   # 文件上传目录
├── seafood-mall-admin/            # 管理后台
│   ├── src/
│   │   ├── api/                   # API接口
│   │   ├── views/                 # 页面组件
│   │   ├── stores/                # 状态管理
│   │   ├── utils/                 # 工具函数
│   │   └── router/                # 路由配置
│   └── public/                    # 静态资源
└── seafood-mall-h5/               # H5移动端
    ├── src/
    │   ├── api/                   # API接口
    │   ├── views/                 # 页面组件
    │   ├── stores/                # 状态管理
    │   ├── utils/                 # 工具函数
    │   └── router/                # 路由配置
    └── public/                    # 静态资源
```

## 数据库设计

### 核心数据表
- **用户表** (`user`): 用户基本信息、类型、等级
- **管理员表** (`admin_user`): 后台管理员信息
- **商品表** (`product`): 商品信息、价格、库存
- **分类表** (`category`): 商品分类管理
- **订单表** (`order`): 订单主信息
- **订单项表** (`order_item`): 订单商品明细
- **用户地址表** (`user_address`): 收货地址管理
- **储值卡表** (`stored_value_card`): 储值卡信息
- **支付记录表** (`payment_record`): 支付流水
- **轮播图表** (`banner`): 首页轮播图

## 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 后端部署

1. **克隆项目**
```bash
git clone [项目地址]
cd icodestarmall_basic
```

2. **数据库配置**
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE seafood_mall_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据库脚本
mysql -u root -p seafood_mall_db < seafood-mall-backend/src/main/resources/db/schema.sql
mysql -u root -p seafood_mall_db < seafood-mall-backend/src/main/resources/db/data_db.sql
```

3. **修改配置文件**
编辑 `seafood-mall-backend/src/main/resources/application.yml`，配置数据库连接信息。

4. **启动后端服务**
```bash
cd seafood-mall-backend
mvn spring-boot:run
```

### 管理后台部署

1. **安装依赖**
```bash
cd seafood-mall-admin
npm install
```

2. **启动开发服务器**
```bash
npm run dev
```

3. **构建生产版本**
```bash
npm run build
```

### H5移动端部署

1. **安装依赖**
```bash
cd seafood-mall-h5
npm install
```

2. **启动开发服务器**
```bash
npm run dev
```

3. **构建生产版本**
```bash
npm run build
```

## API文档

### 用户相关接口
- `POST /api/user/login` - 用户登录
- `POST /api/user/register` - 用户注册
- `GET /api/user/info` - 获取用户信息
- `PUT /api/user/info` - 更新用户信息

### 商品相关接口
- `GET /api/product/list` - 获取商品列表
- `GET /api/product/{id}` - 获取商品详情
- `GET /api/category/list` - 获取分类列表

### 订单相关接口
- `POST /api/order/create` - 创建订单
- `GET /api/order/list` - 获取订单列表
- `GET /api/order/{id}` - 获取订单详情
- `PUT /api/order/{id}/status` - 更新订单状态

### 管理后台接口
- `POST /api/admin/login` - 管理员登录
- `GET /api/admin/dashboard` - 获取仪表板数据
- `GET /api/admin/product/list` - 获取商品管理列表
- `POST /api/admin/product` - 创建商品
- `PUT /api/admin/product/{id}` - 更新商品

## 部署说明

### 生产环境部署
1. **后端部署**: 使用Docker或直接部署到服务器
2. **前端部署**: 构建静态文件部署到Nginx
3. **数据库**: 配置主从复制，确保数据安全
4. **文件存储**: 配置CDN加速图片访问

### 性能优化
- 数据库索引优化
- Redis缓存热点数据
- 图片压缩和CDN加速
- 前端代码分割和懒加载

## 开发团队

- **开发公司**: ICodeStar 智码星科技
- **官网**: https://www.icodestar.net
- **许可证**: MIT License

## 更新日志

### v1.0.0 (2024-06-23)
- 初始版本发布
- 完整的电商功能实现
- 管理后台和H5移动端
- 多种支付方式支持

## 贡献指南

欢迎提交Issue和Pull Request来改进项目。

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。
