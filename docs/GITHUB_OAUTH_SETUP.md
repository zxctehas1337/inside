# GitHub OAuth Setup

## Настройка GitHub OAuth приложения

### 1. Создание OAuth приложения в GitHub

1. Перейдите в настройки GitHub: https://github.com/settings/developers
2. Нажмите "New OAuth App"
3. Заполните форму:
   - **Application name**: ShakeDown Client
   - **Homepage URL**: `https://oneshakedown.onrender.com`
   - **Authorization callback URL**: `https://oneshakedown.onrender.com/api/auth/github/callback`
4. Нажмите "Register application"
5. Скопируйте **Client ID** и **Client Secret**

### 2. Настройка переменных окружения

Добавьте в файл `.env`:

```env
GITHUB_CLIENT_ID=your_client_id_here
GITHUB_CLIENT_SECRET=your_client_secret_here
GITHUB_CALLBACK_URL=https://oneshakedown.onrender.com/api/auth/github/callback
```

### 3. Структура базы данных

При первом запуске сервера автоматически добавятся поля:
- `github_id` - уникальный ID пользователя GitHub
- `github_avatar` - URL аватара из GitHub

### 4. Использование

#### Веб-версия
Пользователи могут войти через кнопку "Войти через GitHub" на странице `/auth`

#### Лаунчер-версия
1. Пользователь нажимает "Войти через GitHub"
2. Запускается локальный OAuth сервер на порту 3000
3. Открывается браузер с GitHub OAuth
4. После авторизации данные передаются обратно в лаунчер
5. Локальный сервер автоматически останавливается

### 5. Безопасность

- Используется JWT токен для аутентификации
- Токены действительны 50 дней
- Все OAuth данные хранятся в защищенной PostgreSQL базе
- Применяется rate limiting для защиты от атак

### 6. Логика работы

1. **Новый пользователь**: создается аккаунт с email из GitHub
2. **Существующий email**: привязывается GitHub ID к существующему аккаунту
3. **Существующий GitHub ID**: выполняется вход в систему

### 7. Аватары

Приоритет аватаров:
1. Пользовательский аватар (custom_avatar)
2. GitHub аватар (github_avatar)
3. Google аватар (google_avatar)
4. Yandex аватар (yandex_avatar)

### 8. Тестирование

Для локального тестирования:
1. Создайте отдельное OAuth приложение для разработки
2. Используйте callback URL: `http://localhost:8080/api/auth/github/callback`
3. Обновите `.env` с локальными credentials

## Troubleshooting

### Ошибка "Email не предоставлен GitHub"
- Убедитесь, что в настройках GitHub OAuth приложения запрошен scope `user:email`
- Проверьте, что у пользователя есть публичный email в профиле GitHub

### Ошибка redirect_uri_mismatch
- Проверьте, что GITHUB_CALLBACK_URL в `.env` совпадает с настройками в GitHub OAuth App
- URL должен быть точным, включая протокол (https://)

### Лаунчер не получает данные
- Проверьте, что локальный OAuth сервер запустился (порт 3000)
- Убедитесь, что firewall не блокирует порт 3000
- Проверьте логи в консоли разработчика
