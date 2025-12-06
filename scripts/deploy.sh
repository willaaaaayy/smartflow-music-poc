#!/bin/bash

# Скрипт для деплоя приложения на VPS
# Использование: ./deploy.sh user@45.155.164.190

set -e

# Параметры
VPS_HOST="45.155.164.190"
VPS_USER="${1:-root}"
APP_DIR="/opt/smartflow"
JAR_NAME="smartflow-backend-1.0.0.jar"

echo "🚀 Деплой SmartFlow на VPS..."

# Сборка проекта
echo "📦 Сборка проекта..."
mvn clean package -DskipTests

# Копирование JAR на сервер
echo "📤 Копирование JAR на сервер..."
scp target/${JAR_NAME} ${VPS_USER}@${VPS_HOST}:${APP_DIR}/

# Копирование application.yml (если нужно обновить)
echo "📤 Копирование конфигурации..."
scp src/main/resources/application.yml ${VPS_USER}@${VPS_HOST}:${APP_DIR}/config/

# Подключение к серверу и перезапуск приложения
echo "🔄 Перезапуск приложения на сервере..."
ssh ${VPS_USER}@${VPS_HOST} << 'ENDSSH'
cd /opt/smartflow
sudo systemctl restart smartflow
sudo systemctl status smartflow
ENDSSH

echo "✅ Деплой завершен!"

