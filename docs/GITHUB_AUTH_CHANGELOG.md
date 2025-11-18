# GitHub OAuth - Changelog

## Дата: 18 ноября 2025

### Добавлено

#### Backend (server/index.js)
- ✅ Установлен пакет `passport-github2`
- ✅ Добавлена GitHub OAuth Strategy с поддержкой:
  - Автоматическое создание нового пользователя
  - Привязка GitHub к существующему аккаунту по email
  - Обновление GitHub аватара при каждом входе
- ✅ Добавлены endpoints:
  - `GET /api/auth/github` - инициация OAuth
  - `GET /api/auth/github/callback` - обработка callback
- ✅ Поддержка redirect параметра для веб/лаунчер версий
- ✅ Генерация JWT токена при успешной авторизации
- ✅ Обновлена инициализация БД для добавления полей:
  - `github_id` (VARCHAR 255, UNIQUE)
  - `github_avatar` (TEXT)

#### Frontend - Веб версия (src/pages/AuthPage.tsx)
- ✅ Добавлена кнопка "Войти через GitHub"
- ✅ Стили для кнопки GitHub (src/styles/AuthPage.css)
- ✅ Автоматическая обработка OAuth callback
- ✅ Сохранение JWT токена в localStorage

#### Frontend - Лаунчер (Launcher/src/pages/AuthPage.tsx)
- ✅ Добавлена кнопка "Войти через GitHub"
- ✅ Функция `handleGithubLogin()` для запуска OAuth flow
- ✅ Стили для кнопки GitHub (Launcher/src/styles/AuthPage.css)
- ✅ Интеграция с локальным OAuth сервером (порт 3000)

#### Конфигурация
- ✅ Обновлен `.env` с добавлением:
  - `GITHUB_CLIENT_ID`
  - `GITHUB_CLIENT_SECRET`
  - `GITHUB_CALLBACK_URL`

#### Документация
- ✅ Создан `docs/GITHUB_OAUTH_SETUP.md` - полная инструкция по настройке
- ✅ Создан `test-github-auth.http` - тестовые запросы для REST Client

### Технические детали

#### Логика работы
1. Пользователь нажимает "Войти через GitHub"
2. Перенаправление на GitHub OAuth с scope `user:email`
3. GitHub возвращает профиль с email и аватаром
4. Сервер проверяет:
   - Есть ли пользователь с таким `github_id`
   - Есть ли пользователь с таким `email`
   - Создает нового пользователя, если не найден
5. Генерируется JWT токен (действителен 50 дней)
6. Пользователь перенаправляется:
   - Веб: `/dashboard?auth=success&user=...`
   - Лаунчер: `http://localhost:3000/callback?user=...`

#### Приоритет аватаров
```
custom_avatar > github_avatar > google_avatar > yandex_avatar > null
```

#### Безопасность
- JWT токены с истечением через 50 дней
- Rate limiting на всех endpoints
- Helmet для защиты HTTP заголовков
- Валидация всех входных данных
- CORS настроен только для доверенных доменов

### Совместимость

- ✅ Работает с существующими Google OAuth пользователями
- ✅ Работает с существующими Yandex OAuth пользователями
- ✅ Поддерживает привязку нескольких OAuth провайдеров к одному аккаунту
- ✅ Обратная совместимость с существующей БД

### Тестирование

Для тестирования используйте:
1. Веб-версия: https://oneshakedown.onrender.com/auth
2. Лаунчер: запустите приложение и нажмите "Войти через GitHub"
3. REST Client: откройте `test-github-auth.http` в VS Code

### Следующие шаги

Для продакшн использования:
1. ✅ Убедитесь, что GitHub OAuth App настроен правильно
2. ✅ Проверьте, что callback URL совпадает с `.env`
3. ✅ Протестируйте на staging окружении
4. ✅ Проверьте логи сервера при первом входе
5. ✅ Убедитесь, что БД обновилась с новыми полями

### Известные ограничения

- GitHub должен предоставить email (публичный или приватный)
- Требуется scope `user:email` в OAuth приложении
- Локальный OAuth сервер лаунчера использует порт 3000 (должен быть свободен)
