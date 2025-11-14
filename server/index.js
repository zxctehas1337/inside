const express = require('express');
const cors = require('cors');
const { Pool } = require('pg');
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
        settings JSONB DEFAULT '{"notifications": true, "autoUpdate": true, "theme": "dark", "language": "ru"}'::jsonb
      )
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

    // Создание пользователя
    const result = await pool.query(
      `INSERT INTO users (username, email, password) 
       VALUES ($1, $2, $3) 
       RETURNING id, username, email, subscription, registered_at, is_admin, is_banned, settings`,
      [username, email, Buffer.from(password).toString('base64')]
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
      settings: result.rows[0].settings
    };

    res.json({ success: true, message: 'Регистрация успешна!', data: user });
  } catch (error) {
    console.error('Registration error:', error);
    res.status(500).json({ success: false, message: 'Ошибка сервера' });
  }
});

// Вход
app.post('/api/auth/login', async (req, res) => {
  const { usernameOrEmail, password } = req.body;

  try {
    const encodedPassword = Buffer.from(password).toString('base64');
    
    const result = await pool.query(
      `SELECT id, username, email, password, subscription, registered_at, is_admin, is_banned, settings 
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
      `SELECT id, username, email, password, subscription, registered_at, is_admin, is_banned, settings 
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

app.listen(PORT, () => {
  console.log(`🚀 Сервер запущен на порту ${PORT}`);
});
