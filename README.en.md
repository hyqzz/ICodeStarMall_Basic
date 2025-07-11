# Seafood Mall System

## Project Overview

This project is produced by ICodeStar Technology. It is truly open - sourced under the MIT license. You can use it arbitrarily, but please retain the copyright information.
https://www.icodestar.net

The mall system is a modern e - commerce platform based on Spring Boot + Vue.js, designed specifically for product sales. The system adopts a front - end and back - end separated architecture, including a management background, an H5 mobile terminal, and a RESTful API back - end service, providing a complete e - commerce solution.

This project is very suitable as an example for learning full - stack development or as a starting point for small - scale e - commerce projects. The code structure is clear, the documentation is complete, and there are various deployment options, making it a great example of a full - stack project.

## Technology Stack

### Backend Technology Stack
- **Framework**: Spring Boot 2.7.18
- **Database**: MySQL 8.0
- **ORM**: MyBatis-Plus 3.5.3.1
- **Security**: Spring Security + JWT
- **Utility Libraries**: Hutool 5.8.20, Lombok 1.18.38
- **Java Version**: JDK 17

### Frontend Technology Stack
- **Admin Dashboard**: Vue 3 + TypeScript + Element Plus + Vite
- **H5 Mobile Client**: Vue 3 + TypeScript + Vant + Vite
- **State Management**: Pinia
- **Routing**: Vue Router 4
- **HTTP Client**: Axios

## Features

### 🏪 Core E-commerce Features
- **Product Management**: Product categories, product information, inventory management, multi-image display
- **User System**: User registration, login, profile management, address management
- **Shopping Cart**: Add products, modify quantities, price calculation
- **Order System**: Order creation, status tracking, order details
- **Payment System**: Multiple payment methods (online payment, stored value card, monthly billing)
- **Stored Value Card**: Card management, balance inquiry, consumption records

### 📱 Mobile Features
- **Responsive Design**: Adapts to various mobile devices
- **User-Friendly**: Intuitive shopping process and interface
- **Address Management**: Add, delete, modify, and query shipping addresses
- **Order Tracking**: Real-time order status updates
- **Personal Center**: User information, order history, address management

### 🛠️ Admin Dashboard Features
- **Data Statistics**: Sales data, user statistics, order analysis
- **Product Management**: Product listing/delisting, category management, inventory adjustment
- **Order Management**: Order processing, status updates, shipping management
- **User Management**: User information viewing, status management
- **Banner Management**: Homepage banner configuration
- **Stored Value Card Management**: Card issuance, balance management

### 🔐 Security Features
- **JWT Authentication**: Token-based identity verification
- **Permission Control**: Admin permission levels
- **Data Encryption**: BCrypt password encryption
- **API Security**: RESTful API security protection

## Project Structure

```
icodestarmall_basic/
├── seafood-mall-backend/          # Backend Service
│   ├── src/main/java/com/seafoodmall/backend/
│   │   ├── common/                # Common Components
│   │   ├── config/                # Configuration Classes
│   │   ├── controller/            # Controller Layer
│   │   ├── dto/                   # Data Transfer Objects
│   │   ├── entity/                # Entity Classes
│   │   ├── filter/                # Filters
│   │   ├── mapper/                # Data Access Layer
│   │   ├── service/               # Business Logic Layer
│   │   └── util/                  # Utility Classes
│   ├── src/main/resources/
│   │   ├── db/                    # Database Scripts
│   │   └── application.yml        # Configuration Files
│   └── uploads/                   # File Upload Directory
├── seafood-mall-admin/            # Admin Dashboard
│   ├── src/
│   │   ├── api/                   # API Interfaces
│   │   ├── views/                 # Page Components
│   │   ├── stores/                # State Management
│   │   ├── utils/                 # Utility Functions
│   │   └── router/                # Route Configuration
│   └── public/                    # Static Resources
└── seafood-mall-h5/               # H5 Mobile Client
    ├── src/
    │   ├── api/                   # API Interfaces
    │   ├── views/                 # Page Components
    │   ├── stores/                # State Management
    │   ├── utils/                 # Utility Functions
    │   └── router/                # Route Configuration
    └── public/                    # Static Resources
```

## Database Design

### Core Data Tables
- **User Table** (`user`): User basic information, types, levels
- **Admin Table** (`admin_user`): Backend admin information
- **Product Table** (`product`): Product information, pricing, inventory
- **Category Table** (`category`): Product category management
- **Order Table** (`order`): Order main information
- **Order Item Table** (`order_item`): Order product details
- **User Address Table** (`user_address`): Shipping address management
- **Stored Value Card Table** (`stored_value_card`): Card information
- **Payment Record Table** (`payment_record`): Payment transactions
- **Banner Table** (`banner`): Homepage banners

## Quick Start

### Prerequisites
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### Backend Deployment

1. **Clone the project**
```bash
git clone [project-url]
cd icodestarmall_basic
```

2. **Database configuration**
```bash
# Create database
mysql -u root -p
CREATE DATABASE seafood_mall_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Import database scripts
mysql -u root -p seafood_mall_db < seafood-mall-backend/src/main/resources/db/schema.sql
mysql -u root -p seafood_mall_db < seafood-mall-backend/src/main/resources/db/data_db.sql
```

3. **Modify configuration file**
Edit `seafood-mall-backend/src/main/resources/application.yml` to configure database connection information.

4. **Start backend service**
```bash
cd seafood-mall-backend
mvn spring-boot:run
```

### Admin Dashboard Deployment

1. **Install dependencies**
```bash
cd seafood-mall-admin
npm install
```

2. **Start development server**
```bash
npm run dev
```

3. **Build for production**
```bash
npm run build
```

### H5 Mobile Client Deployment

1. **Install dependencies**
```bash
cd seafood-mall-h5
npm install
```

2. **Start development server**
```bash
npm run dev
```

3. **Build for production**
```bash
npm run build
```

## API Documentation

### User-related APIs
- `POST /api/user/login` - User login
- `POST /api/user/register` - User registration
- `GET /api/user/info` - Get user information
- `PUT /api/user/info` - Update user information

### Product-related APIs
- `GET /api/product/list` - Get product list
- `GET /api/product/{id}` - Get product details
- `GET /api/category/list` - Get category list

### Order-related APIs
- `POST /api/order/create` - Create order
- `GET /api/order/list` - Get order list
- `GET /api/order/{id}` - Get order details
- `PUT /api/order/{id}/status` - Update order status

### Admin Dashboard APIs
- `POST /api/admin/login` - Admin login
- `GET /api/admin/dashboard` - Get dashboard data
- `GET /api/admin/product/list` - Get product management list
- `POST /api/admin/product` - Create product
- `PUT /api/admin/product/{id}` - Update product

## Deployment Guide

### Production Environment Deployment
1. **Backend Deployment**: Use Docker or deploy directly to server
2. **Frontend Deployment**: Build static files and deploy to Nginx
3. **Database**: Configure master-slave replication for data security
4. **File Storage**: Configure CDN for image access acceleration

### Performance Optimization
- Database index optimization
- Redis caching for hot data
- Image compression and CDN acceleration
- Frontend code splitting and lazy loading

## Development Team

- **Development Company**: ICodeStar Technology
- **Website**: https://www.icodestar.net
- **License**: MIT License

## Changelog

### v1.0.0 (2024-06-23)
- Initial version release
- Complete e-commerce functionality implementation
- Admin dashboard and H5 mobile client
- Multiple payment method support

## Contributing

We welcome Issue submissions and Pull Requests to improve the project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
