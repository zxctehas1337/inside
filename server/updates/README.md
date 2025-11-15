# 📦 Папка для обновлений лаунчера

Сюда загружайте файлы обновлений:

1. `latest.yml` - информация о последней версии
2. `ShakeDown-Launcher Setup X.X.X.exe` - установщик новой версии

## Как загрузить обновление:

1. Запустите `Launcher/simple-publish.bat`
2. Скопируйте файлы из `Launcher/dist/` сюда
3. Сделайте commit и push в репозиторий
4. Render автоматически обновит сервер

## Структура latest.yml:

```yaml
version: 9.2.5
files:
  - url: ShakeDown-Launcher Setup 9.2.5.exe
    sha512: [хеш]
    size: [размер в байтах]
path: ShakeDown-Launcher Setup 9.2.5.exe
sha512: [хеш]
releaseDate: 2024-11-15T12:00:00.000Z
```

## Доступ:

Файлы доступны по URL:
- https://oneshakedown.onrender.com/updates/latest.yml
- https://oneshakedown.onrender.com/updates/ShakeDown-Launcher%20Setup%209.2.5.exe
