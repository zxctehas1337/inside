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
  origin: ['https://oneshakedown.onrender.com', 'http://localhost:5173', 'http://localhost:8080'],
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

// Хранилище для кодов авторизации больше не используется - прямой OAuth flow

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
      // Получаем аватарку из Google профиля
      const googleAvatar = profile.photos && profile.photos.length > 0 ? profile.photos[0].value : null;
      
      // Проверяем, существует ли пользователь с таким Google ID
      let result = await pool.query(
        'SELECT * FROM users WHERE google_id = $1',
        [profile.id]
      );

      if (result.rows.length > 0) {
        // НЕ обновляем аватарку, если у пользователя уже есть custom_avatar
        // Только обновляем google_avatar для возможности восстановления
        const user = result.rows[0];
        const updateResult = await pool.query(
          'UPDATE users SET google_avatar = $1 WHERE google_id = $2 RETURNING *',
          [googleAvatar, profile.id]
        );
        return done(null, updateResult.rows[0]);
      }

      // Проверяем, существует ли пользователь с таким email
      result = await pool.query(
        'SELECT * FROM users WHERE email = $1',
        [profile.emails[0].value]
      );

      if (result.rows.length > 0) {
        // Обновляем существующего пользователя, добавляя Google ID
        // Устанавливаем avatar только если у пользователя нет custom_avatar
        const user = result.rows[0];
        const avatarToSet = user.custom_avatar ? user.custom_avatar : googleAvatar;
        const updateResult = await pool.query(
          'UPDATE users SET google_id = $1, email_verified = true, google_avatar = $2, avatar = $3 WHERE id = $4 RETURNING *',
          [profile.id, googleAvatar, avatarToSet, result.rows[0].id]
        );
        return done(null, updateResult.rows[0]);
      }

      // Создаем нового пользователя
      const username = profile.emails[0].value.split('@')[0] + '_' + Math.floor(Math.random() * 1000);
      
      // Сначала создаем пользователя без UID
      const newUserResult = await pool.query(
        `INSERT INTO users (username, email, password, google_id, email_verified, subscription, avatar, google_avatar) 
         VALUES ($1, $2, $3, $4, true, 'free', $5, $6) 
         RETURNING *`,
        [username, profile.emails[0].value, '', profile.id, googleAvatar, googleAvatar]
      );
      
      // Генерируем UID на основе года регистрации и ID
      const year = new Date(newUserResult.rows[0].registered_at).getFullYear();
      const uid = `AZ-${year}-${String(newUserResult.rows[0].id).padStart(3, '0')}`;
      
      // Обновляем пользователя с UID
      const updatedUserResult = await pool.query(
        'UPDATE users SET uid = $1 WHERE id = $2 RETURNING *',
        [uid, newUserResult.rows[0].id]
      );

      return done(null, updatedUserResult.rows[0]);
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
        avatar TEXT,
        google_avatar TEXT,
        custom_avatar TEXT,
        uid VARCHAR(50) UNIQUE,
        settings JSONB DEFAULT '{"notifications": true, "autoUpdate": true, "theme": "dark", "language": "ru"}'::jsonb
      )
    `);
    
    // Добавляем колонки для существующих таблиц
    await pool.query(`
      ALTER TABLE users 
      ADD COLUMN IF NOT EXISTS google_id VARCHAR(255) UNIQUE,
      ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT false,
      ADD COLUMN IF NOT EXISTS avatar TEXT,
      ADD COLUMN IF NOT EXISTS google_avatar TEXT,
      ADD COLUMN IF NOT EXISTS custom_avatar TEXT,
      ADD COLUMN IF NOT EXISTS uid VARCHAR(50) UNIQUE
    `);
    
    // Генерируем UID для существующих пользователей без UID
    await pool.query(`
      UPDATE users 
      SET uid = 'AZ-' || TO_CHAR(registered_at, 'YYYY') || '-' || LPAD(id::text, 3, '0')
      WHERE uid IS NULL
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
    
    // Создаем таблицу аналитики
    await pool.query(`
      CREATE TABLE IF NOT EXISTS analytics (
        id SERIAL PRIMARY KEY,
        user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
        event_type VARCHAR(100) NOT NULL,
        page VARCHAR(255),
        data JSONB,
        timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    `);
    
    // Создаем индексы для быстрого поиска
    await pool.query(`
      CREATE INDEX IF NOT EXISTS idx_analytics_user_id ON analytics(user_id);
      CREATE INDEX IF NOT EXISTS idx_analytics_event_type ON analytics(event_type);
      CREATE INDEX IF NOT EXISTS idx_analytics_timestamp ON analytics(timestamp);
    `);
    
    // Создаем таблицу версий чита
    await pool.query(`
      CREATE TABLE IF NOT EXISTS client_versions (
        id SERIAL PRIMARY KEY,
        version VARCHAR(50) NOT NULL,
        download_url TEXT NOT NULL,
        changelog TEXT,
        uploaded_by INTEGER REFERENCES users(id),
        uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        is_active BOOLEAN DEFAULT true
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
    const adminEmail = process.env.ADMIN_EMAIL || 'admin@shakedown.com';
    const adminPassword = process.env.ADMIN_PASSWORD || 'SHAKEDOWN-PROJECT-EASY';
    const adminUsername = process.env.ADMIN_USERNAME || 'admin';

    console.log('🔧 Настройка администратора...');
    console.log(`   Email: ${adminEmail}`);
    console.log(`   Username: ${adminUsername}`);
    console.log(`   Password: ${adminPassword}`);

    // Проверяем, существует ли уже администратор по email или username
    const checkResult = await pool.query(
      'SELECT id, username, email, password, is_admin FROM users WHERE email = $1 OR username = $2',
      [adminEmail, adminUsername]
    );

    if (checkResult.rows.length > 0) {
      const existingUser = checkResult.rows[0];
      console.log(`📝 Найден существующий пользователь: ${existingUser.username} (ID: ${existingUser.id})`);
      console.log(`   Текущий пароль: ${existingUser.password || 'не установлен'}`);
      console.log(`   Администратор: ${existingUser.is_admin ? 'да' : 'нет'}`);
      
      // Обновляем существующего пользователя, делая его администратором
      const updateResult = await pool.query(
        'UPDATE users SET is_admin = true, password = $1, email_verified = true WHERE email = $2 OR username = $3 RETURNING id, username, email, password, is_admin',
        [adminPassword, adminEmail, adminUsername]
      );
      
      console.log('✅ Администратор обновлен:', updateResult.rows[0].email);
      console.log(`   Новый пароль: ${updateResult.rows[0].password}`);
    } else {
      // Создаем нового администратора
      const insertResult = await pool.query(
        `INSERT INTO users (username, email, password, is_admin, email_verified, subscription) 
         VALUES ($1, $2, $3, true, true, 'premium')
         RETURNING id, username, email, password, is_admin`,
        [adminUsername, adminEmail, adminPassword]
      );
      
      console.log('✅ Администратор создан:', insertResult.rows[0].email);
      console.log(`   ID: ${insertResult.rows[0].id}`);
      console.log(`   Пароль: ${insertResult.rows[0].password}`);
    }

    console.log('\n📋 Данные для входа администратора:');
    console.log(`   Email: ${adminEmail}`);
    console.log(`   Password: ${adminPassword}\n`);
  } catch (error) {
    console.error('❌ Ошибка создания администратора:', error);
  }
}

initDatabase();

// Health check
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok' });
});

// Тестовый endpoint удален - используется прямой OAuth flow

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
      avatar: req.user.avatar,
      uid: req.user.uid,
      settings: req.user.settings
    };
    
    if (redirectUrl === 'launcher') {
      // Для лаунчера - перенаправляем на локальный сервер с данными пользователя
      const userData = encodeURIComponent(JSON.stringify(user));
      const callbackUrl = `http://localhost:3000/callback?user=${userData}`;
      
      console.log(`🔄 Перенаправление на локальный сервер лаунчера`);
      console.log(`👤 Пользователь: ${user.email} (ID: ${user.id})`);
      
      // Перенаправляем на локальный сервер лаунчера
      res.redirect(callbackUrl);
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

  console.log(`🔐 Попытка входа администратора: email=${email}`);

  try {
    // Получаем пользователя с паролем в одном запросе
    const result = await pool.query(
      'SELECT id, username, email, password, subscription, registered_at, is_admin, is_banned, avatar, uid, settings FROM users WHERE email = $1 AND is_admin = true',
      [email]
    );

    if (result.rows.length === 0) {
      console.log(`❌ Администратор не найден: ${email}`);
      return res.json({ success: false, message: 'Администратор не найден' });
    }

    const dbUser = result.rows[0];
    console.log(`✅ Администратор найден: ${dbUser.username} (ID: ${dbUser.id})`);
    console.log(`🔑 Пароль в БД: ${dbUser.password ? 'установлен' : 'не установлен'}`);
    console.log(`🔑 Введенный пароль: ${password}`);

    // Проверяем пароль
    if (!dbUser.password) {
      console.log(`❌ У администратора не установлен пароль`);
      return res.json({ success: false, message: 'Пароль не установлен' });
    }

    if (dbUser.password !== password) {
      console.log(`❌ Неверный пароль для ${email}`);
      return res.json({ success: false, message: 'Неверный пароль' });
    }

    console.log(`✅ Вход администратора успешен: ${dbUser.email}`);

    const user = {
      id: dbUser.id,
      username: dbUser.username,
      email: dbUser.email,
      subscription: dbUser.subscription,
      registeredAt: dbUser.registered_at,
      isAdmin: dbUser.is_admin,
      isBanned: dbUser.is_banned,
      avatar: dbUser.avatar,
      uid: dbUser.uid,
      settings: dbUser.settings
    };

    res.json({ success: true, data: user });
  } catch (error) {
    console.error('❌ Admin login error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Endpoint для проверки кода удален - используется прямой OAuth flow

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
       RETURNING id, username, email, password, subscription, registered_at, is_admin, is_banned, avatar, google_avatar, custom_avatar, uid, settings`,
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
      avatar: dbUser.avatar,
      googleAvatar: dbUser.google_avatar,
      customAvatar: dbUser.custom_avatar,
      uid: dbUser.uid,
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
      `SELECT id, username, email, subscription, registered_at, is_admin, is_banned, avatar, uid, settings 
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
      avatar: dbUser.avatar,
      uid: dbUser.uid,
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
      `SELECT id, username, email, subscription, registered_at, is_admin, is_banned, avatar, uid, settings 
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
      avatar: dbUser.avatar,
      uid: dbUser.uid,
      settings: dbUser.settings
    }));

    res.json({ success: true, data: users });
  } catch (error) {
    console.error('Get users error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Изменение подписки пользователя (только для администратора)
app.patch('/api/users/:id/subscription', async (req, res) => {
  const { id } = req.params;
  const userId = parseInt(id, 10);
  const { subscription } = req.body;

  // Проверка валидности подписки
  const validSubscriptions = ['free', 'premium', 'alpha'];
  if (!validSubscriptions.includes(subscription)) {
    return res.json({ success: false, message: 'Неверный тип подписки' });
  }

  try {
    const result = await pool.query(
      `UPDATE users SET subscription = $1 
       WHERE id = $2 
       RETURNING id, username, email, subscription, registered_at, is_admin, is_banned, avatar, uid, settings`,
      [subscription, userId]
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
      avatar: dbUser.avatar,
      uid: dbUser.uid,
      settings: dbUser.settings
    };

    console.log(`✅ Подписка изменена для пользователя: ${dbUser.username} (ID: ${dbUser.id}) -> ${subscription}`);
    res.json({ success: true, data: user });
  } catch (error) {
    console.error('❌ Change subscription error:', error);
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

// Загрузка пользовательской аватарки
app.post('/api/users/:id/avatar', async (req, res) => {
  const { id } = req.params;
  const userId = parseInt(id, 10);
  const { avatar } = req.body; // base64 строка

  try {
    // Обновляем custom_avatar и avatar
    const result = await pool.query(
      `UPDATE users SET custom_avatar = $1, avatar = $1 
       WHERE id = $2 
       RETURNING id, username, email, subscription, registered_at, is_admin, is_banned, avatar, google_avatar, custom_avatar, uid, settings`,
      [avatar, userId]
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
      avatar: dbUser.avatar,
      googleAvatar: dbUser.google_avatar,
      customAvatar: dbUser.custom_avatar,
      uid: dbUser.uid,
      settings: dbUser.settings
    };

    console.log(`✅ Аватарка обновлена для пользователя: ${dbUser.username} (ID: ${dbUser.id})`);
    res.json({ success: true, data: user });
  } catch (error) {
    console.error('❌ Upload avatar error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Удаление пользовательской аватарки (восстановление Google аватарки)
app.delete('/api/users/:id/avatar', async (req, res) => {
  const { id } = req.params;
  const userId = parseInt(id, 10);

  try {
    // Удаляем custom_avatar и восстанавливаем google_avatar
    const result = await pool.query(
      `UPDATE users SET custom_avatar = NULL, avatar = COALESCE(google_avatar, NULL) 
       WHERE id = $1 
       RETURNING id, username, email, subscription, registered_at, is_admin, is_banned, avatar, google_avatar, custom_avatar, uid, settings`,
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
      avatar: dbUser.avatar,
      googleAvatar: dbUser.google_avatar,
      customAvatar: dbUser.custom_avatar,
      uid: dbUser.uid,
      settings: dbUser.settings
    };

    console.log(`✅ Пользовательская аватарка удалена для: ${dbUser.username} (ID: ${dbUser.id})`);
    res.json({ success: true, data: user });
  } catch (error) {
    console.error('❌ Delete avatar error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// ============= LAUNCHER UPDATES API =============

// Раздача обновлений для лаунчера
app.use('/updates', express.static(path.join(__dirname, 'updates')));

// Информация о последней версии (для проверки обновлений)
app.get('/api/launcher/version', async (req, res) => {
  try {
    const fs = require('fs');
    const ymlPath = path.join(__dirname, 'updates', 'latest.yml');
    
    if (!fs.existsSync(ymlPath)) {
      return res.json({ 
        success: false, 
        message: 'Файл обновления не найден' 
      });
    }
    
    const ymlContent = fs.readFileSync(ymlPath, 'utf8');
    res.set('Content-Type', 'text/yaml');
    res.send(ymlContent);
  } catch (error) {
    console.error('❌ Ошибка получения версии:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// ============= CLIENT VERSION API =============

// Получение текущей версии чита
app.get('/api/client/version', async (req, res) => {
  try {
    const result = await pool.query(
      'SELECT version, download_url, changelog, uploaded_at FROM client_versions WHERE is_active = true ORDER BY uploaded_at DESC LIMIT 1'
    );

    if (result.rows.length === 0) {
      return res.json({ 
        success: false, 
        message: 'Версия чита не найдена' 
      });
    }

    const version = {
      version: result.rows[0].version,
      downloadUrl: result.rows[0].download_url,
      changelog: result.rows[0].changelog,
      uploadedAt: result.rows[0].uploaded_at
    };

    res.json({ success: true, data: version });
  } catch (error) {
    console.error('❌ Ошибка получения версии чита:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Загрузка новой версии чита (только для админов)
app.post('/api/client/version', async (req, res) => {
  const { version, downloadUrl, changelog, userId } = req.body;

  try {
    // Проверяем, что пользователь - администратор
    const userCheck = await pool.query(
      'SELECT is_admin FROM users WHERE id = $1',
      [userId]
    );

    if (userCheck.rows.length === 0 || !userCheck.rows[0].is_admin) {
      return res.json({ success: false, message: 'Доступ запрещен' });
    }

    // Деактивируем предыдущие версии
    await pool.query('UPDATE client_versions SET is_active = false WHERE is_active = true');

    // Добавляем новую версию
    const result = await pool.query(
      'INSERT INTO client_versions (version, download_url, changelog, uploaded_by) VALUES ($1, $2, $3, $4) RETURNING id, version, download_url, changelog, uploaded_at',
      [version, downloadUrl, changelog, userId]
    );

    const newVersion = {
      id: result.rows[0].id,
      version: result.rows[0].version,
      downloadUrl: result.rows[0].download_url,
      changelog: result.rows[0].changelog,
      uploadedAt: result.rows[0].uploaded_at
    };

    console.log(`✅ Новая версия чита загружена: ${version} (Admin ID: ${userId})`);
    res.json({ success: true, data: newVersion });
  } catch (error) {
    console.error('❌ Ошибка загрузки версии чита:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Получение истории версий (только для админов)
app.get('/api/client/versions', async (req, res) => {
  try {
    const result = await pool.query(
      `SELECT cv.id, cv.version, cv.download_url, cv.changelog, cv.uploaded_at, cv.is_active, u.username as uploaded_by_name
       FROM client_versions cv
       LEFT JOIN users u ON cv.uploaded_by = u.id
       ORDER BY cv.uploaded_at DESC`
    );

    const versions = result.rows.map(row => ({
      id: row.id,
      version: row.version,
      downloadUrl: row.download_url,
      changelog: row.changelog,
      uploadedAt: row.uploaded_at,
      isActive: row.is_active,
      uploadedBy: row.uploaded_by_name
    }));

    res.json({ success: true, data: versions });
  } catch (error) {
    console.error('❌ Ошибка получения истории версий:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// ============= ANALYTICS API =============

// Запись события аналитики
app.post('/api/analytics', async (req, res) => {
  const { userId, eventType, page, data } = req.body;

  try {
    const result = await pool.query(
      'INSERT INTO analytics (user_id, event_type, page, data) VALUES ($1, $2, $3, $4) RETURNING id',
      [userId || null, eventType, page || null, data ? JSON.stringify(data) : null]
    );

    res.json({ success: true, id: result.rows[0].id });
  } catch (error) {
    console.error('Analytics error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Получение статистики
app.get('/api/analytics/stats', async (req, res) => {
  try {
    // Общее количество посещений
    const totalVisits = await pool.query(
      "SELECT COUNT(*) as count FROM analytics WHERE event_type = 'page_view'"
    );

    // Уникальные пользователи
    const uniqueUsers = await pool.query(
      "SELECT COUNT(DISTINCT user_id) as count FROM analytics WHERE user_id IS NOT NULL"
    );

    // Посещения за последние 7 дней (по дням)
    const weeklyVisits = await pool.query(`
      SELECT 
        DATE(timestamp) as date,
        COUNT(*) as visits
      FROM analytics 
      WHERE event_type = 'page_view' 
        AND timestamp >= NOW() - INTERVAL '7 days'
      GROUP BY DATE(timestamp)
      ORDER BY date ASC
    `);

    // Популярные страницы
    const popularPages = await pool.query(`
      SELECT 
        page,
        COUNT(*) as visits
      FROM analytics 
      WHERE event_type = 'page_view' AND page IS NOT NULL
      GROUP BY page
      ORDER BY visits DESC
      LIMIT 10
    `);

    // События кликов
    const clickEvents = await pool.query(`
      SELECT 
        data->>'element' as element,
        COUNT(*) as clicks
      FROM analytics 
      WHERE event_type = 'click' AND data IS NOT NULL
      GROUP BY data->>'element'
      ORDER BY clicks DESC
      LIMIT 10
    `);

    // Среднее время на сайте (в секундах)
    const avgSessionTime = await pool.query(`
      SELECT 
        AVG(EXTRACT(EPOCH FROM (MAX(timestamp) - MIN(timestamp)))) as avg_time
      FROM analytics
      WHERE user_id IS NOT NULL
      GROUP BY user_id, DATE(timestamp)
    `);

    // Активность по часам
    const hourlyActivity = await pool.query(`
      SELECT 
        EXTRACT(HOUR FROM timestamp) as hour,
        COUNT(*) as activity
      FROM analytics
      WHERE timestamp >= NOW() - INTERVAL '24 hours'
      GROUP BY EXTRACT(HOUR FROM timestamp)
      ORDER BY hour ASC
    `);

    res.json({
      success: true,
      data: {
        totalVisits: parseInt(totalVisits.rows[0].count),
        uniqueUsers: parseInt(uniqueUsers.rows[0].count),
        weeklyVisits: weeklyVisits.rows,
        popularPages: popularPages.rows,
        clickEvents: clickEvents.rows,
        avgSessionTime: avgSessionTime.rows[0]?.avg_time || 0,
        hourlyActivity: hourlyActivity.rows
      }
    });
  } catch (error) {
    console.error('Get analytics stats error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
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
  console.log('║              🚀 ShakeDown Server v3.1.0                   ║');
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
  console.log('   PATCH /api/users/:id/subscription - Изменение подписки');
  console.log('   GET  /api/news - Список новостей');
  console.log('   POST /api/news - Создание новости');
  console.log('   DELETE /api/news/:id - Удаление новости');
  console.log('   POST /api/analytics - Запись события аналитики');
  console.log('   GET  /api/analytics/stats - Получение статистики\n');
  console.log('🔗 Authorized redirect URIs:');
  console.log(`   ${process.env.GOOGLE_CALLBACK_URL || 'https://oneshakedown.onrender.com/api/auth/google/callback'}`);
  console.log('   http://localhost:8080/api/auth/google/callback\n');
  console.log('🌐 Authorized JavaScript origins:');
  console.log('   https://oneshakedown.onrender.com');
  console.log('   http://localhost:8080\n');
  console.log('═══════════════════════════════════════════════════════════════\n');
});
