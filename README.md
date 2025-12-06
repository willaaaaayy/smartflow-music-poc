# 🎵 SmartFlow - Музыкальный сервис с AI-рекомендациями

Backend для музыкального сервиса SmartFlow с персонализированными рекомендациями, интеграцией с Telegram и low-code автоматизацией через n8n.

## 🚀 Особенности

- **Персонализированные рекомендации** на основе предпочтений пользователя
- **Полнотекстовый поиск** по трекам, исполнителям и альбомам
- **Динамическая конфигурация** через JSON без перекомпиляции
- **Интеграция с Telegram** для удобного доступа
- **n8n workflows** для автоматизации бизнес-процессов
- **Кэширование** через Redis для высокой производительности
- **Мониторинг** через Spring Boot Actuator

## 🏗️ Архитектура

```
┌─────────────┐      ┌──────────────┐      ┌─────────────┐
│   Telegram  │─────▶│     n8n      │─────▶│  SmartFlow  │
│     Bot     │      │  Workflows   │      │   Backend   │
└─────────────┘      └──────────────┘      └─────────────┘
                                              │         │
                                              ▼         ▼
                                        ┌─────────┐ ┌──────┐
                                        │PostgreSQL│ │Redis │
                                        └─────────┘ └──────┘
```

## 📋 Технологический стек

- **Java 17** - основной язык разработки
- **Spring Boot 3.2.0** - фреймворк
- **PostgreSQL** - основная база данных
- **Redis** - кэширование
- **n8n** - автоматизация workflow
- **Telegram Bot API** - интеграция с мессенджером

## 🔧 Быстрый старт

### Локальная разработка

1. **Клонирование репозитория**
```bash
git clone https://github.com/willaaaaayy/smartflow-music-poc.git
cd smartflow-music-poc
```

2. **Настройка базы данных**
```bash
# PostgreSQL
createdb smartflow_db
createuser smartflow_user

# Redis
redis-server
```

3. **Настройка application.yml**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/smartflow_db
    username: smartflow_user
    password: ваш_пароль
  data:
    redis:
      host: localhost
      port: 6379
```

4. **Запуск приложения**
```bash
./mvnw spring-boot:run
```

### Деплой на VPS

См. подробное руководство: [DEPLOYMENT.md](./docs/DEPLOYMENT.md)

## 📡 API Endpoints

### Wave API (Рекомендации)

```http
GET /api/v1/wave/recommendations?userId={userId}&limit={limit}
```

**Пример:**
```bash
curl "http://localhost:8080/api/v1/wave/recommendations?userId=1&limit=10"
```

### Search API

```http
GET /api/v1/search?query={query}&page={page}&size={size}
GET /api/v1/search/genre?genreId={genreId}&page={page}&size={size}
```

**Пример:**
```bash
curl "http://localhost:8080/api/v1/search?query=rock&page=0&size=20"
```

### Telegram API

```http
POST /api/v1/telegram/webhook
Content-Type: application/json

{
  "command": "/mywave",
  "userId": 1
}
```

### Config API

```http
GET /api/v1/config/recommendations
PUT /api/v1/config/recommendations
POST /api/v1/config/recommendations/reload
```

**Обновление конфигурации через API (для n8n):**
```bash
curl -X PUT http://localhost:8080/api/v1/config/recommendations \
  -H "Content-Type: application/json" \
  -d '{
    "genreBoost": {"rock": 1.5},
    "popularityWeight": 1.2,
    "randomFactor": 0.15,
    "fallbackLimit": 15
  }'
```

## 🤖 Telegram команды

- `/mywave` - получить персональные рекомендации
- `/like <trackId>` - добавить трек в избранное
- `/history` - показать избранные треки

## ⚙️ Конфигурация рекомендаций

Параметры алгоритма рекомендаций настраиваются через `recommendation_config.json`:

```json
{
  "genreBoost": {
    "rock": 1.3,
    "pop": 1.0,
    "jazz": 0.9
  },
  "popularityWeight": 1.2,
  "randomFactor": 0.15,
  "fallbackLimit": 15
}
```

Конфигурация может быть обновлена через n8n workflow без перезагрузки приложения.

## 🔄 n8n Integration

SmartFlow полностью интегрирован с n8n для low-code автоматизации:

### Доступные workflows:

1. **Telegram Bot Commands** - обработка команд от пользователей
2. **Dynamic Config Update** - обновление конфигурации без перезапуска
3. **Recommendation Analytics** - сбор аналитики и отчетов

### Быстрый старт:

1. Создайте Telegram бота через [@BotFather](https://t.me/BotFather)
2. Настройте n8n (см. [n8n-workflows.md](./docs/n8n-workflows.md))
3. Настройте Telegram бота (см. [telegram-setup.md](./docs/telegram-setup.md))

**Подробная документация:**
- [n8n Workflows](./docs/n8n-workflows.md) - настройка workflow
- [Telegram Setup](./docs/telegram-setup.md) - настройка бота

## 📊 Мониторинг

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Metrics

```bash
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/prometheus
```

## 🧪 Тестирование

```bash
# Запуск всех тестов
./mvnw test

# Запуск с покрытием
./mvnw test jacoco:report
```

## 📁 Структура проекта

```
smartflow-music-poc/
├── src/
│   ├── main/
│   │   ├── java/com/smartflow/
│   │   │   ├── controller/     # REST контроллеры
│   │   │   ├── service/        # Бизнес-логика
│   │   │   ├── repository/     # Доступ к данным
│   │   │   ├── model/          # JPA сущности
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   └── config/         # Конфигурация
│   │   └── resources/
│   │       ├── application.yml
│   │       └── recommendation_config.json
│   └── test/                   # Тесты
├── docs/                       # Документация
├── scripts/                    # Скрипты деплоя
└── pom.xml
```

## 🔐 Безопасность

- Пароли хранятся в переменных окружения
- SSL/TLS для всех соединений
- Валидация входных данных
- Защита от SQL injection через JPA

## 📝 Лицензия

MIT License

## 👥 Авторы

SmartFlow Team

## 🙏 Благодарности

- Spring Boot Community
- n8n Team
- Telegram Bot API

---

**Статус:** ✅ n8n и Telegram workflow интегрированы

