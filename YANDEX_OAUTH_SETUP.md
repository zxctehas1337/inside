# Настройка Yandex OAuth для ShakeDown

## Информация для настройки в Yandex OAuth

### 1. Создание приложения
Перейдите на https://oauth.yandex.ru/ и создайте новое приложение.

### 2. Redirect URI (Callback URL)
Добавьте следующие Redirect URI в настройках вашего приложения:

**Для продакшн:**
```
https://oneshakedown.onrender.com/api/auth/yandex/callback
```

**Для локальной разработки:**
```
http://localhost:8080/api/auth/yandex/callback
```

**Для лаунчера (локальный OAuth сервер):**
```
http://localhost:3000/callback
```

### 3. Suggest Hostname (Разрешенные домены)
Добавьте следующие домены в список разрешенных:

```
oneshakedown.onrender.com
localhost
```

### 4. Права доступа (Scopes)
Выберите следующие права доступа:
- ✅ **login:email** - Доступ к email адресу пользователя
- ✅ **login:info** - Доступ к информации профиля (имя, аватар)
- ✅ **login:avatar** - Доступ к аватару пользователя

### 5. Переменные окружения
Убедитесь, что в файле `.env` указаны следующие переменные:

```env
# Yandex OAuth Configuration
YANDEX_CLIENT_ID=6a99906594384864bde2d04cf2855ec2
YANDEX_CLIENT_SECRET=06941c5ff4ce4618b0e3550cb97455df
YANDEX_CALLBACK_URL=https://oneshakedown.onrender.com/api/auth/yandex/callback
```

## Использование

### Веб-версия
Пользователи могут войти через Yandex, нажав кнопку "Войти через Yandex" на странице `/auth`.

### Лаунчер
В лаунчере также доступна кнопка "Войти через Yandex", которая:
1. Запускает локальный OAuth сервер на порту 3000
2. Открывает браузер для авторизации
3. Получает данные пользователя через callback
4. Автоматически входит в лаунчер

## Endpoints

### Инициация авторизации
```
GET /api/auth/yandex?redirect=web|launcher
```

### Callback
```
GET /api/auth/yandex/callback
```

## База данных
Добавлены следующие колонки в таблицу `users`:
- `yandex_id` - Уникальный ID пользователя Yandex
- `yandex_avatar` - URL аватара из Yandex профиля

## Логика работы
1. Пользователь нажимает "Войти через Yandex"
2. Перенаправление на Yandex OAuth
3. После авторизации Yandex возвращает пользователя на callback URL
4. Сервер проверяет существование пользователя:
   - Если пользователь с таким `yandex_id` существует - обновляет данные
   - Если пользователь с таким email существует - привязывает Yandex ID
   - Если пользователь новый - создает новый аккаунт
5. Перенаправление на dashboard (веб) или локальный сервер (лаунчер)

## Тестирование
1. Запустите сервер: `npm start`
2. Откройте http://localhost:8080/auth
3. Нажмите "Войти через Yandex"
4. Авторизуйтесь через Yandex
5. Проверьте успешный вход в систему
