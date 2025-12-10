#!/bin/bash

# Скрипт для локального запуска SmartFlow

echo "🚀 Запуск SmartFlow локально..."

# Проверка Java
if ! command -v java &> /dev/null; then
    echo "❌ Java не установлена. Установите Java 17 или выше."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1)
echo "✅ Java найдена: $JAVA_VERSION"

# Проверка Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven не установлен. Установите Maven."
    exit 1
fi

echo "✅ Maven найден: $(mvn -version | head -n 1)"

echo ""
echo "⚠️  ВАЖНО: Для правильной работы Lombok рекомендуется использовать IDE:"
echo "   - IntelliJ IDEA (автоматически обрабатывает Lombok)"
echo "   - VS Code с расширением 'Lombok Annotations Support'"
echo ""
echo "Продолжить сборку через Maven? (y/n)"
read -r response

if [[ ! "$response" =~ ^[Yy]$ ]]; then
    echo "Отменено. Используйте IDE для запуска."
    exit 0
fi

# Сборка проекта
echo "📦 Сборка проекта..."
echo "⚠️  Если сборка не удается из-за Lombok, используйте IDE для запуска"
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Ошибка при сборке проекта"
    echo "💡 Решение: Запустите проект через IDE (IntelliJ IDEA или VS Code)"
    echo "   - Откройте проект в IDE"
    echo "   - Запустите SmartFlowApplication с профилем 'local'"
    exit 1
fi

# Запуск с локальным профилем
echo ""
echo "🎵 Запуск приложения с профилем 'local'..."
echo "📍 Админ-панель: http://localhost:8080/admin"
echo "📍 H2 Console: http://localhost:8080/h2-console"
echo "📍 API: http://localhost:8080/api/v1"
echo ""
echo "Логин админ-панели: admin / admin"
echo "H2 Console: JDBC URL: jdbc:h2:mem:smartflow_db, User: sa, Password: (пусто)"
echo ""

java -jar -Dspring.profiles.active=local target/smartflow-backend-1.0.0.jar

