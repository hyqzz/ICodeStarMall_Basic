#!/bin/bash

# 海鲜商城系统自动化部署脚本
# ICodeStar 智码星科技
# https://www.icodestar.net

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
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

# 检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        log_error "$1 is not installed. Please install it first."
        exit 1
    fi
}

# 检查系统要求
check_requirements() {
    log_info "Checking system requirements..."
    
    check_command java
    check_command mvn
    check_command node
    check_command npm
    check_command mysql
    
    # 检查Java版本
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        log_error "Java 17 or higher is required. Current version: $JAVA_VERSION"
        exit 1
    fi
    
    # 检查Node.js版本
    NODE_VERSION=$(node --version | cut -d'v' -f2 | cut -d'.' -f1)
    if [ "$NODE_VERSION" -lt 16 ]; then
        log_error "Node.js 16 or higher is required. Current version: $NODE_VERSION"
        exit 1
    fi
    
    log_info "System requirements check passed."
}

# 数据库配置
setup_database() {
    log_info "Setting up database..."
    
    # 检查数据库是否存在
    if mysql -u root -p -e "USE seafood_mall_db;" 2>/dev/null; then
        log_warn "Database seafood_mall_db already exists."
        read -p "Do you want to recreate it? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            mysql -u root -p -e "DROP DATABASE IF EXISTS seafood_mall_db;"
        else
            log_info "Using existing database."
            return
        fi
    fi
    
    # 创建数据库
    mysql -u root -p -e "CREATE DATABASE seafood_mall_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    
    # 导入数据库脚本
    if [ -f "seafood-mall-backend/src/main/resources/db/schema.sql" ]; then
        mysql -u root -p seafood_mall_db < seafood-mall-backend/src/main/resources/db/schema.sql
    fi
    
    if [ -f "seafood-mall-backend/src/main/resources/db/data_db.sql" ]; then
        mysql -u root -p seafood_mall_db < seafood-mall-backend/src/main/resources/db/data_db.sql
    fi
    
    log_info "Database setup completed."
}

# 构建后端
build_backend() {
    log_info "Building backend..."
    
    cd seafood-mall-backend
    
    # 清理并构建
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        log_info "Backend build completed successfully."
    else
        log_error "Backend build failed."
        exit 1
    fi
    
    cd ..
}

# 构建前端
build_frontend() {
    log_info "Building frontend applications..."
    
    # 构建管理后台
    log_info "Building admin dashboard..."
    cd seafood-mall-admin
    npm install
    npm run build
    cd ..
    
    # 构建H5移动端
    log_info "Building H5 mobile client..."
    cd seafood-mall-h5
    npm install
    npm run build
    cd ..
    
    log_info "Frontend build completed successfully."
}

# 创建部署目录
create_deploy_dirs() {
    log_info "Creating deployment directories..."
    
    sudo mkdir -p /opt/seafood-mall
    sudo mkdir -p /var/www/seafood-mall/admin
    sudo mkdir -p /var/www/seafood-mall/h5
    sudo mkdir -p /var/log/seafood-mall
    
    # 创建服务用户
    if ! id "seafood" &>/dev/null; then
        sudo useradd -r -s /bin/false seafood
    fi
    
    sudo chown -R seafood:seafood /opt/seafood-mall
    sudo chown -R www-data:www-data /var/www/seafood-mall
    sudo chown -R seafood:seafood /var/log/seafood-mall
    
    log_info "Deployment directories created."
}

# 部署应用
deploy_applications() {
    log_info "Deploying applications..."
    
    # 部署后端
    sudo cp seafood-mall-backend/target/seafood-mall-backend-1.0.0.jar /opt/seafood-mall/
    sudo cp seafood-mall-backend/src/main/resources/application.yml /opt/seafood-mall/
    sudo chown seafood:seafood /opt/seafood-mall/*
    
    # 部署前端
    sudo cp -r seafood-mall-admin/dist/* /var/www/seafood-mall/admin/
    sudo cp -r seafood-mall-h5/dist/* /var/www/seafood-mall/h5/
    sudo chown -R www-data:www-data /var/www/seafood-mall
    
    log_info "Applications deployed successfully."
}

# 创建systemd服务
create_systemd_service() {
    log_info "Creating systemd service..."
    
    cat > /tmp/seafood-mall.service << EOF
[Unit]
Description=Seafood Mall Backend Service
After=network.target mysql.service

[Service]
Type=simple
User=seafood
WorkingDirectory=/opt/seafood-mall
ExecStart=/usr/bin/java -Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -jar seafood-mall-backend-1.0.0.jar
ExecReload=/bin/kill -HUP \$MAINPID
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF
    
    sudo cp /tmp/seafood-mall.service /etc/systemd/system/
    sudo systemctl daemon-reload
    sudo systemctl enable seafood-mall
    
    log_info "Systemd service created."
}

# 启动服务
start_services() {
    log_info "Starting services..."
    
    sudo systemctl start seafood-mall
    
    if systemctl is-active --quiet seafood-mall; then
        log_info "Seafood Mall backend service started successfully."
    else
        log_error "Failed to start Seafood Mall backend service."
        sudo journalctl -u seafood-mall -n 20
        exit 1
    fi
}

# 创建Nginx配置
create_nginx_config() {
    log_info "Creating Nginx configuration..."
    
    cat > /tmp/seafood-mall-nginx << EOF
server {
    listen 80;
    server_name localhost;

    # 管理后台
    location /admin {
        alias /var/www/seafood-mall/admin;
        try_files \$uri \$uri/ /admin/index.html;
        
        # 缓存配置
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }

    # H5移动端
    location / {
        root /var/www/seafood-mall/h5;
        try_files \$uri \$uri/ /index.html;
        
        # 缓存配置
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }

    # API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        
        # 超时配置
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }

    # 文件上传代理
    location /uploads {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    # Gzip压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;
}
EOF
    
    sudo cp /tmp/seafood-mall-nginx /etc/nginx/sites-available/seafood-mall
    sudo ln -sf /etc/nginx/sites-available/seafood-mall /etc/nginx/sites-enabled/
    
    # 测试Nginx配置
    if sudo nginx -t; then
        sudo systemctl reload nginx
        log_info "Nginx configuration applied successfully."
    else
        log_error "Nginx configuration test failed."
        exit 1
    fi
}

# 创建监控脚本
create_monitor_script() {
    log_info "Creating monitoring script..."
    
    cat > /opt/seafood-mall/monitor.sh << 'EOF'
#!/bin/bash

# 海鲜商城系统监控脚本

LOG_FILE="/var/log/seafood-mall/monitor.log"
BACKUP_DIR="/opt/seafood-mall/backups"

# 创建日志目录
mkdir -p $(dirname $LOG_FILE)
mkdir -p $BACKUP_DIR

log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" >> $LOG_FILE
}

# 检查服务状态
check_service() {
    if ! systemctl is-active --quiet seafood-mall; then
        log "ERROR: Seafood Mall service is down, restarting..."
        systemctl restart seafood-mall
        sleep 5
        if systemctl is-active --quiet seafood-mall; then
            log "INFO: Service restarted successfully"
        else
            log "ERROR: Failed to restart service"
        fi
    fi
}

# 检查磁盘空间
check_disk() {
    DISK_USAGE=$(df / | awk 'NR==2 {print $5}' | sed 's/%//')
    if [ $DISK_USAGE -gt 80 ]; then
        log "WARN: Disk usage is high: ${DISK_USAGE}%"
    fi
}

# 检查内存使用
check_memory() {
    MEMORY_USAGE=$(free | awk 'NR==2{printf "%.0f", $3*100/$2}')
    if [ $MEMORY_USAGE -gt 80 ]; then
        log "WARN: Memory usage is high: ${MEMORY_USAGE}%"
    fi
}

# 数据库备份
backup_database() {
    DATE=$(date +%Y%m%d_%H%M%S)
    BACKUP_FILE="$BACKUP_DIR/seafood_mall_db_${DATE}.sql"
    
    if mysqldump -u root -p seafood_mall_db > $BACKUP_FILE 2>/dev/null; then
        log "INFO: Database backup created: $BACKUP_FILE"
        
        # 删除7天前的备份
        find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
    else
        log "ERROR: Database backup failed"
    fi
}

# 主函数
main() {
    log "INFO: Starting monitoring check"
    
    check_service
    check_disk
    check_memory
    
    # 每天凌晨2点进行数据库备份
    if [ $(date +%H) -eq 2 ]; then
        backup_database
    fi
    
    log "INFO: Monitoring check completed"
}

main
EOF
    
    chmod +x /opt/seafood-mall/monitor.sh
    sudo chown seafood:seafood /opt/seafood-mall/monitor.sh
    
    # 添加到crontab
    (crontab -l 2>/dev/null; echo "*/5 * * * * /opt/seafood-mall/monitor.sh") | crontab -
    
    log_info "Monitoring script created and scheduled."
}

# 显示部署信息
show_deployment_info() {
    log_info "Deployment completed successfully!"
    echo
    echo "=========================================="
    echo "          部署完成信息"
    echo "=========================================="
    echo "后端服务: http://localhost:8080"
    echo "管理后台: http://localhost/admin"
    echo "H5移动端: http://localhost"
    echo "API文档: http://localhost:8080/swagger-ui.html"
    echo
    echo "服务状态检查:"
    echo "  sudo systemctl status seafood-mall"
    echo "  sudo systemctl status nginx"
    echo
    echo "日志查看:"
    echo "  sudo journalctl -u seafood-mall -f"
    echo "  tail -f /var/log/seafood-mall/monitor.log"
    echo
    echo "默认管理员账号:"
    echo "  用户名: superadmin"
    echo "  密码: 123456"
    echo "=========================================="
}

# 主函数
main() {
    echo "=========================================="
    echo "      海鲜商城系统自动化部署脚本"
    echo "=========================================="
    echo
    
    # 检查是否为root用户
    if [ "$EUID" -eq 0 ]; then
        log_error "Please do not run this script as root."
        exit 1
    fi
    
    # 检查参数
    if [ "$1" = "--help" ] || [ "$1" = "-h" ]; then
        echo "Usage: $0 [OPTIONS]"
        echo "Options:"
        echo "  --help, -h     Show this help message"
        echo "  --skip-db      Skip database setup"
        echo "  --skip-build   Skip building applications"
        exit 0
    fi
    
    SKIP_DB=false
    SKIP_BUILD=false
    
    for arg in "$@"; do
        case $arg in
            --skip-db)
                SKIP_DB=true
                shift
                ;;
            --skip-build)
                SKIP_BUILD=true
                shift
                ;;
        esac
    done
    
    # 执行部署步骤
    check_requirements
    
    if [ "$SKIP_DB" = false ]; then
        setup_database
    fi
    
    if [ "$SKIP_BUILD" = false ]; then
        build_backend
        build_frontend
    fi
    
    create_deploy_dirs
    deploy_applications
    create_systemd_service
    start_services
    create_nginx_config
    create_monitor_script
    show_deployment_info
}

# 执行主函数
main "$@" 