# 🚀 Локальный запуск SmartFlow

## Быстрый старт

### Вариант 1: Через IDE (рекомендуется)

1. **Откройте проект в IntelliJ IDEA или VS Code**
   - IntelliJ IDEA автоматически обрабатывает Lombok
   - VS Code: установите расширение "Lombok Annotations Support"

2. **Настройте профиль запуска:**
   - Profile: `local`
   - Main class: `com.smartflow.SmartFlowApplication`

3. **Запустите приложение**

### Вариант 2: Через Maven (если Lombok настроен)

```bash
# Убедитесь, что annotation processing включен
mvn clean compile
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Вариант 3: Через готовый JAR (если уже собран)

```bash
java -jar -Dspring.profiles.active=local target/smartflow-backend-1.0.0.jar
```

## Доступ к приложению

После запуска:

- **Админ-панель**: http://localhost:8080/admin
  - Логин: `admin`
  - Пароль: `admin`

- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:smartflow_db`
  - User: `sa`
  - Password: (пусто)

- **API**: http://localhost:8080/api/v1
- **Actuator**: http://localhost:8080/actuator/health

## Настройка для локальной разработки

Профиль `local` использует:
- **H2** встроенную базу данных (в памяти)
- **Без Redis** (кэширование отключено)
- **Автоматическое создание схемы** при старте

## Решение проблем

### Lombok не работает

1. **IntelliJ IDEA:**
   - Settings → Build → Compiler → Annotation Processors → Enable annotation processing
   - Установите плагин Lombok

2. **VS Code:**
   - Установите расширение "Lombok Annotations Support"
   - Перезапустите IDE

3. **Maven:**
   - Убедитесь, что `maven-compiler-plugin` настроен правильно
   - Проверьте, что Lombok в dependencies

### Ошибки компиляции

Если видите ошибки типа "cannot find symbol: variable log":
- Убедитесь, что классы имеют аннотацию `@Slf4j`
- Проверьте, что Lombok обрабатывается компилятором

### База данных не создается

- Проверьте логи приложения
- Убедитесь, что используется профиль `local`
- Проверьте H2 Console для просмотра данных

## Следующие шаги

После успешного запуска:
1. Откройте админ-панель
2. Создайте тестовых пользователей через H2 Console или API
3. Добавьте тестовые треки и жанры
4. Протестируйте рекомендации

