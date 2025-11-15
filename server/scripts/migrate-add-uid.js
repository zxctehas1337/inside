const { Pool } = require('pg');
require('dotenv').config({ path: require('path').join(__dirname, '../../.env') });

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

async function migrateAddUID() {
  try {
    console.log('🔄 Начинаем миграцию: добавление UID для пользователей...\n');

    // Добавляем колонки avatar и uid, если их нет
    await pool.query(`
      ALTER TABLE users 
      ADD COLUMN IF NOT EXISTS avatar TEXT,
      ADD COLUMN IF NOT EXISTS uid VARCHAR(50) UNIQUE
    `);
    console.log('✅ Колонки avatar и uid добавлены (если их не было)\n');

    // Получаем всех пользователей без UID
    const usersResult = await pool.query(
      'SELECT id, username, email, registered_at FROM users WHERE uid IS NULL ORDER BY id'
    );

    if (usersResult.rows.length === 0) {
      console.log('✅ Все пользователи уже имеют UID\n');
      return;
    }

    console.log(`📋 Найдено пользователей без UID: ${usersResult.rows.length}\n`);

    // Генерируем UID для каждого пользователя
    for (const user of usersResult.rows) {
      const year = new Date(user.registered_at).getFullYear();
      const uid = `AZ-${year}-${String(user.id).padStart(3, '0')}`;

      await pool.query(
        'UPDATE users SET uid = $1 WHERE id = $2',
        [uid, user.id]
      );

      console.log(`✅ Пользователь ${user.username} (ID: ${user.id}) -> UID: ${uid}`);
    }

    console.log(`\n✅ Миграция завершена! Обновлено пользователей: ${usersResult.rows.length}`);

  } catch (error) {
    console.error('❌ Ошибка миграции:', error);
  } finally {
    await pool.end();
  }
}

migrateAddUID();
