const express = require('express');
const cors = require('cors');
const path = require('path');
const { Pool } = require('pg');
const passport = require('passport');
const GoogleStrategy = require('passport-google-oauth20').Strategy;
const session = require('express-session');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 8080;

// Подключение к PostgreSQL
const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

// Middleware - настройка CORS для продакшн
app.use(cors({
  origin: ['https://insidenew.onrender.com', 'http://localhost:5173', 'http://localhost:8080'],
  credentials: true,
  methods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));
app.use(express.json());

// Session middleware для Passport
app.use(session({
  secret: process.env.SESSION_SECRET || 'your-secret-key',
  resave: false,
  saveUninitialized: false,
  cookie: {
    secure: process.env.NODE_ENV === 'production',
    maxAge: 24 * 60 * 60 * 1000 // 24 часа
  }
}));

// Инициализация Passport
app.use(passport.initialize());
app.use(passport.session());

// Хранилище для кодов авторизации (в продакшене использовать Redis)
const authCodes = new Map(); // { code: { user, expiresAt } }

// Очистка истекших кодов каждые 5 минут
setInterval(() => {
  const now = Date.now();
  for (const [code, data] of authCodes.entries()) {
    if (data.expiresAt < now) {
      authCodes.delete(code);
      console.log(`🗑️  Удален истекший код: ${code}`);
    }
  }
}, 5 * 60 * 1000);

// Сериализация пользователя для сессии
passport.serializeUser((user, done) => {
  done(null, user.id);
});

passport.deserializeUser(async (id, done) => {
  try {
    const result = await pool.query('SELECT * FROM users WHERE id = $1', [id]);
    done(null, result.rows[0]);
  } catch (error) {
    done(error, null);
  }
});

// Google OAuth Strategy
passport.use(new GoogleStrategy({
    clientID: process.env.GOOGLE_CLIENT_ID,
    clientSecret: process.env.GOOGLE_CLIENT_SECRET,
    callbackURL: process.env.GOOGLE_CALLBACK_URL
  },
  async (accessToken, refreshToken, profile, done) => {
    try {
      // Проверяем, существует ли пользователь с таким Google ID
      let result = await pool.query(
        'SELECT * FROM users WHERE google_id = $1',
        [profile.id]
      );

      if (result.rows.length > 0) {
        // Пользователь существует
        return done(null, result.rows[0]);
      }

      // Проверяем, существует ли пользователь с таким email
      result = await pool.query(
        'SELECT * FROM users WHERE email = $1',
        [profile.emails[0].value]
      );

      if (result.rows.length > 0) {
        // Обновляем существующего пользователя, добавляя Google ID
        const updateResult = await pool.query(
          'UPDATE users SET google_id = $1, email_verified = true WHERE id = $2 RETURNING *',
          [profile.id, result.rows[0].id]
        );
        return done(null, updateResult.rows[0]);
      }

      // Создаем нового пользователя
      const username = profile.emails[0].value.split('@')[0] + '_' + Math.floor(Math.random() * 1000);
      const newUserResult = await pool.query(
        `INSERT INTO users (username, email, password, google_id, email_verified, subscription) 
         VALUES ($1, $2, $3, $4, true, 'free') 
         RETURNING *`,
        [username, profile.emails[0].value, '', profile.id]
      );

      return done(null, newUserResult.rows[0]);
    } catch (error) {
      return done(error, null);
    }
  }
));

// Serve static files from the dist directory
app.use(express.static(path.join(__dirname, '../dist')));

// Инициализация таблицы users и news
async function initDatabase() {
  try {
    await pool.query(`
      CREATE TABLE IF NOT EXISTS users (
        id SERIAL PRIMARY KEY,
        username VARCHAR(255) UNIQUE NOT NULL,
        email VARCHAR(255) UNIQUE NOT NULL,
        password TEXT,
        google_id VARCHAR(255) UNIQUE,
        subscription VARCHAR(50) DEFAULT 'free',
        registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        is_admin BOOLEAN DEFAULT false,
        is_banned BOOLEAN DEFAULT false,
        email_verified BOOLEAN DEFAULT false,
        settings JSONB DEFAULT '{"notifications": true, "autoUpdate": true, "theme": "dark", "language": "ru"}'::jsonb
      )
    `);
    
    // Добавляем колонку google_id для существующих таблиц
    await pool.query(`
      ALTER TABLE users 
      ADD COLUMN IF NOT EXISTS google_id VARCHAR(255) UNIQUE,
      ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT false
    `);
    
    // Создаем таблицу новостей
    await pool.query(`
      CREATE TABLE IF NOT EXISTS news (
        id SERIAL PRIMARY KEY,
        title VARCHAR(500) NOT NULL,
        content TEXT NOT NULL,
        author VARCHAR(255) NOT NULL,
        type VARCHAR(50) DEFAULT 'website',
        date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    `);
    
    console.log('✅ База данных инициализирована');
    
    // Автоматическое создание администратора
    await createDefaultAdmin();
  } catch (error) {
    console.error('❌ Ошибка инициализации БД:', error);
  }
}

// Автоматическое создание администратора при запуске
async function createDefaultAdmin() {
  try {
    const adminEmail = process.env.ADMIN_EMAIL || 'admin@inside.com';
    const adminPassword = process.env.ADMIN_PASSWORD || 'INSIDE-PROJECT-EASY';
    const adminUsername = process.env.ADMIN_USERNAME || 'admin';

    // Проверяем, существует ли уже администратор
    const checkResult = await pool.query(
      'SELECT * FROM users WHERE email = $1',
      [adminEmail]
    );

    if (checkResult.rows.length > 0) {
      // Обновляем существующего пользователя, делая его администратором
      await pool.query(
        'UPDATE users SET is_admin = true, password = $1, email_verified = true WHERE email = $2',
        [adminPassword, adminEmail]
      );
      console.log('✅ Администратор обновлен:', adminEmail);
    } else {
      // Создаем нового администратора
      await pool.query(
        `INSERT INTO users (username, email, password, is_admin, email_verified, subscription) 
         VALUES ($1, $2, $3, true, true, 'premium')`,
        [adminUsername, adminEmail, adminPassword]
      );
      console.log('✅ Администратор создан:', adminEmail);
    }

    console.log('📋 Данные для входа администратора:');
    console.log(`   Email: ${adminEmail}`);
    console.log(`   Password: ${adminPassword}`);
  } catch (error) {
    console.error('❌ Ошибка создания администратора:', error);
  }
}

initDatabase();

// Health check
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok' });
});

// Тестовый endpoint для проверки кодов авторизации
app.get('/api/auth/debug-codes', (req, res) => {
  const codes = Array.from(authCodes.entries()).map(([code, data]) => ({
    code,
    email: data.user.email,
    expiresAt: new Date(data.expiresAt).toISOString(),
    timeLeft: Math.max(0, Math.floor((data.expiresAt - Date.now()) / 1000))
  }));
  
  res.json({ 
    success: true, 
    activeCodes: codes.length,
    codes: codes
  });
});

// ============= GOOGLE OAUTH ENDPOINTS =============

// Инициация Google OAuth
app.get('/api/auth/google', (req, res, next) => {
  // Передаем redirect параметр через state для надежности
  const redirectUrl = req.query.redirect || 'web';
  console.log(`🔗 Redirect URL: ${redirectUrl}`);
  
  passport.authenticate('google', { 
    scope: ['profile', 'email'],
    state: redirectUrl
  })(req, res, next);
});

// Google OAuth callback
app.get('/api/auth/google/callback',
  passport.authenticate('google', { failureRedirect: '/auth' }),
  (req, res) => {
    console.log(`✅ Google OAuth успешен для пользователя: ${req.user.email}`);
    console.log(`🔍 Все query параметры:`, req.query);
    
    // Получаем redirect URL из state параметра
    let redirectUrl = req.query.state || 'web';
    
    // Резервная проверка: если state не передался, но в User-Agent есть признаки лаунчера
    if (redirectUrl === 'web' && req.headers['user-agent']) {
      const userAgent = req.headers['user-agent'].toLowerCase();
      if (userAgent.includes('electron') || userAgent.includes('launcher')) {
        redirectUrl = 'launcher';
        console.log(`🔄 Обнаружен лаунчер по User-Agent, переключаем на launcher режим`);
      }
    }
    
    console.log(`📋 Финальный redirect URL: ${redirectUrl}`);
    
    // Успешная аутентификация
    const user = {
      id: req.user.id,
      username: req.user.username,
      email: req.user.email,
      subscription: req.user.subscription,
      registeredAt: req.user.registered_at,
      isAdmin: req.user.is_admin,
      isBanned: req.user.is_banned,
      settings: req.user.settings
    };
    
    if (redirectUrl === 'launcher') {
      // Для лаунчера - генерируем код авторизации
      const code = Math.random().toString(36).substring(2, 8).toUpperCase();
      const expiresAt = Date.now() + 5 * 60 * 1000; // 5 минут
      
      authCodes.set(code, { user, expiresAt });
      console.log(`🔑 Сгенерирован код авторизации для лаунчера: ${code} (истекает через 5 минут)`);
      console.log(`👤 Пользователь: ${user.email} (ID: ${user.id})`);
      
      // Показываем страницу с кодом для лаунчера
      console.log(`📄 Отображаем страницу с кодом для лаунчера`);
      res.send(`
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="utf-8">
          <title>Inside Launcher</title>
          <style>
            * {
              margin: 0;
              padding: 0;
              box-sizing: border-box;
            }
            body {
              font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
              background: #0a0a0f;
              display: flex;
              align-items: center;
              justify-content: center;
              min-height: 100vh;
              color: white;
              position: relative;
              overflow: hidden;
            }
            body::before {
              content: '';
              position: absolute;
              inset: 0;
              background: radial-gradient(circle at 50% 50%, rgba(138, 75, 255, 0.1) 0%, transparent 60%);
              animation: pulse 4s ease-in-out infinite;
            }
            @keyframes pulse {
              0%, 100% { opacity: 0.4; }
              50% { opacity: 0.8; }
            }
            .container {
              position: relative;
              z-index: 1;
              text-align: center;
              max-width: 400px;
              width: 100%;
              padding: 20px;
            }
            .logo {
              margin-bottom: 32px;
              animation: float 3s ease-in-out infinite;
            }
            @keyframes float {
              0%, 100% { transform: translateY(0px); }
              50% { transform: translateY(-10px); }
            }
            h1 {
              font-size: 32px;
              font-weight: 700;
              background: linear-gradient(135deg, #8A4BFF 0%, #FF6B9D 100%);
              -webkit-background-clip: text;
              -webkit-text-fill-color: transparent;
              background-clip: text;
              margin-bottom: 48px;
              letter-spacing: -1px;
            }
            .code {
              font-size: 56px;
              font-weight: 700;
              letter-spacing: 12px;
              font-family: 'Courier New', monospace;
              background: linear-gradient(135deg, #8A4BFF 0%, #FF6B9D 100%);
              -webkit-background-clip: text;
              -webkit-text-fill-color: transparent;
              background-clip: text;
              user-select: all;
              cursor: pointer;
              margin-bottom: 32px;
              transition: transform 0.2s;
            }
            .code:hover {
              transform: scale(1.05);
            }
            .hint {
              font-size: 14px;
              color: rgba(255, 255, 255, 0.5);
              margin-bottom: 24px;
            }
            .timer {
              font-size: 13px;
              color: rgba(255, 255, 255, 0.4);
            }
            .copied {
              position: fixed;
              top: 24px;
              left: 50%;
              transform: translateX(-50%);
              background: rgba(138, 75, 255, 0.2);
              border: 1px solid rgba(138, 75, 255, 0.4);
              padding: 12px 24px;
              border-radius: 8px;
              font-size: 14px;
              font-weight: 500;
              animation: slideDown 0.3s ease-out;
              display: none;
            }
            @keyframes slideDown {
              from {
                opacity: 0;
                transform: translateX(-50%) translateY(-20px);
              }
              to {
                opacity: 1;
                transform: translateX(-50%) translateY(0);
              }
            }
          </style>
        </head>
        <body>
          <div class="copied" id="copiedMsg">Код скопирован</div>
          <div class="container">
            <div class="logo">
              <svg width="64" height="64" viewBox="0 0 24 24" fill="none">
                <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="url(#gradient1)"/>
                <path d="M2 17L12 22L22 17V7L12 12L2 7V17Z" fill="url(#gradient2)"/>
                <defs>
                  <linearGradient id="gradient1" x1="2" y1="2" x2="22" y2="12">
                    <stop offset="0%" stop-color="#8A4BFF"/>
                    <stop offset="100%" stop-color="#FF6B9D"/>
                  </linearGradient>
                  <linearGradient id="gradient2" x1="2" y1="7" x2="22" y2="22">
                    <stop offset="0%" stop-color="#6C37D7"/>
                    <stop offset="100%" stop-color="#8A4BFF"/>
                  </linearGradient>
                </defs>
              </svg>
            </div>
            <h1>Inside Client</h1>
            <div class="code" id="authCode" onclick="copyCode()">${code}</div>
            <div class="hint">Вставьте код в лаунчер</div>
            <div class="timer">Действителен <span id="timeLeft">5:00</span></div>
          </div>

          <script>
            function copyCode() {
              const code = document.getElementById('authCode').textContent;
              navigator.clipboard.writeText(code).then(() => {
                const msg = document.getElementById('copiedMsg');
                msg.style.display = 'block';
                setTimeout(() => {
                  msg.style.display = 'none';
                }, 2000);
              });
            }

            let timeLeft = 5 * 60;
            const timerElement = document.getElementById('timeLeft');
            
            setInterval(() => {
              timeLeft--;
              if (timeLeft <= 0) {
                timerElement.textContent = 'Истек';
                return;
              }
              const minutes = Math.floor(timeLeft / 60);
              const seconds = timeLeft % 60;
              timerElement.textContent = minutes + ':' + (seconds < 10 ? '0' : '') + seconds;
            }, 1000);

            window.onload = () => copyCode();
          </script>
        </body>
        </html>
      `);
    } else {
      // Для веба - перенаправляем на дашборд с данными пользователя
      const userData = encodeURIComponent(JSON.stringify(user));
      console.log(`🌐 Перенаправление на веб-дашборд для пользователя: ${user.email}`);
      res.redirect(`/dashboard?auth=success&user=${userData}`);
    }
  }
);

// Вход администратора
app.post('/api/auth/admin', async (req, res) => {
  const { email, password } = req.body;

  try {
    const result = await pool.query(
      'SELECT id, username, email, subscription, registered_at, is_admin, is_banned, settings FROM users WHERE email = $1 AND is_admin = true',
      [email]
    );

    if (result.rows.length === 0) {
      return res.json({ success: false, message: 'Администратор не найден' });
    }

    const dbUser = result.rows[0];

    // Проверяем пароль (если он установлен)
    const passwordResult = await pool.query(
      'SELECT password FROM users WHERE id = $1',
      [dbUser.id]
    );

    if (passwordResult.rows[0].password && passwordResult.rows[0].password !== password) {
      return res.json({ success: false, message: 'Неверный пароль' });
    }

    const user = {
      id: dbUser.id,
      username: dbUser.username,
      email: dbUser.email,
      subscription: dbUser.subscription,
      registeredAt: dbUser.registered_at,
      isAdmin: dbUser.is_admin,
      isBanned: dbUser.is_banned,
      settings: dbUser.settings
    };

    res.json({ success: true, data: user });
  } catch (error) {
    console.error('Admin login error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Проверка кода авторизации
app.post('/api/auth/verify-code', async (req, res) => {
  const { code } = req.body;

  if (!code) {
    return res.json({ success: false, message: 'Код не указан' });
  }

  const authData = authCodes.get(code.toUpperCase());

  if (!authData) {
    console.log(`❌ Неверный код: ${code}`);
    return res.json({ success: false, message: 'Неверный код авторизации' });
  }

  if (authData.expiresAt < Date.now()) {
    authCodes.delete(code.toUpperCase());
    console.log(`⏱️  Истекший код: ${code}`);
    return res.json({ success: false, message: 'Код авторизации истек' });
  }

  // Удаляем использованный код
  authCodes.delete(code.toUpperCase());
  console.log(`✅ Код успешно использован: ${code} для пользователя ${authData.user.email}`);

  res.json({ success: true, data: authData.user });
});

// Выход из системы
app.get('/api/auth/logout', (req, res) => {
  req.logout((err) => {
    if (err) {
      return res.json({ success: false, message: 'Ошибка при выходе' });
    }
    res.json({ success: true, message: 'Выход выполнен' });
  });
});

// Обновление пользователя
app.patch('/api/users/:id', async (req, res) => {
  const { id } = req.params;
  const userId = parseInt(id, 10);
  const updates = req.body;

  try {
    const fields = [];
    const values = [];
    let paramCount = 1;

    Object.keys(updates).forEach(key => {
      const dbKey = key.replace(/([A-Z])/g, '_$1').toLowerCase();
      if (dbKey === 'settings') {
        fields.push(`${dbKey} = $${paramCount}`);
        values.push(JSON.stringify(updates[key]));
      } else {
        fields.push(`${dbKey} = $${paramCount}`);
        values.push(updates[key]);
      }
      paramCount++;
    });

    values.push(userId);

    const result = await pool.query(
      `UPDATE users SET ${fields.join(', ')} 
       WHERE id = $${paramCount} 
       RETURNING id, username, email, password, subscription, registered_at, is_admin, is_banned, settings`,
      values
    );

    if (result.rows.length === 0) {
      return res.json({ success: false, message: 'Пользователь не найден' });
    }

    const dbUser = result.rows[0];
    const user = {
      id: dbUser.id,
      username: dbUser.username,
      email: dbUser.email,
      password: dbUser.password,
      subscription: dbUser.subscription,
      registeredAt: dbUser.registered_at,
      isAdmin: dbUser.is_admin,
      isBanned: dbUser.is_banned,
      settings: dbUser.settings
    };

    res.json({ success: true, data: user });
  } catch (error) {
    console.error('Update error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Получение информации о пользователе
app.get('/api/users/:id', async (req, res) => {
  const { id } = req.params;
  const userId = parseInt(id, 10);

  try {
    const result = await pool.query(
      `SELECT id, username, email, subscription, registered_at, is_admin, is_banned, settings 
       FROM users WHERE id = $1`,
      [userId]
    );

    if (result.rows.length === 0) {
      return res.json({ success: false, message: 'Пользователь не найден' });
    }

    const dbUser = result.rows[0];
    
    const user = {
      id: dbUser.id,
      username: dbUser.username,
      email: dbUser.email,
      subscription: dbUser.subscription,
      registeredAt: dbUser.registered_at,
      isAdmin: dbUser.is_admin,
      isBanned: dbUser.is_banned,
      settings: dbUser.settings
    };

    res.json({ success: true, data: user });
  } catch (error) {
    console.error('❌ Get user error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Получение всех пользователей (для админки)
app.get('/api/users', async (req, res) => {
  try {
    const result = await pool.query(
      `SELECT id, username, email, subscription, registered_at, is_admin, is_banned, settings 
       FROM users ORDER BY id DESC`
    );

    const users = result.rows.map(dbUser => ({
      id: dbUser.id,
      username: dbUser.username,
      email: dbUser.email,
      subscription: dbUser.subscription,
      registeredAt: dbUser.registered_at,
      isAdmin: dbUser.is_admin,
      isBanned: dbUser.is_banned,
      settings: dbUser.settings
    }));

    res.json({ success: true, data: users });
  } catch (error) {
    console.error('Get users error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Удаление пользователя
app.delete('/api/users/:id', async (req, res) => {
  const { id } = req.params;
  const userId = parseInt(id, 10);

  try {
    // Проверяем существование пользователя
    const checkUser = await pool.query(
      'SELECT id, username, email, google_id FROM users WHERE id = $1',
      [userId]
    );

    if (checkUser.rows.length === 0) {
      return res.json({ success: false, message: 'Пользователь не найден' });
    }

    const user = checkUser.rows[0];
    console.log(`🗑️  Удаление пользователя: ID=${userId}, Username=${user.username}, Email=${user.email}, Google ID=${user.google_id || 'нет'}`);

    // Удаляем пользователя (независимо от того, через Google он зарегистрирован или нет)
    const result = await pool.query(
      'DELETE FROM users WHERE id = $1 RETURNING id, username, email',
      [userId]
    );

    if (result.rows.length === 0) {
      return res.json({ success: false, message: 'Не удалось удалить пользователя' });
    }

    console.log(`✅ Пользователь успешно удален: ${result.rows[0].username}`);

    res.json({ 
      success: true, 
      message: 'Пользователь удален', 
      username: result.rows[0].username,
      email: result.rows[0].email 
    });
  } catch (error) {
    console.error('❌ Delete user error:', error.message);
    console.error('❌ Full error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера: ' + error.message });
  }
});

// ============= NEWS API =============

// Получение всех новостей
app.get('/api/news', async (req, res) => {
  try {
    const result = await pool.query(
      'SELECT id, title, content, author, type, date FROM news ORDER BY date DESC'
    );

    const news = result.rows.map(item => ({
      id: item.id,
      title: item.title,
      content: item.content,
      author: item.author,
      type: item.type,
      date: item.date
    }));

    res.json({ success: true, data: news });
  } catch (error) {
    console.error('Get news error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Создание новости
app.post('/api/news', async (req, res) => {
  const { title, content, author, type } = req.body;

  try {
    const result = await pool.query(
      'INSERT INTO news (title, content, author, type) VALUES ($1, $2, $3, $4) RETURNING id, title, content, author, type, date',
      [title, content, author, type || 'website']
    );

    const news = {
      id: result.rows[0].id,
      title: result.rows[0].title,
      content: result.rows[0].content,
      author: result.rows[0].author,
      type: result.rows[0].type,
      date: result.rows[0].date
    };

    res.json({ success: true, data: news });
  } catch (error) {
    console.error('Create news error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Удаление новости
app.delete('/api/news/:id', async (req, res) => {
  const { id } = req.params;
  const newsId = parseInt(id, 10);

  try {
    const result = await pool.query(
      'DELETE FROM news WHERE id = $1 RETURNING id',
      [newsId]
    );

    if (result.rows.length === 0) {
      return res.json({ success: false, message: 'Новость не найдена' });
    }

    res.json({ success: true, message: 'Новость удалена' });
  } catch (error) {
    console.error('Delete news error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Serve index.html for all other routes (SPA support)
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, '../dist/index.html'));
});

app.listen(PORT, () => {
  console.log('\n╔════════════════════════════════════════════════════════════╗');
  console.log('║                  🚀 INSIDE Server v3.1.0                  ║');
  console.log('╚════════════════════════════════════════════════════════════╝\n');
  console.log(`✅ Сервер запущен на порту ${PORT}`);
  console.log(`🔐 Google OAuth: ${process.env.GOOGLE_CLIENT_ID ? 'Настроен' : 'Не настроен'}`);
  console.log(`🗄️  База данных: Подключена\n`);
  console.log('📝 Доступные эндпоинты:');
  console.log('   GET  /api/auth/google - Вход через Google');
  console.log('   GET  /api/auth/google/callback - Google OAuth callback');
  console.log('   POST /api/auth/admin - Вход администратора');
  console.log('   GET  /api/auth/logout - Выход из системы');
  console.log('   GET  /api/users - Список пользователей');
  console.log('   GET  /api/users/:id - Информация о пользователе');
  console.log('   GET  /api/news - Список новостей');
  console.log('   POST /api/news - Создание новости');
  console.log('   DELETE /api/news/:id - Удаление новости\n');
  console.log('🔗 Authorized redirect URIs:');
  console.log(`   ${process.env.GOOGLE_CALLBACK_URL || 'https://insidenew.onrender.com/api/auth/google/callback'}`);
  console.log('   http://localhost:8080/api/auth/google/callback\n');
  console.log('🌐 Authorized JavaScript origins:');
  console.log('   https://insidenew.onrender.com');
  console.log('   http://localhost:8080\n');
  console.log('═══════════════════════════════════════════════════════════════\n');
});
