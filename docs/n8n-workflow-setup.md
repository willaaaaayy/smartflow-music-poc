# Настройка n8n Workflow для SmartFlow

Это руководство описывает настройку n8n workflow для интеграции с SmartFlow backend и Telegram ботом.

## 📋 Предварительные требования

1. n8n установлен и запущен на VPS (порт 5678)
2. Telegram бот создан через [@BotFather](https://t.me/BotFather)
3. SmartFlow backend запущен и доступен

## 🔧 Шаг 1: Создание Telegram бота

1. Откройте [@BotFather](https://t.me/BotFather) в Telegram
2. Отправьте команду `/newbot`
3. Следуйте инструкциям для создания бота
4. Сохраните полученный токен (например: `123456789:ABCdefGHIjklMNOpqrsTUVwxyz`)

## 🔧 Шаг 2: Настройка Webhook в n8n

### 2.1. Создание нового Workflow

1. Откройте n8n интерфейс: `http://45.155.164.190:5678`
2. Создайте новый workflow: **"SmartFlow Telegram Bot"**

### 2.2. Настройка Telegram Trigger

1. Добавьте узел **"Telegram Trigger"**
2. Настройте параметры:
   - **Credential**: Создайте новую credential с токеном бота
   - **Updates**: `message`
   - **Additional Fields**: Оставьте по умолчанию

3. Сохраните workflow и активируйте его

## 🔧 Шаг 3: Обработка команд

### 3.1. Узел "Switch" для маршрутизации команд

Добавьте узел **"Switch"** после Telegram Trigger:

- **Mode**: `Rules`
- **Rules**:
  - **Rule 1**: 
    - Value: `{{ $json.message.text }}`
    - Operation: `Starts With`
    - Value 2: `/mywave`
  - **Rule 2**:
    - Value: `{{ $json.message.text }}`
    - Operation: `Starts With`
    - Value 2: `/like`
  - **Rule 3**:
    - Value: `{{ $json.message.text }}`
    - Operation: `Starts With`
    - Value 2: `/history`

### 3.2. Обработка команды /mywave

**Узел 1: HTTP Request (Get Recommendations)**

- **Method**: `GET`
- **URL**: `http://localhost:8080/api/v1/wave/recommendations?userId={{ $json.message.from.id }}&limit=10`
- **Response Format**: `JSON`

**Узел 2: Code (Format Response)**

```javascript
const recommendations = $input.item.json.recommendations;
let message = "🎵 Ваша волна рекомендаций:\n\n";

if (recommendations && recommendations.length > 0) {
  recommendations.forEach((track, index) => {
    message += `${index + 1}. ${track.artist} - ${track.title}\n`;
  });
} else {
  message += "Пока нет рекомендаций. Добавьте любимые треки!";
}

return {
  chat_id: $('Telegram Trigger').item.json.message.chat.id,
  text: message
};
```

**Узел 3: Telegram (Send Message)**

- **Chat ID**: `{{ $json.chat_id }}`
- **Text**: `{{ $json.text }}`

### 3.3. Обработка команды /like

**Узел 1: Code (Parse Command)**

```javascript
const text = $('Telegram Trigger').item.json.message.text;
const parts = text.split(' ');
const trackId = parts.length > 1 ? parts[1] : null;

return {
  userId: $('Telegram Trigger').item.json.message.from.id,
  trackId: trackId,
  chatId: $('Telegram Trigger').item.json.message.chat.id
};
```

**Узел 2: HTTP Request (Like Track)**

- **Method**: `POST`
- **URL**: `http://localhost:8080/api/v1/telegram/webhook`
- **Body**:
```json
{
  "command": "/like",
  "userId": {{ $json.userId }},
  "trackId": {{ $json.trackId }}
}
```

**Узел 3: Telegram (Send Message)**

- **Chat ID**: `{{ $('Code').item.json.chatId }}`
- **Text**: `{{ $json.text }}`

### 3.4. Обработка команды /history

**Узел 1: HTTP Request (Get History)**

- **Method**: `POST`
- **URL**: `http://localhost:8080/api/v1/telegram/webhook`
- **Body**:
```json
{
  "command": "/history",
  "userId": {{ $('Telegram Trigger').item.json.message.from.id }}
}
```

**Узел 2: Telegram (Send Message)**

- **Chat ID**: `{{ $('Telegram Trigger').item.json.message.chat.id }}`
- **Text**: `{{ $json.text }}`

## 🔧 Шаг 4: Workflow для обновления конфигурации

Создайте отдельный workflow **"Update Recommendation Config"**:

### 4.1. Узел "Webhook" (Trigger)

- **HTTP Method**: `POST`
- **Path**: `update-config`
- **Response Mode**: `Response Node`

### 4.2. Узел "Read Binary File" (Read Config JSON)

- **File Path**: `/opt/smartflow/recommendation_config.json`
- Или используйте **HTTP Request** для чтения с сервера

### 4.3. Узел "HTTP Request" (Reload Config)

- **Method**: `POST`
- **URL**: `http://localhost:8080/api/v1/config/recommendations/reload`

### 4.4. Узел "Respond to Webhook"

- **Response Body**: `{{ $json }}`

## 🔧 Шаг 5: Workflow для мониторинга

Создайте workflow **"Monitor SmartFlow"**:

### 5.1. Узел "Schedule Trigger"

- **Trigger Times**: Каждый час
- **Timezone**: Ваш часовой пояс

### 5.2. Узел "HTTP Request" (Health Check)

- **Method**: `GET`
- **URL**: `http://localhost:8080/actuator/health`

### 5.3. Узел "IF" (Check Status)

- **Condition**: `{{ $json.status }}` не равно `UP`

### 5.4. Узел "Telegram" (Send Alert)

- Отправка уведомления администратору при проблемах

## 📊 Пример полного workflow

```
Telegram Trigger
    ↓
Switch (по команде)
    ├─ /mywave → HTTP Request → Code → Telegram Send
    ├─ /like → Code → HTTP Request → Telegram Send
    └─ /history → HTTP Request → Telegram Send
```

## 🔐 Безопасность

1. **Храните токены в n8n Credentials**, не в workflow
2. **Используйте переменные окружения** для URL и токенов
3. **Ограничьте доступ** к n8n интерфейсу через файрвол
4. **Используйте HTTPS** для production

## 🧪 Тестирование

1. Откройте Telegram бота
2. Отправьте команду `/mywave`
3. Проверьте ответ в Telegram
4. Проверьте логи в n8n

## 📝 Полезные команды для тестирования

- `/mywave` - получить рекомендации
- `/like 1` - добавить трек с ID=1 в избранное
- `/history` - показать избранное

## 🔗 Полезные ссылки

- [n8n Documentation](https://docs.n8n.io/)
- [Telegram Bot API](https://core.telegram.org/bots/api)
- [SmartFlow API Documentation](../README.md)

