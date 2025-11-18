const { Pool } = require('pg');
require('dotenv').config();

const API_URL = process.env.VITE_API_URL || 'http://localhost:8080';

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

async function testAPI() {
  try {
    console.log('🧪 Тест API блокировки и удаления пользователей\n');
    console.log(`API URL: ${API_URL}\n`);

    // Создаем тестового пользователя напрямую в БД
    console.log('1️⃣  Создание тестового пользователя...');
    const createResult = await pool.query(
      `INSERT INTO users (username, email, password, subscription, is_admin, is_banned) 
       VALUES ($1, $2, $3, $4, $5, $6) 
       RETURNING id, username, email, is_banned`,
      ['test_api_user', 'test_api@test.com', 'test123', 'free', false, false]
    );
    
    const testUser = createResult.rows[0];
    console.log('✅ Пользователь создан:', testUser);
    console.log(`   ID: ${testUser.id}, Username: ${testUser.username}, Banned: ${testUser.is_banned}\n`);

    // Тест 2: Блокировка через API
    console.log('2️⃣  Тест блокировки через API (PATCH /api/users/:id)...');
    const banResponse = await fetch(`${API_URL}/api/users/${testUser.id}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ isBanned: true }),
    });
    
    const banData = await banResponse.json();
    console.log('Ответ API:', banData);
    
    if (banData.success && banData.data) {
      console.log(`✅ API вернул успех. Banned: ${banData.data.isBanned}`);
      
      // Проверяем в БД
      const checkBan = await pool.query('SELECT is_banned FROM users WHERE id = $1', [testUser.id]);
      console.log(`   Проверка в БД: is_banned = ${checkBan.rows[0].is_banned}\n`);
    } else {
      console.log('❌ API вернул ошибку:', banData.message, '\n');
    }

    // Тест 3: Разблокировка через API
    console.log('3️⃣  Тест разблокировки через API...');
    const unbanResponse = await fetch(`${API_URL}/api/users/${testUser.id}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ isBanned: false }),
    });
    
    const unbanData = await unbanResponse.json();
    console.log('Ответ API:', unbanData);
    
    if (unbanData.success && unbanData.data) {
      console.log(`✅ API вернул успех. Banned: ${unbanData.data.isBanned}`);
      
      // Проверяем в БД
      const checkUnban = await pool.query('SELECT is_banned FROM users WHERE id = $1', [testUser.id]);
      console.log(`   Проверка в БД: is_banned = ${checkUnban.rows[0].is_banned}\n`);
    } else {
      console.log('❌ API вернул ошибку:', unbanData.message, '\n');
    }

    // Тест 4: Удаление через API
    console.log('4️⃣  Тест удаления через API (DELETE /api/users/:id)...');
    const deleteResponse = await fetch(`${API_URL}/api/users/${testUser.id}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    const deleteData = await deleteResponse.json();
    console.log('Ответ API:', deleteData);
    
    if (deleteData.success) {
      console.log('✅ API вернул успех');
      
      // Проверяем в БД
      const checkDelete = await pool.query('SELECT * FROM users WHERE id = $1', [testUser.id]);
      if (checkDelete.rows.length === 0) {
        console.log('   ✅ Пользователь действительно удален из БД\n');
      } else {
        console.log('   ❌ Пользователь все еще существует в БД!\n');
      }
    } else {
      console.log('❌ API вернул ошибку:', deleteData.message);
      
      // Проверяем в БД
      const checkDelete = await pool.query('SELECT * FROM users WHERE id = $1', [testUser.id]);
      if (checkDelete.rows.length > 0) {
        console.log('   ❌ Пользователь все еще существует в БД!');
        console.log('   Удаляем вручную...');
        await pool.query('DELETE FROM users WHERE id = $1', [testUser.id]);
        console.log('   ✅ Удалено вручную\n');
      }
    }

    console.log('✅ Все тесты завершены!');
    
  } catch (error) {
    console.error('❌ Ошибка теста:', error.message);
    console.error('Stack:', error.stack);
  } finally {
    await pool.end();
  }
}

testAPI();
