# Руководство по развёртыванию SmartFlow на VPS

Полное руководство по деплою SmartFlow backend и n8n на VPS сервер.

## 📋 Предварительные требования

- VPS с Ubuntu 20.04+ или Debian 11+
- Root доступ или пользователь с sudo правами
- Доменное имя (опционально, для SSL)

## 🚀 Шаг 1: Подготовка сервера

### 1.1. Обновление системы

```bash
sudo apt update
sudo apt upgrade -y
```

### 1.2. Установка Java 17

```bash
sudo apt install openjdk-17-jdk -y
java -version
```

### 1.3. Установка PostgreSQL

```bash
sudo apt install postgresql postgresql-contrib -y

# Создание базы данных и пользователя
sudo -u postgres psql <<EOF
CREATE DATABASE smartflow_db;
CREATE USER smartflow_user WITH PASSWORD '22o7rL5PGWN4';
GRANT ALL PRIVILEGES ON DATABASE smartflow_db TO smartflow_user;
\c smartflow_db
GRANT ALL ON SCHEMA public TO smartflow_user;
\q
EOF

# Настройка для удалённых подключений
sudo nano /etc/postgresql/*/main/postgresql.conf
# Раскомментируйте: listen_addresses = '*'

sudo nano /etc/postgresql/*/main/pg_hba.conf
# Добавьте: host    all    all    0.0.0.0/0    md5

sudo systemctl restart postgresql
sudo systemctl enable postgresql
```

### 1.4. Установка Redis

```bash
sudo apt install redis-server -y

# Настройка пароля
sudo nano /etc/redis/redis.conf
# Найдите и раскомментируйте: requirepass ВАШ_ПАРОЛЬ_REDIS

sudo systemctl restart redis-server
sudo systemctl enable redis-server
```

### 1.5. Установка Docker (для n8n)

```bash
sudo apt install docker.io docker-compose -y
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER
```

## 🚀 Шаг 2: Деплой SmartFlow Backend

### 2.1. Клонирование репозитория

```bash
cd /opt
sudo git clone https://github.com/willaaaaayy/smartflow-music-poc.git smartflow
cd smartflow
```

### 2.2. Настройка application.yml

Убедитесь, что `application.yml` содержит правильные настройки для VPS:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/smartflow_db
    username: smartflow_user
    password: 22o7rL5PGWN4
  data:
    redis:
      host: localhost
      port: 6379
      password: ВАШ_ПАРОЛЬ_REDIS
```

### 2.3. Сборка проекта

```bash
./mvnw clean package -DskipTests
```

### 2.4. Создание systemd service

```bash
sudo nano /etc/systemd/system/smartflow.service
```

Вставьте:

```ini
[Unit]
Description=SmartFlow Backend Application
After=network.target postgresql.service redis-server.service

[Service]
Type=simple
User=YOUR_USERNAME
WorkingDirectory=/opt/smartflow
ExecStart=/usr/bin/java -jar /opt/smartflow/target/smartflow-backend-1.0.0.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=smartflow

[Install]
WantedBy=multi-user.target
```

Замените `YOUR_USERNAME` на ваше имя пользователя.

### 2.5. Запуск сервиса

```bash
sudo systemctl daemon-reload
sudo systemctl enable smartflow
sudo systemctl start smartflow
sudo systemctl status smartflow
```

### 2.6. Проверка работы

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/v1/search?query=test
```

## 🚀 Шаг 3: Деплой n8n

### 3.1. Запуск n8n через Docker

```bash
docker run -d \
  --name n8n \
  -p 5678:5678 \
  -v ~/.n8n:/home/node/.n8n \
  -e N8N_BASIC_AUTH_ACTIVE=true \
  -e N8N_BASIC_AUTH_USER=admin \
  -e N8N_BASIC_AUTH_PASSWORD=ВАШ_ПАРОЛЬ \
  n8nio/n8n
```

### 3.2. Создание systemd service для n8n

```bash
sudo nano /etc/systemd/system/n8n.service
```

```ini
[Unit]
Description=n8n workflow automation
After=network.target

[Service]
Type=simple
User=YOUR_USERNAME
ExecStart=/usr/bin/docker start -a n8n
ExecStop=/usr/bin/docker stop n8n
Restart=always

[Install]
WantedBy=multi-user.target
```

### 3.3. Запуск n8n

```bash
sudo systemctl enable n8n
sudo systemctl start n8n
```

### 3.4. Доступ к n8n

Откройте в браузере: `http://45.155.164.190:5678`

## 🚀 Шаг 4: Настройка Nginx (опционально)

### 4.1. Установка Nginx

```bash
sudo apt install nginx -y
```

### 4.2. Создание конфигурации

```bash
sudo nano /etc/nginx/sites-available/smartflow
```

```nginx
server {
    listen 80;
    server_name williiiiiss.com 45.155.164.190;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /n8n {
        proxy_pass http://localhost:5678;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 4.3. Активация конфигурации

```bash
sudo ln -s /etc/nginx/sites-available/smartflow /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### 4.4. Настройка SSL (Let's Encrypt)

```bash
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx -d williiiiiss.com
```

## 🔥 Шаг 5: Настройка файрвола

```bash
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw allow 5678/tcp  # n8n (опционально, если не через nginx)
sudo ufw enable
```

## ✅ Проверка работы

### Backend

```bash
# Health check
curl http://localhost:8080/actuator/health

# Поиск
curl "http://localhost:8080/api/v1/search?query=rock"

# Рекомендации
curl "http://localhost:8080/api/v1/wave/recommendations?userId=1&limit=10"
```

### n8n

1. Откройте `http://45.155.164.190:5678`
2. Войдите с учётными данными
3. Создайте workflow по инструкции из `docs/n8n-workflow-setup.md`

### Telegram бот

1. Найдите вашего бота в Telegram
2. Отправьте команду `/mywave`
3. Проверьте ответ

## 📝 Логи

```bash
# Логи SmartFlow
sudo journalctl -u smartflow -f

# Логи n8n
docker logs -f n8n

# Логи PostgreSQL
sudo tail -f /var/log/postgresql/postgresql-*.log

# Логи Redis
sudo tail -f /var/log/redis/redis-server.log
```

## 🔧 Устранение проблем

### Backend не запускается

```bash
# Проверьте логи
sudo journalctl -u smartflow -n 50

# Проверьте подключение к БД
psql -h localhost -U smartflow_user -d smartflow_db

# Проверьте Redis
redis-cli ping
```

### n8n не работает

```bash
# Проверьте статус контейнера
docker ps -a | grep n8n

# Перезапустите
docker restart n8n

# Проверьте логи
docker logs n8n
```

## 🔐 Безопасность

1. **Измените пароли по умолчанию**
2. **Используйте SSH ключи** вместо паролей
3. **Настройте fail2ban** для защиты от брутфорса
4. **Регулярно обновляйте систему**
5. **Используйте SSL/TLS** для всех соединений

## 📚 Дополнительные ресурсы

- [SmartFlow README](../README.md)
- [n8n Workflow Setup](./n8n-workflow-setup.md)
- [API Documentation](../README.md#api-endpoints)

