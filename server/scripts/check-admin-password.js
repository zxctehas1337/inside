const { Pool } = require('pg');
require('dotenv').config();

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

async function checkAdminPassword() {
  try {
    console.log('🔍 Проверка пароля администратора...\n');
    
    const result = await pool.query(
      'SELECT id, username, email, password, is_admin FROM users WHERE is_admin = true'
    );

    if (result.rows.length === 0) {
      console.log('❌ Администраторы не найдены в базе данных');
    } else {
      console.log(`✅ Найдено администраторов: ${result.rows.length}\n`);
      
      result.rows.forEach((admin, index) => {
        console.log(`Администратор #${index + 1}:`);
        console.log(`   ID: ${admin.id}`);
        console.log(`   Username: ${admin.username}`);
        console.log(`   Email: ${admin.email}`);
        console.log(`   Password: ${admin.password || 'НЕ УСТАНОВЛЕН'}`);
        console.log('');
      });
    }
    
    await pool.end();
  } catch (error) {
    console.error('❌ Ошибка:', error);
    await pool.end();
  }
}

checkAdminPassword();
