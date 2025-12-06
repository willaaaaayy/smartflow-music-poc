# Настройка Telegram бота для SmartFlow

Пошаговая инструкция по созданию и настройке Telegram бота для управления рекомендациями.

## 🤖 Шаг 1: Создание бота через BotFather

1. Откройте Telegram и найдите [@BotFather](https://t.me/botfather)

2. Отправьте команду `/newbot`

3. Следуйте инструкциям:
   - Введите имя бота (например: `SmartFlow Music Bot`)
   - Введите username бота (должен заканчиваться на `bot`, например: `smartflow_music_bot`)

4. BotFather выдаст вам **токен бота** (выглядит как: `123456789:ABCdefGHIjklMNOpqrsTUVwxyz`)

5. Сохраните токен в безопасном месте!

## ⚙️ Шаг 2: Настройка в application.yml

Добавьте настройки Telegram бота в `application.yml`:

```yaml
telegram:
  bot:
    token: ВАШ_ТОКЕН_ОТ_BOTFATHER
    enabled: true
```

Или используйте переменные окружения:

```yaml
telegram:
  bot:
    token: ${TELEGRAM_BOT_TOKEN:}
    enabled: ${TELEGRAM_BOT_ENABLED:false}
```

## 🔗 Шаг 3: Настройка Webhook (опционально)

Если хотите использовать webhook вместо polling:

```bash
curl -X POST "https://api.telegram.org/bot<ВАШ_ТОКЕН>/setWebhook" \
  -H "Content-Type: application/json" \
  -d '{"url": "https://williiiiiss.com/api/v1/telegram/webhook"}'
```

## 📱 Шаг 4: Доступные команды

После настройки бот поддерживает следующие команды:

### `/mywave`
Получить персональные рекомендации

**Пример:**
```
/mywave
```

**Ответ:**
```
🎵 Ваша волна рекомендаций:

1. The Beatles - Hey Jude
2. Pink Floyd - Comfortably Numb
3. Led Zeppelin - Stairway to Heaven
...
```

### `/like <trackId>`
Добавить трек в избранное

**Пример:**
```
/like 123
```

**Ответ:**
```
✅ Добавлено в избранное: The Beatles - Hey Jude
```

### `/history`
Показать историю избранных треков

**Пример:**
```
/history
```

**Ответ:**
```
📜 Ваше избранное:

1. The Beatles - Hey Jude
2. Pink Floyd - Comfortably Numb
3. Led Zeppelin - Stairway to Heaven
```

## 🔧 Шаг 5: Интеграция с n8n

1. В n8n создайте новый workflow

2. Добавьте узел **Telegram Trigger**

3. Настройте узел:
   - **Credential**: создайте новую credential с вашим токеном
   - **Updates**: выберите `getUpdates` или `Webhook`

4. Добавьте узел **HTTP Request** для вызова API SmartFlow:
   - URL: `http://localhost:8080/api/v1/telegram/webhook`
   - Method: `POST`
   - Body:
     ```json
     {
       "command": "{{ $json.message.text }}",
       "userId": "{{ $json.message.from.id }}"
     }
     ```

5. Добавьте узел **Telegram** для отправки ответа:
   - Action: `Send Message`
   - Chat ID: `{{ $json.message.chat.id }}`
   - Text: `{{ $('HTTP Request').item.json.text }}`

## 🧪 Шаг 6: Тестирование

1. Найдите вашего бота в Telegram (по username)

2. Отправьте команду `/start`

3. Попробуйте команды:
   - `/mywave`
   - `/like 1`
   - `/history`

## 📝 Примечания

- Убедитесь, что пользователь с указанным `userId` существует в базе данных
- Для команды `/like` нужен валидный `trackId` из базы данных
- Бот работает только если `telegram.bot.enabled=true` в конфигурации

## 🔐 Безопасность

- **Никогда не коммитьте токен бота в Git!**
- Используйте переменные окружения или секреты
- Ограничьте доступ к webhook endpoint через firewall

## 🐛 Решение проблем

### Бот не отвечает
- Проверьте, что `telegram.bot.enabled=true`
- Убедитесь, что токен правильный
- Проверьте логи приложения: `sudo journalctl -u smartflow -f`

### Ошибка "User not found"
- Создайте пользователя в базе данных:
  ```sql
  INSERT INTO users (username, email, display_name) 
  VALUES ('telegram_user', 'user@example.com', 'Telegram User');
  ```

### Webhook не работает
- Проверьте, что URL доступен из интернета
- Убедитесь, что используется HTTPS
- Проверьте логи n8n

