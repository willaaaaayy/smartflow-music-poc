# Скрипты для настройки и деплоя SmartFlow

## 📋 Содержание

1. `setup-vps.sh` - Автоматическая настройка VPS (PostgreSQL, Redis, Nginx)
2. `deploy.sh` - Скрипт для деплоя приложения на сервер

## 🚀 Быстрый старт

### Шаг 1: Настройка VPS

1. Подключитесь к серверу:
```bash
ssh root@45.155.164.190
```

2. Скопируйте скрипт на сервер:
```bash
scp scripts/setup-vps.sh root@45.155.164.190:/tmp/
```

3. Запустите скрипт на сервере:
```bash
ssh root@45.155.164.190
chmod +x /tmp/setup-vps.sh
/tmp/setup-vps.sh
```

4. После выполнения скрипта:
   - Замените `ВАШ_ПАРОЛЬ_REDIS` в `/etc/redis/redis.conf`
   - Замените `YOUR_USERNAME` в `/etc/systemd/system/smartflow.service`

### Шаг 2: Настройка SSL сертификата

```bash
sudo certbot --nginx -d williiiiiss.com
```

### Шаг 3: Деплой приложения

1. Сделайте скрипт исполняемым:
```bash
chmod +x scripts/deploy.sh
```

2. Запустите деплой:
```bash
./scripts/deploy.sh root
```

Или вручную:
```bash
# Сборка
mvn clean package -DskipTests

# Копирование на сервер
scp target/smartflow-backend-1.0.0.jar root@45.155.164.190:/opt/smartflow/

# Запуск
ssh root@45.155.164.190 "sudo systemctl start smartflow"
```

## 🔧 Ручная настройка (если скрипт не подходит)

### PostgreSQL

```bash
sudo -u postgres psql
CREATE DATABASE smartflow_db;
CREATE USER smartflow_user WITH PASSWORD '22o7rL5PGWN4';
GRANT ALL PRIVILEGES ON DATABASE smartflow_db TO smartflow_user;
\q
```

### Redis

Отредактируйте `/etc/redis/redis.conf`:
```
requirepass ваш_пароль_redis
```

### Systemd Service

Создайте `/etc/systemd/system/smartflow.service` (см. скрипт)

## 📝 Проверка работы

```bash
# Статус приложения
sudo systemctl status smartflow

# Логи
sudo journalctl -u smartflow -f

# Проверка эндпоинтов
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/v1/search?query=test
```

## 🔐 Безопасность

- Не храните пароли в открытом виде в репозитории
- Используйте переменные окружения для production
- Настройте файрвол правильно
- Используйте SSL/TLS для всех соединений

