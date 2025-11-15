const { Pool } = require('pg');
require('dotenv').config();

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

async function checkAdmin() {
  try {
    console.log('🔍 Проверка администратора в базе данных...\n');
    
    const adminEmail = process.env.ADMIN_EMAIL || 'admin@lolyou.com';
    
    const result = await pool.query(
      'SELECT id, username, email, password, is_admin, email_verified, subscription FROM users WHERE email = $1',
      [adminEmail]
    );

    if (result.rows.length === 0) {
      console.log(`❌ Администратор с email ${adminEmail} не найден в базе данных`);
      console.log('\n📝 Все пользователи в базе:');
      const allUsers = await pool.query('SELECT id, username, email, is_admin FROM users');
      console.table(allUsers.rows);
    } else {
      const admin = result.rows[0];
      console.log('✅ Администратор найден!\n');
      console.log('📋 Данные администратора:');
      console.log(`   ID: ${admin.id}`);
      console.log(`   Username: ${admin.username}`);
      console.log(`   Email: ${admin.email}`);
      console.log(`   Password: ${admin.password || 'НЕ УСТАНОВЛЕН'}`);
      console.log(`   Is Admin: ${admin.is_admin ? 'ДА' : 'НЕТ'}`);
      console.log(`   Email Verified: ${admin.email_verified ? 'ДА' : 'НЕТ'}`);
      console.log(`   Subscription: ${admin.subscription}`);
      
      if (!admin.password) {
        console.log('\n⚠️  ВНИМАНИЕ: Пароль не установлен!');
      }
      
      if (!admin.is_admin) {
        console.log('\n⚠️  ВНИМАНИЕ: Пользователь не является администратором!');
      }
    }
    
    await pool.end();
  } catch (error) {
    console.error('❌ Ошибка:', error);
    await pool.end();
  }
}

checkAdmin();
