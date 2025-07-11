#!/bin/bash

# 海鲜商城系统快速启动脚本
# ICodeStar 智码星科技
# https://www.icodestar.net

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# 检查Docker是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed. Please install Docker first."
        echo "Installation guide: https://docs.docker.com/get-docker/"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose is not installed. Please install Docker Compose first."
        echo "Installation guide: https://docs.docker.com/compose/install/"
        exit 1
    fi
    
    # 检查Docker服务是否运行
    if ! docker info &> /dev/null; then
        log_error "Docker service is not running. Please start Docker first."
        exit 1
    fi
}

# 创建必要的目录和文件
create_directories() {
    log_step "Creating necessary directories and files..."
    
    # 创建目录
    mkdir -p nginx
    mkdir -p mysql/conf.d
    mkdir -p monitoring/grafana/dashboards
    mkdir -p monitoring/grafana/datasources
    mkdir -p logs/backend
    mkdir -p logs/nginx
    
    # 创建MySQL配置文件
    cat > mysql/conf.d/my.cnf << 'EOF'
[mysqld]
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
default-storage-engine = InnoDB
innodb_buffer_pool_size = 256M
max_connections = 200
EOF

    # 创建Nginx配置文件
    cat > nginx/nginx.conf << 'EOF'
events {
    worker_connections 1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;
    
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';
    
    access_log /var/log/nginx/access.log main;
    error_log /var/log/nginx/error.log;
    
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;
    
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;
    
    include /etc/nginx/conf.d/*.conf;
}
EOF

    # 创建管理后台Nginx配置
    cat > nginx/admin.conf << 'EOF'
server {
    listen 80;
    server_name localhost;
    
    root /usr/share/nginx/html;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
EOF

    # 创建H5移动端Nginx配置
    cat > nginx/h5.conf << 'EOF'
server {
    listen 80;
    server_name localhost;
    
    root /usr/share/nginx/html;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
EOF

    # 创建Prometheus配置
    cat > monitoring/prometheus.yml << 'EOF'
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'seafood-mall-backend'
    static_configs:
      - targets: ['backend:8080']
    metrics_path: '/actuator/prometheus'
EOF

    log_info "Directories and configuration files created."
}

# 构建项目
build_project() {
    log_step "Building project components..."
    
    # 构建后端
    log_info "Building backend..."
    cd seafood-mall-backend
    if [ -f "pom.xml" ]; then
        mvn clean package -DskipTests
    else
        log_warn "Backend pom.xml not found, skipping backend build."
    fi
    cd ..
    
    # 构建管理后台
    log_info "Building admin dashboard..."
    cd seafood-mall-admin
    if [ -f "package.json" ]; then
        npm install
        npm run build
    else
        log_warn "Admin package.json not found, skipping admin build."
    fi
    cd ..
    
    # 构建H5移动端
    log_info "Building H5 mobile client..."
    cd seafood-mall-h5
    if [ -f "package.json" ]; then
        npm install
        npm run build
    else
        log_warn "H5 package.json not found, skipping H5 build."
    fi
    cd ..
    
    log_info "Project build completed."
}

# 启动服务
start_services() {
    log_step "Starting services with Docker Compose..."
    
    # 停止现有服务
    docker-compose down
    
    # 启动服务
    docker-compose up -d
    
    # 等待服务启动
    log_info "Waiting for services to start..."
    sleep 30
    
    # 检查服务状态
    check_service_status
}

# 检查服务状态
check_service_status() {
    log_step "Checking service status..."
    
    services=("mysql" "backend" "admin" "h5" "nginx")
    
    for service in "${services[@]}"; do
        if docker-compose ps | grep -q "$service.*Up"; then
            log_info "$service: Running"
        else
            log_warn "$service: Not running"
        fi
    done
}

# 显示访问信息
show_access_info() {
    log_info "Deployment completed successfully!"
    echo
    echo "=========================================="
    echo "          访问信息"
    echo "=========================================="
    echo "管理后台: http://localhost"
    echo "H5移动端: http://localhost:81"
    echo "后端API: http://localhost:8080"
    echo "数据库: localhost:3306"
    echo "Redis: localhost:6379"
    echo
    echo "监控服务（可选）:"
    echo "Prometheus: http://localhost:9090"
    echo "Grafana: http://localhost:3000 (admin/admin123)"
    echo
    echo "服务管理:"
    echo "  查看状态: docker-compose ps"
    echo "  查看日志: docker-compose logs -f [service_name]"
    echo "  停止服务: docker-compose down"
    echo "  重启服务: docker-compose restart"
    echo
    echo "默认账号:"
    echo "  管理员: superadmin / 123456"
    echo "  数据库: root / root"
    echo "=========================================="
}

# 健康检查
health_check() {
    log_step "Performing health check..."
    
    # 检查后端API
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        log_info "Backend API: Healthy"
    else
        log_warn "Backend API: Unhealthy"
    fi
    
    # 检查管理后台
    if curl -f http://localhost &> /dev/null; then
        log_info "Admin Dashboard: Healthy"
    else
        log_warn "Admin Dashboard: Unhealthy"
    fi
    
    # 检查H5移动端
    if curl -f http://localhost:81 &> /dev/null; then
        log_info "H5 Mobile: Healthy"
    else
        log_warn "H5 Mobile: Unhealthy"
    fi
}

# 清理函数
cleanup() {
    log_step "Cleaning up..."
    docker-compose down -v
    log_info "Cleanup completed."
}

# 帮助信息
show_help() {
    echo "海鲜商城系统快速启动脚本"
    echo
    echo "用法: $0 [选项]"
    echo
    echo "选项:"
    echo "  start       启动所有服务"
    echo "  stop        停止所有服务"
    echo "  restart     重启所有服务"
    echo "  status      查看服务状态"
    echo "  logs        查看服务日志"
    echo "  build       构建项目"
    echo "  clean       清理所有数据"
    echo "  health      健康检查"
    echo "  help        显示此帮助信息"
    echo
    echo "示例:"
    echo "  $0 start     # 启动所有服务"
    echo "  $0 logs      # 查看日志"
    echo "  $0 stop      # 停止服务"
}

# 主函数
main() {
    case "${1:-start}" in
        start)
            echo "=========================================="
            echo "      海鲜商城系统快速启动"
            echo "=========================================="
            echo
            
            check_docker
            create_directories
            build_project
            start_services
            health_check
            show_access_info
            ;;
        stop)
            log_step "Stopping services..."
            docker-compose down
            log_info "Services stopped."
            ;;
        restart)
            log_step "Restarting services..."
            docker-compose restart
            log_info "Services restarted."
            ;;
        status)
            docker-compose ps
            ;;
        logs)
            if [ -n "$2" ]; then
                docker-compose logs -f "$2"
            else
                docker-compose logs -f
            fi
            ;;
        build)
            check_docker
            create_directories
            build_project
            log_info "Build completed."
            ;;
        clean)
            log_warn "This will remove all data including database!"
            read -p "Are you sure? (y/N): " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                cleanup
            fi
            ;;
        health)
            health_check
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            log_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@" 