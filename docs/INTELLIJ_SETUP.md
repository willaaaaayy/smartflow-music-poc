# Настройка IntelliJ IDEA для SmartFlow

## Быстрая настройка

### 1. Установка плагина Lombok

1. Откройте **File → Settings** (или **IntelliJ IDEA → Preferences** на Mac)
2. Перейдите в **Plugins**
3. Найдите и установите плагин **Lombok**
4. Перезапустите IntelliJ IDEA

### 2. Включение Annotation Processing

1. **File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors**
2. Установите галочку **Enable annotation processing**
3. Нажмите **Apply** и **OK**

### 3. Настройка проекта

1. Откройте проект в IntelliJ IDEA
2. Дождитесь индексации проекта (в правом нижнем углу)
3. Если видите ошибки компиляции - нажмите **Build → Rebuild Project**

### 4. Запуск приложения

1. Откройте файл `src/main/java/com/smartflow/SmartFlowApplication.java`
2. Рядом с методом `main` нажмите зеленую стрелку или **Ctrl+Shift+F10** (Mac: **Cmd+Shift+R**)
3. В конфигурации запуска добавьте:
   - **VM options**: `-Dspring.profiles.active=local`
   - Или в **Program arguments**: `--spring.profiles.active=local`

### 5. Альтернативный способ (через Run Configuration)

1. **Run → Edit Configurations**
2. Нажмите **+** → **Application**
3. Настройте:
   - **Name**: SmartFlow
   - **Main class**: `com.smartflow.SmartFlowApplication`
   - **VM options**: `-Dspring.profiles.active=local`
   - **Working directory**: `$PROJECT_DIR$`
4. Нажмите **OK** и запустите

## Проверка работы

После запуска откройте в браузере:
- http://localhost:8080/admin (admin/admin)
- http://localhost:8080/h2-console
- http://localhost:8080/api/v1/wave/recommendations?userId=1&limit=10

## Решение проблем

### Ошибка "ClassNotFoundException"

1. **Build → Rebuild Project**
2. Проверьте, что Lombok plugin установлен
3. Убедитесь, что Annotation Processing включен

### Lombok не работает

1. Проверьте установку плагина Lombok
2. Перезапустите IntelliJ IDEA
3. **File → Invalidate Caches / Restart**

### Ошибки компиляции

1. Убедитесь, что используется Java 21
2. **File → Project Structure → Project SDK**: выберите Java 21
3. **File → Project Structure → Project Language Level**: выберите 21



