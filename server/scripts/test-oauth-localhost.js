/**
 * Тестовый скрипт для проверки OAuth flow с локальным сервером
 * Симулирует работу лаунчера111
 */

const http = require('http');
const { exec } = require('child_process');

const OAUTH_PORT = 3000;
const API_URL = 'https://insidenew.onrender.com';

console.log('🧪 Тест OAuth flow с локальным сервером\n');

// Создаем локальный сервер для приема callback
const server = http.createServer((req, res) => {
  const url = new URL(req.url, `http://localhost:${OAUTH_PORT}`);
  
  console.log(`Получен запрос: ${url.pathname}`);
  console.log(`Query параметры:`, url.searchParams.toString());

  if (url.pathname === '/callback') {
    const userData = url.searchParams.get('user');
    const token = url.searchParams.get('token');
    
    console.log('\n✅ OAuth callback получен!');
    console.log('📦 Данные:', { 
      hasUserData: !!userData, 
      hasToken: !!token,
      userDataLength: userData ? userData.length : 0
    });

    if (userData) {
      try {
        const user = JSON.parse(decodeURIComponent(userData));
        console.log('\n👤 Данные пользователя:');
        console.log('   ID:', user.id);
        console.log('   Username:', user.username);
        console.log('   Email:', user.email);
        console.log('   Subscription:', user.subscription);
        console.log('   Is Admin:', user.isAdmin);
      } catch (error) {
        console.error('❌ Ошибка парсинга данных:', error.message);
      }
    }

    // Отправляем успешный ответ
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    res.end(`
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="utf-8">
        <title>Тест успешен</title>
        <style>
          body {
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
            color: white;
          }
          .container {
            text-align: center;
            padding: 40px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 20px;
            backdrop-filter: blur(10px);
          }
          .checkmark {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.2);
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 20px;
            font-size: 48px;
          }
          h1 { margin: 0 0 10px 0; font-size: 28px; }
          p { margin: 0; opacity: 0.9; }
        </style>
      </head>
      <body>
        <div class="container">
          <div class="checkmark">✓</div>
          <h1>Тест успешен!</h1>
          <p>OAuth callback получен. Проверьте консоль.</p>
        </div>
        <script>
          setTimeout(() => window.close(), 3000);
        </script>
      </body>
      </html>
    `);

    // Останавливаем сервер
    setTimeout(() => {
      console.log('\n🛑 Остановка тестового сервера');
      server.close();
      process.exit(0);
    }, 1000);
  } else {
    res.writeHead(404, { 'Content-Type': 'text/plain' });
    res.end('Not Found');
  }
});

server.listen(OAUTH_PORT, 'localhost', () => {
  console.log(`✅ Тестовый OAuth сервер запущен на http://localhost:${OAUTH_PORT}`);
  
  // Формируем URL для OAuth
  const authUrl = `${API_URL}/api/auth/google?redirect=http://localhost:${OAUTH_PORT}/callback`;
  console.log(`\n🌐 OAuth URL: ${authUrl}`);
  console.log('\n📝 Инструкция:');
  console.log('   1. Откройте URL выше в браузере');
  console.log('   2. Войдите через Google');
  console.log('   3. После успешной авторизации вы будете перенаправлены обратно');
  console.log('   4. Проверьте консоль для результатов\n');
  
  // Автоматически открываем браузер (опционально)
  console.log('🚀 Открываем браузер...\n');
  const command = process.platform === 'win32' ? 'start' : 
                  process.platform === 'darwin' ? 'open' : 'xdg-open';
  exec(`${command} "${authUrl}"`);
});

server.on('error', (err) => {
  console.error('❌ Ошибка сервера:', err.message);
  if (err.code === 'EADDRINUSE') {
    console.error(`   Порт ${OAUTH_PORT} уже используется. Закройте другое приложение или измените порт.`);
  }
  process.exit(1);
});
