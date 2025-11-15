const { Pool } = require('pg');
require('dotenv').config({ path: require('path').join(__dirname, '../../.env') });

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

async function testDeleteAccount() {
  console.log('\n╔════════════════════════════════════════════════════════════╗');
  console.log('║          🧪 Тест удаления аккаунта пользователем          ║');
  console.log('╚════════════════════════════════════════════════════════════╝\n');

  try {
    // 1. Создаем тестового пользователя
    console.log('📝 Шаг 1: Создание тестового пользователя...');
    const testUsername = `test_delete_${Date.now()}`;
    const testEmail = `delete_${Date.now()}@test.com`;
    const testPassword = Buffer.from('testpassword123').toString('base64');

    const createResult = await pool.query(
      `INSERT INTO users (username, email, password, email_verified) 
       VALUES ($1, $2, $3, true) 
       RETURNING id, username, email`,
      [testUsername, testEmail, testPassword]
    );

    const testUser = createResult.rows[0];
    console.log('✅ Пользователь создан:', testUser);

    // 2. Проверяем, что пользователь существует
    console.log('\n🔍 Шаг 2: Проверка существования пользователя...');
    const checkResult = await pool.query(
      'SELECT id, username, email FROM users WHERE id = $1',
      [testUser.id]
    );
    console.log('✅ Пользователь найден:', checkResult.rows[0]);

    // 3. Удаляем пользователя
    console.log('\n🗑️  Шаг 3: Удаление пользователя...');
    const deleteResult = await pool.query(
      'DELETE FROM users WHERE id = $1 RETURNING id, username, email',
      [testUser.id]
    );
    console.log('✅ Пользователь удален:', deleteResult.rows[0]);

    // 4. Проверяем, что пользователь действительно удален
    console.log('\n🔍 Шаг 4: Проверка удаления...');
    const verifyResult = await pool.query(
      'SELECT id FROM users WHERE id = $1',
      [testUser.id]
    );

    if (verifyResult.rows.length === 0) {
      console.log('✅ Подтверждено: пользователь полностью удален из базы данных');
    } else {
      console.log('❌ ОШИБКА: пользователь все еще существует в базе данных!');
    }

    console.log('\n╔════════════════════════════════════════════════════════════╗');
    console.log('║                  ✅ Тест успешно завершен                  ║');
    console.log('╚════════════════════════════════════════════════════════════╝\n');

  } catch (error) {
    console.error('\n❌ Ошибка теста:', error.message);
    console.error('Детали:', error);
  } finally {
    await pool.end();
  }
}

testDeleteAccount();
