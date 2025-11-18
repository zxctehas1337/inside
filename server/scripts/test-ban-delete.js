const { Pool } = require('pg');
require('dotenv').config();

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

async function testBanAndDelete() {
  try {
    console.log('🧪 Тест блокировки и удаления пользователей\n');

    // Создаем тестового пользователя
    console.log('1️⃣  Создание тестового пользователя...');
    const createResult = await pool.query(
      `INSERT INTO users (username, email, password, subscription, is_admin, is_banned) 
       VALUES ($1, $2, $3, $4, $5, $6) 
       RETURNING id, username, email, is_banned`,
      ['test_ban_user', 'test_ban@test.com', 'test123', 'free', false, false]
    );
    
    const testUser = createResult.rows[0];
    console.log('✅ Пользователь создан:', testUser);
    console.log(`   ID: ${testUser.id}, Username: ${testUser.username}, Banned: ${testUser.is_banned}\n`);

    // Тест 1: Блокировка пользователя
    console.log('2️⃣  Тест блокировки пользователя...');
    const banResult = await pool.query(
      `UPDATE users SET is_banned = $1 
       WHERE id = $2 
       RETURNING id, username, email, is_banned`,
      [true, testUser.id]
    );
    
    if (banResult.rows.length > 0) {
      console.log('✅ Пользователь заблокирован:', banResult.rows[0]);
      console.log(`   Banned: ${banResult.rows[0].is_banned}\n`);
    } else {
      console.log('❌ Не удалось заблокировать пользователя\n');
    }

    // Тест 2: Разблокировка пользователя
    console.log('3️⃣  Тест разблокировки пользователя...');
    const unbanResult = await pool.query(
      `UPDATE users SET is_banned = $1 
       WHERE id = $2 
       RETURNING id, username, email, is_banned`,
      [false, testUser.id]
    );
    
    if (unbanResult.rows.length > 0) {
      console.log('✅ Пользователь разблокирован:', unbanResult.rows[0]);
      console.log(`   Banned: ${unbanResult.rows[0].is_banned}\n`);
    } else {
      console.log('❌ Не удалось разблокировать пользователя\n');
    }

    // Тест 3: Удаление пользователя
    console.log('4️⃣  Тест удаления пользователя...');
    const deleteResult = await pool.query(
      'DELETE FROM users WHERE id = $1 RETURNING id, username, email',
      [testUser.id]
    );
    
    if (deleteResult.rows.length > 0) {
      console.log('✅ Пользователь удален:', deleteResult.rows[0]);
    } else {
      console.log('❌ Не удалось удалить пользователя');
    }

    // Проверка, что пользователь действительно удален
    console.log('\n5️⃣  Проверка удаления...');
    const checkResult = await pool.query(
      'SELECT * FROM users WHERE id = $1',
      [testUser.id]
    );
    
    if (checkResult.rows.length === 0) {
      console.log('✅ Пользователь действительно удален из базы данных');
    } else {
      console.log('❌ Пользователь все еще существует в базе данных!');
    }

    console.log('\n✅ Все тесты завершены!');
    
  } catch (error) {
    console.error('❌ Ошибка теста:', error.message);
    console.error('Stack:', error.stack);
  } finally {
    await pool.end();
  }
}

testBanAndDelete();
