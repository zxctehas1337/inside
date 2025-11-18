const { Pool } = require('pg');
require('dotenv').config({ path: require('path').join(__dirname, '../../.env') });

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: {
    rejectUnauthorized: false
  }
});

async function fixReactionsConstraint() {
  try {
    console.log('🔧 Исправление ограничений таблицы comment_reactions...');

    // Удаляем старое ограничение UNIQUE(comment_id, user_id, reaction)
    await pool.query(`
      ALTER TABLE comment_reactions 
      DROP CONSTRAINT IF EXISTS comment_reactions_comment_id_user_id_reaction_key;
    `);
    console.log('✅ Старое ограничение удалено');

    // Удаляем дубликаты реакций (оставляем только последнюю реакцию каждого пользователя)
    await pool.query(`
      DELETE FROM comment_reactions
      WHERE id NOT IN (
        SELECT MAX(id)
        FROM comment_reactions
        GROUP BY comment_id, user_id
      );
    `);
    console.log('✅ Дубликаты реакций удалены');

    // Добавляем новое ограничение UNIQUE(comment_id, user_id)
    await pool.query(`
      ALTER TABLE comment_reactions 
      ADD CONSTRAINT comment_reactions_comment_id_user_id_key 
      UNIQUE (comment_id, user_id);
    `);
    console.log('✅ Новое ограничение добавлено: один пользователь = одна реакция');

    // Создаем индексы для оптимизации
    await pool.query(`
      CREATE INDEX IF NOT EXISTS idx_comment_reactions_comment_id 
      ON comment_reactions(comment_id);
    `);
    await pool.query(`
      CREATE INDEX IF NOT EXISTS idx_comment_reactions_user_id 
      ON comment_reactions(user_id);
    `);
    console.log('✅ Индексы созданы для ускорения запросов');

    console.log('\n✨ Миграция завершена успешно!');
    console.log('Теперь каждый пользователь может поставить только одну реакцию на комментарий.');
    
  } catch (error) {
    console.error('❌ Ошибка миграции:', error);
  } finally {
    await pool.end();
  }
}

fixReactionsConstraint();
