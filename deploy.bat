@echo off
echo ========================================
echo   ShakeDown - JWT Deploy Script
echo ========================================
echo.

echo [1/3] Добавление файлов в Git...
git add .
if %errorlevel% neq 0 (
    echo ОШИБКА: Не удалось добавить файлы в Git
    pause
    exit /b 1
)

echo.
echo [2/3] Создание коммита...
git commit -m "Миграция на JWT токены для устойчивости к передеплою"
if %errorlevel% neq 0 (
    echo ВНИМАНИЕ: Нет изменений для коммита или ошибка
)

echo.
echo [3/3] Отправка на GitHub...
git push origin main
if %errorlevel% neq 0 (
    echo ОШИБКА: Не удалось отправить на GitHub
    pause
    exit /b 1
)

echo.
echo ========================================
echo   Деплой успешно запущен!
echo ========================================
echo.
echo Render.com автоматически начнет деплой.
echo Проверьте статус на: https://dashboard.render.com/
echo.
echo НЕ ЗАБУДЬТЕ добавить JWT_SECRET в Environment Variables!
echo Key: JWT_SECRET
echo Value: KOTAKBAS3991-JWT-SECRET-KEY-2024
echo.
pause
