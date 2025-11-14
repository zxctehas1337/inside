const express = require('express');
const cors = require('cors');
const { Pool } = require('pg');
const nodemailer = require('nodemailer');
const crypto = require('crypto');
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

// Middleware
app.use(cors());
app.use(express.json());

// Настройка SMTP транспорта
const transporter = nodemailer.createTransport({
  host: process.env.SMTP_HOST,
  port: 465,
  secure: true, // true для порта 465
  auth: {
    user: process.env.SMTP_USER,
    pass: process.env.SMTP_PASS
  }
});

// Функция отправки письма подтверждения через Google SMTP
async function sendVerificationEmail(email, username, verificationToken) {
  try {
    const verificationUrl = `https://insidenew.onrender.com/api/auth/verify-email?token=${verificationToken}`;
    
    const mailOptions = {
      from: process.env.SMTP_FROM,
      to: email,
      subject: 'Подтверждение регистрации - Vansono',
      html: `
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
          <style>
            body { font-family: Arial, sans-serif; background-color: #0a0a0a; margin: 0; padding: 0; }
            .container { max-width: 600px; margin: 0 auto; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); border-radius: 12px; overflow: hidden; }
            .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; }
            .header h1 { color: #ffffff; margin: 0; font-size: 28px; }
            .content { padding: 40px 30px; color: #ffffff; }
            .content p { font-size: 16px; line-height: 1.6; color: #cccccc; }
            .button { display: inline-block; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px 40px; text-decoration: none; border-radius: 8px; margin: 20px 0; font-weight: bold; }
            .footer { padding: 20px; text-align: center; color: #888888; font-size: 12px; border-top: 1px solid #2a2a3e; }
          </style>
        </head>
        <body>
          <div class="container">
            <div class="header">
              <h1>✨ Добро пожаловать, ${username}!</h1>
            </div>
            <div class="content">
              <p>Спасибо за регистрацию на платформе Vansono!</p>
              <p>Для завершения регистрации и активации вашего аккаунта, пожалуйста, подтвердите ваш email адрес, нажав на кнопку ниже:</p>
              <div style="text-align: center;">
                <a href="${verificationUrl}" class="button">Подтвердить Email</a>
              </div>
              <p>Или скопируйте и вставьте эту ссылку в браузер:</p>
              <p style="word-break: break-all; color: #00d4ff;">${verificationUrl}</p>
              <p>Если вы не регистрировались на нашем сайте, просто проигнорируйте это письмо.</p>
            </div>
            <div class="footer">
              <p>© 2024 Vansono. Все права защищены.</p>
            </div>
          </div>
        </body>
        </html>
      `
    };

    await transporter.sendMail(mailOptions);
    console.log(`✅ Письмо подтверждения отправлено на ${email}`);
    console.log(`📧 Ссылка для подтверждения: ${verificationUrl}`);
    
    return true;
  } catch (error) {
    console.error('❌ Ошибка отправки email:', error.message);
    return false;
  }
}

// Инициализация таблицы users
async function initDatabase() {
  try {
    await pool.query(`
      CREATE TABLE IF NOT EXISTS users (
        id SERIAL PRIMARY KEY,
        username VARCHAR(255) UNIQUE NOT NULL,
        email VARCHAR(255) UNIQUE NOT NULL,
        password TEXT NOT NULL,
        subscription VARCHAR(50) DEFAULT 'free',
        registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        is_admin BOOLEAN DEFAULT false,
        is_banned BOOLEAN DEFAULT false,
        email_verified BOOLEAN DEFAULT false,
        verification_token TEXT,
        settings JSONB DEFAULT '{"notifications": true, "autoUpdate": true, "theme": "dark", "language": "ru"}'::jsonb
      )
    `);
    
    // Добавляем колонки для существующих таблиц
    await pool.query(`
      ALTER TABLE users 
      ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT false,
      ADD COLUMN IF NOT EXISTS verification_token TEXT
    `);
    
    console.log('✅ База данных инициализирована');
  } catch (error) {
    console.error('❌ Ошибка инициализации БД:', error);
  }
}

initDatabase();

// Health check
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok' });
});

// Регистрация
app.post('/api/auth/register', async (req, res) => {
  const { username, email, password } = req.body;

  try {
    // Проверка существования пользователя
    const existingUser = await pool.query(
      'SELECT * FROM users WHERE username = $1 OR email = $2',
      [username, email]
    );

    if (existingUser.rows.length > 0) {
      const existing = existingUser.rows[0];
      if (existing.username === username) {
        return res.json({ success: false, message: 'Пользователь с таким логином уже существует' });
      }
      if (existing.email === email) {
        return res.json({ success: false, message: 'Email уже зарегистрирован' });
      }
    }

    // Генерация токена подтверждения
    const verificationToken = crypto.randomBytes(32).toString('hex');

    // Создание пользователя
    const result = await pool.query(
      `INSERT INTO users (username, email, password, verification_token, email_verified) 
       VALUES ($1, $2, $3, $4, false) 
       RETURNING id, username, email, subscription, registered_at, is_admin, is_banned, email_verified, settings`,
      [username, email, Buffer.from(password).toString('base64'), verificationToken]
    );

    const user = {
      id: result.rows[0].id,
      username: result.rows[0].username,
      email: result.rows[0].email,
      password: result.rows[0].password,
      subscription: result.rows[0].subscription,
      registeredAt: result.rows[0].registered_at,
      isAdmin: result.rows[0].is_admin,
      isBanned: result.rows[0].is_banned,
      emailVerified: result.rows[0].email_verified,
      settings: result.rows[0].settings
    };

    // Отправка письма подтверждения
    const emailSent = await sendVerificationEmail(email, username, verificationToken);
    
    if (emailSent) {
      res.json({ 
        success: true, 
        message: 'Регистрация успешна! Проверьте email для подтверждения.', 
        data: user 
      });
    } else {
      res.json({ 
        success: true, 
        message: 'Регистрация успешна, но письмо не отправлено. Обратитесь к администратору.', 
        data: user 
      });
    }
  } catch (error) {
    console.error('Registration error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Подтверждение email
app.get('/api/auth/verify-email', async (req, res) => {
  const { token } = req.query;

  if (!token) {
    return res.send(`
      <html>
        <head>
          <title>Ошибка подтверждения</title>
          <style>
            body { font-family: Arial, sans-serif; background: #0a0a0a; color: #fff; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
            .container { text-align: center; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); padding: 40px; border-radius: 12px; border: 1px solid #2a2a3e; }
            h1 { color: #ff4444; }
            a { color: #00d4ff; text-decoration: none; }
          </style>
        </head>
        <body>
          <div class="container">
            <h1>Ошибка</h1>
            <p>Токен подтверждения не найден</p>
            <a href="https://insidenew.onrender.com/auth">Вернуться к авторизации</a>
          </div>
        </body>
      </html>
    `);
  }

  try {
    const result = await pool.query(
      'SELECT * FROM users WHERE verification_token = $1',
      [token]
    );

    if (result.rows.length === 0) {
      return res.send(`
        <html>
          <head>
            <title>Ошибка подтверждения</title>
            <style>
              body { font-family: Arial, sans-serif; background: #0a0a0a; color: #fff; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
              .container { text-align: center; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); padding: 40px; border-radius: 12px; border: 1px solid #2a2a3e; }
              h1 { color: #ff4444; }
              a { color: #00d4ff; text-decoration: none; }
            </style>
          </head>
          <body>
            <div class="container">
              <h1>❌ Неверный токен</h1>
              <p>Токен подтверждения недействителен или уже использован</p>
              <a href="https://insidenew.onrender.com/auth">Вернуться к авторизации</a>
            </div>
          </body>
        </html>
      `);
    }

    // Обновляем статус подтверждения
    await pool.query(
      'UPDATE users SET email_verified = true, verification_token = NULL WHERE verification_token = $1',
      [token]
    );

    res.send(`
      <html>
        <head>
          <title>Email подтвержден</title>
          <style>
            body { font-family: Arial, sans-serif; background: #0a0a0a; color: #fff; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
            .container { text-align: center; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); padding: 40px; border-radius: 12px; border: 1px solid #2a2a3e; }
            h1 { color: #00d4ff; }
            p { color: #cccccc; margin: 20px 0; }
            a { display: inline-block; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 12px 30px; text-decoration: none; border-radius: 8px; margin-top: 20px; }
          </style>
        </head>
        <body>
          <div class="container">
            <h1>✅ Email подтвержден!</h1>
            <p>Ваш email успешно подтвержден. Теперь вы можете войти в систему.</p>
            <a href="https://insidenew.onrender.com/auth">Перейти к авторизации</a>
          </div>
        </body>
      </html>
    `);
  } catch (error) {
    console.error('Verification error:', error);
    res.send(`
      <html>
        <head>
          <title>Ошибка сервера</title>
          <style>
            body { font-family: Arial, sans-serif; background: #0a0a0a; color: #fff; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }
            .container { text-align: center; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); padding: 40px; border-radius: 12px; border: 1px solid #2a2a3e; }
            h1 { color: #ff4444; }
            a { color: #00d4ff; text-decoration: none; }
          </style>
        </head>
        <body>
          <div class="container">
            <h1>❌ Ошибка сервера</h1>
            <p>Произошла ошибка при подтверждении email</p>
            <a href="https://insidenew.onrender.com/auth">Вернуться к авторизации</a>
          </div>
        </body>
      </html>
    `);
  }
});

// Вход
app.post('/api/auth/login', async (req, res) => {
  const { usernameOrEmail, password } = req.body;

  try {
    const encodedPassword = Buffer.from(password).toString('base64');
    
    const result = await pool.query(
      `SELECT id, username, email, password, subscription, registered_at, is_admin, is_banned, email_verified, settings 
       FROM users 
       WHERE (username = $1 OR email = $1) AND password = $2`,
      [usernameOrEmail, encodedPassword]
    );

    if (result.rows.length === 0) {
      return res.json({ success: false, message: 'Неверный логин или пароль' });
    }

    const dbUser = result.rows[0];

    if (dbUser.is_banned) {
      return res.json({ success: false, message: 'Ваш аккаунт заблокирован' });
    }

    const user = {
      id: dbUser.id,
      username: dbUser.username,
      email: dbUser.email,
      password: dbUser.password,
      subscription: dbUser.subscription,
      registeredAt: dbUser.registered_at,
      isAdmin: dbUser.is_admin,
      isBanned: dbUser.is_banned,
      emailVerified: dbUser.email_verified,
      settings: dbUser.settings
    };

    res.json({ success: true, message: 'Вход выполнен!', data: user });
  } catch (error) {
    console.error('Login error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Обновление пользователя
app.patch('/api/users/:id', async (req, res) => {
  const { id } = req.params;
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

    values.push(id);

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

  try {
    const result = await pool.query(
      `SELECT id, username, email, password, subscription, registered_at, is_admin, is_banned, email_verified, settings 
       FROM users WHERE id = $1`,
      [id]
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
      emailVerified: dbUser.email_verified,
      settings: dbUser.settings
    };

    res.json({ success: true, data: user });
  } catch (error) {
    console.error('Get user error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Получение всех пользователей (для админки)
app.get('/api/users', async (req, res) => {
  try {
    const result = await pool.query(
      `SELECT id, username, email, subscription, registered_at, is_admin, is_banned, email_verified, settings 
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
      emailVerified: dbUser.email_verified,
      settings: dbUser.settings
    }));

    res.json({ success: true, data: users });
  } catch (error) {
    console.error('Get users error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

app.listen(PORT, () => {
  console.log('\n╔════════════════════════════════════════════════════════════╗');
  console.log('║                  🚀 INSIDE Server v3.0.0                  ║');
  console.log('╚════════════════════════════════════════════════════════════╝\n');
  console.log(`✅ Сервер запущен на порту ${PORT}`);
  console.log(`📧 Google SMTP: ${process.env.SMTP_USER || 'Не настроен'}`);
  console.log(`🗄️  База данных: Подключена\n`);
  console.log('📝 Доступные эндпоинты:');
  console.log('   POST /api/auth/register - Регистрация');
  console.log('   POST /api/auth/login - Вход');
  console.log('   GET  /api/auth/verify-email?token=xxx - Подтверждение email');
  console.log('   GET  /api/users - Список пользователей');
  console.log('   GET  /api/users/:id - Информация о пользователе\n');
  console.log('🧪 Тестирование:');
  console.log('   npm run test:email - Проверка отправки email');
  console.log('   npm run test:registration - Тест регистрации\n');
  console.log('═══════════════════════════════════════════════════════════════\n');
});
