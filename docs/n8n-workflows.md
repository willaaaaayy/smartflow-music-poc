# n8n Workflows для SmartFlow

Документация по настройке и использованию n8n workflows для управления музыкальным сервисом SmartFlow.

## 📋 Содержание

1. [Установка n8n](#установка-n8n)
2. [Workflow: Telegram Bot Commands](#workflow-telegram-bot-commands)
3. [Workflow: Dynamic Config Update](#workflow-dynamic-config-update)
4. [Workflow: Recommendation Analytics](#workflow-recommendation-analytics)

---

## 🚀 Установка n8n

### Вариант 1: Docker (рекомендуется)

```bash
docker run -it --rm \
  --name n8n \
  -p 5678:5678 \
  -v ~/.n8n:/home/node/.n8n \
  n8nio/n8n
```

### Вариант 2: npm

```bash
npm install n8n -g
n8n start
```

После запуска n8n будет доступен по адресу: `http://your-vps-ip:5678`

---

## 📱 Workflow: Telegram Bot Commands

Этот workflow обрабатывает команды от пользователей Telegram бота.

### Узлы workflow:

1. **Telegram Trigger** - получает обновления от Telegram
   - Метод: `getUpdates` или Webhook
   - Токен бота: настройте в переменных окружения

2. **IF Node** - проверяет тип команды
   - Условия:
     - `/mywave` → переход к получению рекомендаций
     - `/like <trackId>` → переход к добавлению в избранное
     - `/history` → переход к получению истории

3. **HTTP Request** - вызов API SmartFlow
   - URL: `http://localhost:8080/api/v1/telegram/webhook`
   - Method: `POST`
   - Body (JSON):
     ```json
     {
       "command": "{{ $json.message.text }}",
       "userId": "{{ $json.message.from.id }}",
       "trackId": "{{ извлеченный из команды }}"
     }
     ```

4. **Telegram Send Message** - отправка ответа пользователю
   - Chat ID: `{{ $json.message.chat.id }}`
   - Text: `{{ ответ от API }}`

### Пример JSON для HTTP Request:

```json
{
  "command": "/mywave",
  "userId": 123456789
}
```

---

## ⚙️ Workflow: Dynamic Config Update

Этот workflow позволяет обновлять конфигурацию рекомендаций без перезапуска приложения.

### Узлы workflow:

1. **Webhook** - принимает запрос на обновление конфигурации
   - Метод: `POST`
   - Path: `/update-config`
   - Authentication: Basic Auth (опционально)

2. **Read Binary File** - чтение `recommendation_config.json`
   - File Path: `/opt/smartflow/recommendation_config.json`
   - Или используйте HTTP Request для получения текущей конфигурации

3. **Code Node** - модификация конфигурации
   ```javascript
   const config = JSON.parse($input.item.json.data);
   
   // Пример: увеличить вес рока
   config.genreBoost.rock = 1.5;
   
   return { json: config };
   ```

4. **HTTP Request** - обновление конфигурации через API
   - URL: `http://localhost:8080/api/v1/config/recommendations`
   - Method: `PUT`
   - Headers:
     - `Content-Type: application/json`
   - Body (JSON):
     ```json
     {
       "genreBoost": {
         "rock": 1.5,
         "pop": 1.0,
         "jazz": 0.9,
         "electronic": 1.2,
         "hiphop": 1.15
       },
       "recencyWeight": 1.5,
       "popularityWeight": 1.2,
       "randomFactor": 0.15,
       "fallbackLimit": 15
     }
     ```

5. **IF Node** - проверка успешности обновления
   - Условие: `{{ $json.status }} === "success"`

6. **Telegram Send Message** (опционально) - уведомление администратора
   - Chat ID: ваш Telegram ID
   - Text: "✅ Конфигурация обновлена успешно"

### Пример использования:

```bash
curl -X PUT http://localhost:5678/webhook/update-config \
  -H "Content-Type: application/json" \
  -d '{
    "genreBoost": {
      "rock": 1.5
    }
  }'
```

---

## 📊 Workflow: Recommendation Analytics

Этот workflow собирает аналитику по рекомендациям и отправляет отчеты.

### Узлы workflow:

1. **Cron** - запуск по расписанию (например, каждый день в 9:00)
   - Cron Expression: `0 9 * * *`

2. **HTTP Request** - получение метрик из Actuator
   - URL: `http://localhost:8080/actuator/metrics`
   - Method: `GET`

3. **Code Node** - обработка метрик
   ```javascript
   const metrics = $input.item.json;
   
   // Извлечение нужных метрик
   const recommendationsCount = metrics.measurements
     .find(m => m.statistic === 'COUNT' && m.metric === 'recommendations');
   
   return {
     json: {
       totalRecommendations: recommendationsCount.value,
       timestamp: new Date().toISOString()
     }
   };
   ```

4. **HTTP Request** - сохранение в базу данных (опционально)
   - URL: `http://localhost:8080/api/v1/analytics`
   - Method: `POST`

5. **Telegram Send Message** - отправка отчета
   - Chat ID: администратор
   - Text: "📊 Отчет по рекомендациям: {{ $json.totalRecommendations }}"

---

## 🔧 Настройка переменных окружения

В n8n создайте переменные окружения:

1. `SMARTFLOW_API_URL` = `http://localhost:8080`
2. `TELEGRAM_BOT_TOKEN` = ваш токен от BotFather
3. `ADMIN_TELEGRAM_ID` = ваш Telegram ID

---

## 📝 Примеры готовых workflows

### 1. Простой Telegram Bot

```json
{
  "name": "Telegram Bot Commands",
  "nodes": [
    {
      "parameters": {},
      "name": "Telegram Trigger",
      "type": "n8n-nodes-base.telegramTrigger",
      "typeVersion": 1,
      "position": [250, 300]
    },
    {
      "parameters": {
        "url": "={{ $env.SMARTFLOW_API_URL }}/api/v1/telegram/webhook",
        "method": "POST",
        "bodyParameters": {
          "parameters": [
            {
              "name": "command",
              "value": "={{ $json.message.text }}"
            },
            {
              "name": "userId",
              "value": "={{ $json.message.from.id }}"
            }
          ]
        }
      },
      "name": "Call SmartFlow API",
      "type": "n8n-nodes-base.httpRequest",
      "typeVersion": 1,
      "position": [450, 300]
    },
    {
      "parameters": {
        "chatId": "={{ $('Telegram Trigger').item.json.message.chat.id }}",
        "text": "={{ $json.text }}"
      },
      "name": "Send Response",
      "type": "n8n-nodes-base.telegram",
      "typeVersion": 1,
      "position": [650, 300]
    }
  ],
  "connections": {
    "Telegram Trigger": {
      "main": [[{"node": "Call SmartFlow API", "type": "main", "index": 0}]]
    },
    "Call SmartFlow API": {
      "main": [[{"node": "Send Response", "type": "main", "index": 0}]]
    }
  }
}
```

---

## 🔐 Безопасность

1. **Webhook Authentication**: Используйте Basic Auth или API ключи для webhook endpoints
2. **Telegram Token**: Храните токен в переменных окружения, не в workflow
3. **API Access**: Ограничьте доступ к API только с localhost или используйте firewall

---

## 📚 Дополнительные ресурсы

- [n8n Documentation](https://docs.n8n.io/)
- [Telegram Bot API](https://core.telegram.org/bots/api)
- [SmartFlow API Documentation](./api-documentation.md)

