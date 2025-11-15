const { Pool } = require('pg');
require('dotenv').config();

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

async function updateAdminEmail() {
  try {
    const newEmail = process.env.ADMIN_EMAIL || 'admin@lolyou.com';
    const password = process.env.ADMIN_PASSWORD || 'SHAKEDOWN-PROJECT-EASY';
    
    console.log('🔧 Обновление email администратора...\n');
    console.log(`   Новый email: ${newEmail}`);
    console.log(`   Пароль: ${password}\n`);
    
    // Обновляем email администратора
    const result = await pool.query(
      'UPDATE users SET email = $1, password = $2 WHERE is_admin = true RETURNING id, username, email, password, is_admin',
      [newEmail, password]
    );

    if (result.rows.length === 0) {
      console.log('❌ Администратор не найден');
    } else {
      const admin = result.rows[0];
      console.log('✅ Email администратора обновлен!\n');
      console.log('📋 Новые данные администратора:');
      console.log(`   ID: ${admin.id}`);
      console.log(`   Username: ${admin.username}`);
      console.log(`   Email: ${admin.email}`);
      console.log(`   Password: ${admin.password}`);
      console.log(`   Is Admin: ${admin.is_admin ? 'ДА' : 'НЕТ'}`);
    }
    
    await pool.end();
  } catch (error) {
    console.error('❌ Ошибка:', error);
    await pool.end();
  }
}

updateAdminEmail();
