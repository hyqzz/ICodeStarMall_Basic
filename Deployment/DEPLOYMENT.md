# 海鲜商城系统部署文档

## 目录
- [系统概述](#系统概述)
- [环境要求](#环境要求)
- [开发环境部署](#开发环境部署)
- [生产环境部署](#生产环境部署)
- [Docker部署](#docker部署)
- [Nginx配置](#nginx配置)
- [数据库部署](#数据库部署)
- [性能优化](#性能优化)
- [监控与日志](#监控与日志)
- [故障排除](#故障排除)

## 系统概述

海鲜商城系统是一个基于 Spring Boot + Vue.js 的现代化电商平台，包含以下组件：

- **后端服务** (seafood-mall-backend): Spring Boot RESTful API
- **管理后台** (seafood-mall-admin): Vue 3 + Element Plus
- **H5移动端** (seafood-mall-h5): Vue 3 + Vant

### 系统架构
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   管理后台      │    │   H5移动端      │    │   后端API       │
│   (5173端口)    │    │   (5174端口)    │    │   (8080端口)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   MySQL数据库   │
                    │   (3306端口)    │
                    └─────────────────┘
```

## 环境要求

### 最低配置
- **CPU**: 2核心
- **内存**: 4GB RAM
- **存储**: 20GB 可用空间
- **网络**: 稳定的网络连接

### 软件要求
- **操作系统**: Linux (推荐 Ubuntu 20.04+) / Windows 10+ / macOS 10.15+
- **Java**: JDK 17+
- **Node.js**: 16.0+
- **MySQL**: 8.0+
- **Maven**: 3.6+
- **Nginx**: 1.18+ (生产环境)

## 开发环境部署

### 1. 克隆项目
```bash
git clone [项目地址]
cd icodestarmall_basic
```

### 2. 数据库配置

#### 2.1 安装MySQL
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server

# CentOS/RHEL
sudo yum install mysql-server

# macOS
brew install mysql
```

#### 2.2 启动MySQL服务
```bash
# Ubuntu/Debian
sudo systemctl start mysql
sudo systemctl enable mysql

# CentOS/RHEL
sudo systemctl start mysqld
sudo systemctl enable mysqld

# macOS
brew services start mysql
```

#### 2.3 创建数据库和用户
```bash
mysql -u root -p

# 创建数据库
CREATE DATABASE seafood_mall_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 创建用户（可选）
CREATE USER 'seafood_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON seafood_mall_db.* TO 'seafood_user'@'localhost';
FLUSH PRIVILEGES;

# 导入数据库脚本
mysql -u root -p seafood_mall_db < seafood-mall-backend/src/main/resources/db/schema.sql
mysql -u root -p seafood_mall_db < seafood-mall-backend/src/main/resources/db/data_db.sql
```

### 3. 后端服务部署

#### 3.1 配置数据库连接
编辑 `seafood-mall-backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/seafood_mall_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root  # 或 seafood_user
    password: root  # 或 your_password
```

#### 3.2 启动后端服务
```bash
cd seafood-mall-backend
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

### 4. 管理后台部署

#### 4.1 安装依赖
```bash
cd seafood-mall-admin
npm install
```

#### 4.2 启动开发服务器
```bash
npm run dev
```

管理后台将在 `http://localhost:5173` 启动。

### 5. H5移动端部署

#### 5.1 安装依赖
```bash
cd seafood-mall-h5
npm install
```

#### 5.2 启动开发服务器
```bash
npm run dev
```

H5移动端将在 `http://localhost:5174` 启动。

## 生产环境部署

### 1. 服务器准备

#### 1.1 系统更新
```bash
sudo apt update && sudo apt upgrade -y
```

#### 1.2 安装必要软件
```bash
sudo apt install -y openjdk-17-jdk maven nginx mysql-server git curl wget
```

### 2. 后端服务部署

#### 2.1 构建JAR包
```bash
cd seafood-mall-backend
mvn clean package -DskipTests
```

#### 2.2 创建服务用户
```bash
sudo useradd -r -s /bin/false seafood
sudo mkdir -p /opt/seafood-mall
sudo chown seafood:seafood /opt/seafood-mall
```

#### 2.3 部署应用
```bash
sudo cp target/seafood-mall-backend-1.0.0.jar /opt/seafood-mall/
sudo cp src/main/resources/application.yml /opt/seafood-mall/
sudo chown seafood:seafood /opt/seafood-mall/*
```

#### 2.4 创建systemd服务
创建文件 `/etc/systemd/system/seafood-mall.service`:
```ini
[Unit]
Description=Seafood Mall Backend Service
After=network.target mysql.service

[Service]
Type=simple
User=seafood
WorkingDirectory=/opt/seafood-mall
ExecStart=/usr/bin/java -jar seafood-mall-backend-1.0.0.jar
ExecReload=/bin/kill -HUP $MAINPID
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

#### 2.5 启动服务
```bash
sudo systemctl daemon-reload
sudo systemctl enable seafood-mall
sudo systemctl start seafood-mall
sudo systemctl status seafood-mall
```

### 3. 前端部署

#### 3.1 构建管理后台
```bash
cd seafood-mall-admin
npm run build
```

#### 3.2 构建H5移动端
```bash
cd seafood-mall-h5
npm run build
```

#### 3.3 部署到Nginx
```bash
sudo mkdir -p /var/www/seafood-mall
sudo cp -r seafood-mall-admin/dist/* /var/www/seafood-mall/admin/
sudo cp -r seafood-mall-h5/dist/* /var/www/seafood-mall/h5/
sudo chown -R www-data:www-data /var/www/seafood-mall
```

## Docker部署

### 1. 创建Dockerfile

#### 1.1 后端Dockerfile
创建 `seafood-mall-backend/Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/seafood-mall-backend-1.0.0.jar app.jar
COPY src/main/resources/application.yml application.yml

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 1.2 前端Dockerfile
创建 `seafood-mall-admin/Dockerfile`:
```dockerfile
FROM node:16-alpine as build

WORKDIR /app
COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
```

### 2. 创建docker-compose.yml
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: seafood_mall_db
    volumes:
      - mysql_data:/var/lib/mysql
      - ./seafood-mall-backend/src/main/resources/db:/docker-entrypoint-initdb.d
    ports:
      - "3306:3306"

  backend:
    build: ./seafood-mall-backend
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/seafood_mall_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    ports:
      - "8080:8080"
    volumes:
      - uploads:/app/uploads

  admin:
    build: ./seafood-mall-admin
    depends_on:
      - backend
    ports:
      - "80:80"

  h5:
    build: ./seafood-mall-h5
    depends_on:
      - backend
    ports:
      - "81:80"

volumes:
  mysql_data:
  uploads:
```

### 3. 启动Docker服务
```bash
docker-compose up -d
```

## Nginx配置

### 1. 主配置文件
创建 `/etc/nginx/sites-available/seafood-mall`:
```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 管理后台
    location /admin {
        alias /var/www/seafood-mall/admin;
        try_files $uri $uri/ /admin/index.html;
    }

    # H5移动端
    location / {
        root /var/www/seafood-mall/h5;
        try_files $uri $uri/ /index.html;
    }

    # API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 文件上传代理
    location /uploads {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 静态文件缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

### 2. 启用配置
```bash
sudo ln -s /etc/nginx/sites-available/seafood-mall /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

## 数据库部署

### 1. MySQL优化配置
编辑 `/etc/mysql/mysql.conf.d/mysqld.cnf`:
```ini
[mysqld]
# 基础配置
port = 3306
bind-address = 0.0.0.0
max_connections = 1000
max_connect_errors = 1000

# 字符集
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci

# 存储引擎
default-storage-engine = InnoDB
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2

# 查询缓存
query_cache_type = 1
query_cache_size = 128M

# 慢查询日志
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2
```

### 2. 数据库备份脚本
创建 `/opt/seafood-mall/backup.sh`:
```bash
#!/bin/bash
BACKUP_DIR="/opt/seafood-mall/backups"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="seafood_mall_db"

mkdir -p $BACKUP_DIR
mysqldump -u root -p $DB_NAME > $BACKUP_DIR/${DB_NAME}_${DATE}.sql

# 删除7天前的备份
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
```

## 性能优化

### 1. JVM优化
在启动脚本中添加JVM参数:
```bash
java -Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -jar app.jar
```

### 2. 数据库索引优化
```sql
-- 用户表索引
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_phone ON user(phone);

-- 订单表索引
CREATE INDEX idx_order_user_id ON `order`(user_id);
CREATE INDEX idx_order_status ON `order`(status);
CREATE INDEX idx_order_created_time ON `order`(created_time);

-- 商品表索引
CREATE INDEX idx_product_category_id ON product(category_id);
CREATE INDEX idx_product_status ON product(status);
```

### 3. 前端优化
- 启用Gzip压缩
- 配置CDN加速
- 图片懒加载
- 代码分割

## 监控与日志

### 1. 日志配置
编辑 `application.yml`:
```yaml
logging:
  file:
    name: /var/log/seafood-mall/application.log
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  level:
    com.seafoodmall.backend: INFO
    org.springframework.security: WARN
```

### 2. 监控脚本
创建 `/opt/seafood-mall/monitor.sh`:
```bash
#!/bin/bash

# 检查服务状态
if ! systemctl is-active --quiet seafood-mall; then
    echo "Seafood Mall service is down, restarting..."
    systemctl restart seafood-mall
fi

# 检查磁盘空间
DISK_USAGE=$(df / | awk 'NR==2 {print $5}' | sed 's/%//')
if [ $DISK_USAGE -gt 80 ]; then
    echo "Disk usage is high: ${DISK_USAGE}%"
fi

# 检查内存使用
MEMORY_USAGE=$(free | awk 'NR==2{printf "%.2f", $3*100/$2}')
if (( $(echo "$MEMORY_USAGE > 80" | bc -l) )); then
    echo "Memory usage is high: ${MEMORY_USAGE}%"
fi
```

## 故障排除

### 1. 常见问题

#### 1.1 后端服务无法启动
```bash
# 检查端口占用
sudo netstat -tlnp | grep 8080

# 检查日志
sudo journalctl -u seafood-mall -f

# 检查Java版本
java -version
```

#### 1.2 数据库连接失败
```bash
# 检查MySQL服务状态
sudo systemctl status mysql

# 检查数据库连接
mysql -u root -p -e "SHOW DATABASES;"

# 检查防火墙
sudo ufw status
```

#### 1.3 前端构建失败
```bash
# 清理缓存
npm cache clean --force
rm -rf node_modules package-lock.json
npm install

# 检查Node.js版本
node --version
npm --version
```

### 2. 性能问题排查

#### 2.1 数据库慢查询
```sql
-- 查看慢查询日志
SHOW VARIABLES LIKE 'slow_query_log%';
SHOW VARIABLES LIKE 'long_query_time';

-- 分析慢查询
SELECT * FROM mysql.slow_log ORDER BY start_time DESC LIMIT 10;
```

#### 2.2 内存泄漏排查
```bash
# 查看JVM内存使用
jstat -gc <pid>

# 生成堆转储
jmap -dump:format=b,file=heap.hprof <pid>
```

### 3. 安全加固

#### 3.1 防火墙配置
```bash
sudo ufw enable
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8080/tcp
```

#### 3.2 SSL证书配置
```bash
# 安装Certbot
sudo apt install certbot python3-certbot-nginx

# 获取SSL证书
sudo certbot --nginx -d your-domain.com
```

## 更新部署

### 1. 后端更新
```bash
# 停止服务
sudo systemctl stop seafood-mall

# 备份当前版本
sudo cp /opt/seafood-mall/seafood-mall-backend-1.0.0.jar /opt/seafood-mall/backup/

# 部署新版本
sudo cp target/seafood-mall-backend-1.0.0.jar /opt/seafood-mall/

# 启动服务
sudo systemctl start seafood-mall
```

### 2. 前端更新
```bash
# 构建新版本
npm run build

# 备份当前版本
sudo cp -r /var/www/seafood-mall/admin /var/www/seafood-mall/admin.backup

# 部署新版本
sudo cp -r dist/* /var/www/seafood-mall/admin/
```

## 联系支持

- **开发团队**: ICodeStar 智码星科技
- **官网**: https://www.icodestar.net
- **技术支持**: 如有问题请提交Issue或联系开发团队

---

**注意**: 本部署文档基于当前版本编写，请根据实际环境和需求进行调整。生产环境部署前请务必进行充分测试。 