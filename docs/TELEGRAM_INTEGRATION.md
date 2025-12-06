# Интеграция Telegram бота с SmartFlow

## 📋 Содержание

1. [Создание Telegram бота](#создание-телеграм-бота)
2. [Настройка n8n workflow](#настройка-n8n-workflow)
3. [Команды бота](#команды-бота)
4. [Тестирование](#тестирование)

## 🤖 Создание Telegram бота

### Шаг 1: Получение токена

1. Откройте Telegram и найдите [@BotFather](https://t.me/botfather)
2. Отправьте команду `/newbot`
3. Следуйте инструкциям и создайте бота
4. Сохраните полученный токен (например: `123456789:ABCdefGHIjklMNOpqrsTUVwxyz`)

### Шаг 2: Настройка переменных окружения

Добавьте токен в переменные окружения n8n:

```bash
# В n8n UI: Settings → Environment Variables
TELEGRAM_BOT_TOKEN=ваш_токен_бота
TELEGRAM_ADMIN_CHAT_ID=ваш_chat_id
```

### Шаг 3: Получение Chat ID

1. Напишите вашему боту любое сообщение
2. Откройте в браузере: `https://api.telegram.org/botВАШ_ТОКЕН/getUpdates`
3. Найдите `"chat":{"id":123456789}` - это ваш Chat ID

## 🔧 Настройка n8n workflow

### Импорт workflow

1. Откройте n8n: `http://45.155.164.190:5678`
2. Перейдите в **Workflows** → **Import from File**
3. Выберите файл `n8n/workflows/telegram-bot-workflow.json`
4. Настройте переменные окружения в workflow

### Настройка Webhook

1. В узле **Webhook** нажмите **Execute Node**
2. Скопируйте полученный Webhook URL
3. Настройте Telegram webhook:

```bash
curl -X POST "https://api.telegram.org/botВАШ_ТОКЕН/setWebhook" \
  -H "Content-Type: application/json" \
  -d '{"url": "ВАШ_WEBHOOK_URL"}'
```

## 📱 Команды бота

### `/mywave`

Получение персонализированных рекомендаций для пользователя.

**Использование:**
```
/mywave
```

**Ответ:**
```
🎵 Ваши рекомендации:
Song Title 1 - Artist 1
Song Title 2 - Artist 2
...
```

### `/like <trackId>`

Добавление трека в избранное.

**Использование:**
```
/like 123
```

**Ответ:**
```
✅ Трек добавлен в избранное!
```

### `/history`

Получение истории прослушиваний (в разработке).

**Использование:**
```
/history
```

## 🧪 Тестирование

### Локальное тестирование

1. Запустите SmartFlow backend:
```bash
mvn spring-boot:run
```

2. Запустите n8n:
```bash
docker-compose up n8n
```

3. Отправьте команду боту в Telegram

### Проверка логов

```bash
# Логи backend
tail -f logs/smartflow.log

# Логи n8n
docker logs smartflow-n8n -f
```

## 🔐 Безопасность

- **Не храните токены в репозитории**
- Используйте переменные окружения
- Ограничьте доступ к n8n через файрвол
- Используйте HTTPS для production

## 📊 Мониторинг

Проверьте работу эндпоинтов:

```bash
# Проверка здоровья API
curl http://localhost:8080/actuator/health

# Проверка рекомендаций
curl "http://localhost:8080/api/v1/telegram/mywave?userId=1"
```

## 🐛 Troubleshooting

### Бот не отвечает

1. Проверьте, что webhook настроен правильно
2. Проверьте логи n8n workflow
3. Убедитесь, что backend запущен и доступен

### Ошибки API

1. Проверьте логи backend: `logs/smartflow.log`
2. Проверьте, что база данных доступна
3. Убедитесь, что пользователь существует в БД

## 📚 Дополнительные ресурсы

- [Telegram Bot API](https://core.telegram.org/bots/api)
- [n8n Documentation](https://docs.n8n.io/)
- [SmartFlow API Documentation](../README.md)

