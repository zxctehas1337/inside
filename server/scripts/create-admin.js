const { Pool } = require('pg');
require('dotenv').config({ path: '../.env' });

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

async function createAdmin() {
  try {
    const email = 'admin@inside.com';
    const password = 'INSIDE-PROJECT-EASY'; // Измените на свой пароль
    const username = 'admin';

    // Проверяем, существует ли уже администратор
    const checkResult = await pool.query(
      'SELECT * FROM users WHERE email = $1',
      [email]
    );

    if (checkResult.rows.length > 0) {
      // Обновляем существующего пользователя
      const result = await pool.query(
        'UPDATE users SET is_admin = true, password = $1 WHERE email = $2 RETURNING *',
        [password, email]
      );
      console.log('✅ Администратор обновлен:', result.rows[0]);
    } else {
      // Создаем нового администратора
      const result = await pool.query(
        `INSERT INTO users (username, email, password, is_admin, email_verified, subscription) 
         VALUES ($1, $2, $3, true, true, 'premium') 
         RETURNING *`,
        [username, email, password]
      );
      console.log('✅ Администратор создан:', result.rows[0]);
    }

    console.log('\n📋 Данные для входа:');
    console.log(`   Email: ${email}`);
    console.log(`   Password: ${password}`);
    
    await pool.end();
  } catch (error) {
    console.error('❌ Ошибка:', error);
    await pool.end();
  }
}

createAdmin();
