const { Pool } = require('pg');
require('dotenv').config({ path: require('path').join(__dirname, '../../.env') });

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

async function testGoogleUserDelete() {
  try {
    console.log('\n🧪 Тест удаления пользователей с Google ID\n');
    
    // Создаем тестового пользователя с Google ID
    console.log('1️⃣  Создание тестового пользователя с Google ID...');
    const createResult = await pool.query(
      `INSERT INTO users (username, email, password, google_id, email_verified, subscription) 
       VALUES ($1, $2, $3, $4, true, 'free') 
       RETURNING id, username, email, google_id`,
      ['test_google_user', 'test_google@example.com', '', 'google_test_123456']
    );
    
    const testUser = createResult.rows[0];
    console.log('✅ Пользователь создан:', testUser);
    
    // Проверяем, что пользователь существует
    console.log('\n2️⃣  Проверка существования пользователя...');
    const checkResult = await pool.query(
      'SELECT id, username, email, google_id FROM users WHERE id = $1',
      [testUser.id]
    );
    console.log('✅ Пользователь найден:', checkResult.rows[0]);
    
    // Удаляем пользователя
    console.log('\n3️⃣  Удаление пользователя...');
    const deleteResult = await pool.query(
      'DELETE FROM users WHERE id = $1 RETURNING id, username, email',
      [testUser.id]
    );
    console.log('✅ Пользователь удален:', deleteResult.rows[0]);
    
    // Проверяем, что пользователь действительно удален
    console.log('\n4️⃣  Проверка удаления...');
    const verifyResult = await pool.query(
      'SELECT id FROM users WHERE id = $1',
      [testUser.id]
    );
    
    if (verifyResult.rows.length === 0) {
      console.log('✅ Пользователь успешно удален из базы данных');
    } else {
      console.log('❌ ОШИБКА: Пользователь все еще существует!');
    }
    
    console.log('\n✅ Тест завершен успешно!\n');
    
  } catch (error) {
    console.error('\n❌ Ошибка теста:', error.message);
    console.error('Детали:', error);
  } finally {
    await pool.end();
  }
}

testGoogleUserDelete();
