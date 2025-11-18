const { Pool } = require('pg');
require('dotenv').config();

// Подключение к PostgreSQL
const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

// Данные для 4 новых администраторов
const newAdmins = [
  {
    username: 'absolutegay',
    email: 'admin@shakedown.com',
    password: 'SHAKEDOWN-EASY-LOL'
  },
  {
    username: 'superhot',
    email: 'superhot@shakedown.com',
    password: 'SUPERHOT-EASY'
  },
  {
    username: 'developer',
    email: 'developer@shakedown.com',
    password: 'Lenya201027'
  },
  {
    username: 'dalpapeb',
    email: 'dalpaped@shakedown.com',
    password: 'DALPAPED-666'
  }
];

async function createAdmins() {
  console.log('🚀 Начинаем создание администраторов...\n');

  for (const admin of newAdmins) {
    try {
      // Проверяем, существует ли пользователь
      const checkResult = await pool.query(
        'SELECT id, username, email, is_admin FROM users WHERE email = $1 OR username = $2',
        [admin.email, admin.username]
      );

      if (checkResult.rows.length > 0) {
        const existingUser = checkResult.rows[0];
        console.log(`📝 Пользователь уже существует: ${existingUser.username} (${existingUser.email})`);
        
        // Обновляем существующего пользователя до администратора
        const updateResult = await pool.query(
          'UPDATE users SET is_admin = true, password = $1, email_verified = true, subscription = $2 WHERE email = $3 OR username = $4 RETURNING id, username, email',
          [admin.password, 'premium', admin.email, admin.username]
        );
        
        console.log(`✅ Обновлен до администратора: ${updateResult.rows[0].email}`);
        console.log(`   Username: ${updateResult.rows[0].username}`);
        console.log(`   Password: ${admin.password}\n`);
      } else {
        // Создаем нового администратора
        const insertResult = await pool.query(
          `INSERT INTO users (username, email, password, is_admin, email_verified, subscription) 
           VALUES ($1, $2, $3, true, true, 'premium')
           RETURNING id, username, email`,
          [admin.username, admin.email, admin.password]
        );
        
        console.log(`✅ Создан новый администратор: ${insertResult.rows[0].email}`);
        console.log(`   Username: ${insertResult.rows[0].username}`);
        console.log(`   Password: ${admin.password}\n`);
      }
    } catch (error) {
      console.error(`❌ Ошибка при создании администратора ${admin.email}:`, error.message);
    }
  }

  // Показываем всех администраторов
  try {
    const allAdmins = await pool.query(
      'SELECT id, username, email, subscription, registered_at FROM users WHERE is_admin = true ORDER BY id'
    );

    console.log('\n📋 Список всех администраторов:');
    console.log('═'.repeat(80));
    allAdmins.rows.forEach((admin, index) => {
      console.log(`${index + 1}. ${admin.username} (${admin.email})`);
      console.log(`   ID: ${admin.id} | Подписка: ${admin.subscription} | Дата: ${new Date(admin.registered_at).toLocaleDateString('ru-RU')}`);
    });
    console.log('═'.repeat(80));
    console.log(`\nВсего администраторов: ${allAdmins.rows.length}`);
  } catch (error) {
    console.error('❌ Ошибка при получении списка администраторов:', error.message);
  }

  await pool.end();
  console.log('\n✅ Процесс завершен!');
}

createAdmins().catch(error => {
  console.error('❌ Критическая ошибка:', error);
  process.exit(1);
});
