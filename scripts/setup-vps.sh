#!/bin/bash

# Скрипт автоматической настройки SmartFlow на VPS
# Запускать от root или с sudo

set -e

echo "🚀 Начало настройки SmartFlow на VPS..."

# Цвета для вывода
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Проверка прав root
if [ "$EUID" -ne 0 ]; then 
    echo -e "${RED}Пожалуйста, запустите скрипт с sudo${NC}"
    exit 1
fi

# Обновление системы
echo -e "${YELLOW}📦 Обновление системы...${NC}"
apt-get update
apt-get upgrade -y

# Установка необходимых пакетов
echo -e "${YELLOW}📦 Установка необходимых пакетов...${NC}"
apt-get install -y \
    openjdk-17-jdk \
    postgresql \
    postgresql-contrib \
    redis-server \
    maven \
    nginx \
    certbot \
    python3-certbot-nginx \
    git \
    ufw

# Настройка PostgreSQL
echo -e "${YELLOW}🗄️  Настройка PostgreSQL...${NC}"

# Создание базы данных и пользователя
sudo -u postgres psql <<EOF
-- Создание базы данных
CREATE DATABASE smartflow_db;

-- Создание пользователя
CREATE USER smartflow_user WITH PASSWORD '22o7rL5PGWN4';

-- Выдача прав
GRANT ALL PRIVILEGES ON DATABASE smartflow_db TO smartflow_user;

-- Подключение к базе и выдача прав на схему
\c smartflow_db
GRANT ALL ON SCHEMA public TO smartflow_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO smartflow_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO smartflow_user;

\q
EOF

# Настройка PostgreSQL для удаленных подключений
PG_VERSION=$(psql --version | grep -oP '\d+' | head -1)
PG_CONF="/etc/postgresql/${PG_VERSION}/main/postgresql.conf"
PG_HBA="/etc/postgresql/${PG_VERSION}/main/pg_hba.conf"

# Разрешить подключения
sed -i "s/#listen_addresses = 'localhost'/listen_addresses = '*'/" $PG_CONF

# Настройка pg_hba.conf для SSL
echo "hostssl    all             all             0.0.0.0/0               md5" >> $PG_HBA
echo "host       all             all             0.0.0.0/0               md5" >> $PG_HBA

# Включение SSL в PostgreSQL
sed -i "s/#ssl = on/ssl = on/" $PG_CONF

# Перезапуск PostgreSQL
systemctl restart postgresql
systemctl enable postgresql

echo -e "${GREEN}✅ PostgreSQL настроен${NC}"

# Настройка Redis
echo -e "${YELLOW}🔴 Настройка Redis...${NC}"

# Настройка Redis для удаленных подключений
REDIS_CONF="/etc/redis/redis.conf"

# Комментируем bind только localhost
sed -i 's/^bind 127.0.0.1 ::1/#bind 127.0.0.1 ::1/' $REDIS_CONF

# Устанавливаем пароль (замените на ваш реальный пароль)
REDIS_PASSWORD="ВАШ_ПАРОЛЬ_REDIS"
sed -i "s/# requirepass foobared/requirepass ${REDIS_PASSWORD}/" $REDIS_CONF

# Включаем защищенный режим (опционально, если не используется пароль)
# sed -i 's/^protected-mode yes/protected-mode no/' $REDIS_CONF

# Перезапуск Redis
systemctl restart redis-server
systemctl enable redis-server

echo -e "${GREEN}✅ Redis настроен${NC}"

# Настройка файрвола
echo -e "${YELLOW}🔥 Настройка файрвола...${NC}"
ufw allow 22/tcp    # SSH
ufw allow 80/tcp    # HTTP
ufw allow 443/tcp   # HTTPS
ufw allow 5432/tcp  # PostgreSQL (только для внутренней сети)
ufw allow 6379/tcp  # Redis (только для внутренней сети)
ufw --force enable

echo -e "${GREEN}✅ Файрвол настроен${NC}"

# Создание директории для приложения
echo -e "${YELLOW}📁 Создание директорий...${NC}"
mkdir -p /opt/smartflow
mkdir -p /opt/smartflow/logs
chown -R $SUDO_USER:$SUDO_USER /opt/smartflow

echo -e "${GREEN}✅ Директории созданы${NC}"

# Создание systemd service файла
echo -e "${YELLOW}⚙️  Создание systemd service...${NC}"
cat > /etc/systemd/system/smartflow.service <<'SERVICEEOF'
[Unit]
Description=SmartFlow Backend Application
After=network.target postgresql.service redis-server.service

[Service]
Type=simple
User=YOUR_USERNAME
WorkingDirectory=/opt/smartflow
ExecStart=/usr/bin/java -jar /opt/smartflow/smartflow-backend-1.0.0.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=smartflow

[Install]
WantedBy=multi-user.target
SERVICEEOF

echo -e "${YELLOW}⚠️  Не забудьте заменить YOUR_USERNAME в /etc/systemd/system/smartflow.service${NC}"

# Перезагрузка systemd
systemctl daemon-reload

echo -e "${GREEN}✅ Systemd service создан${NC}"

# Настройка Nginx (базовая конфигурация)
echo -e "${YELLOW}🌐 Настройка Nginx...${NC}"
cat > /etc/nginx/sites-available/smartflow <<'NGINXEOF'
server {
    listen 80;
    server_name williiiiiss.com 45.155.164.190;

    # Редирект на HTTPS (после настройки SSL)
    # return 301 https://$server_name$request_uri;

    # Временная конфигурация для HTTP
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
NGINXEOF

ln -sf /etc/nginx/sites-available/smartflow /etc/nginx/sites-enabled/
rm -f /etc/nginx/sites-enabled/default

# Проверка конфигурации Nginx
nginx -t

# Перезапуск Nginx
systemctl restart nginx
systemctl enable nginx

echo -e "${GREEN}✅ Nginx настроен${NC}"

echo -e "${GREEN}🎉 Базовая настройка завершена!${NC}"
echo -e "${YELLOW}📝 Следующие шаги:${NC}"
echo "1. Замените ВАШ_ПАРОЛЬ_REDIS на реальный пароль в /etc/redis/redis.conf"
echo "2. Замените YOUR_USERNAME в /etc/systemd/system/smartflow.service"
echo "3. Скопируйте JAR файл приложения в /opt/smartflow/"
echo "4. Настройте SSL сертификат: sudo certbot --nginx -d williiiiiss.com"
echo "5. Запустите приложение: sudo systemctl start smartflow"

