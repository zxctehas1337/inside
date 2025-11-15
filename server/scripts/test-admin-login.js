require('dotenv').config();

async function testAdminLogin() {
  const API_URL = process.env.VITE_API_URL || 'https://oneshakedown.onrender.com';
  const email = process.env.ADMIN_EMAIL || 'admin@lolyou.com';
  const password = process.env.ADMIN_PASSWORD || 'SHAKEDOWN-PROJECT-EASY';

  console.log('🧪 Тестирование входа администратора...\n');
  console.log(`   API URL: ${API_URL}`);
  console.log(`   Email: ${email}`);
  console.log(`   Password: ${password}\n`);

  try {
    const response = await fetch(`${API_URL}/api/auth/admin`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password }),
    });

    const result = await response.json();

    console.log('📡 Ответ сервера:');
    console.log(JSON.stringify(result, null, 2));

    if (result.success) {
      console.log('\n✅ Вход администратора успешен!');
      console.log(`   Пользователь: ${result.data.username}`);
      console.log(`   Email: ${result.data.email}`);
      console.log(`   Администратор: ${result.data.isAdmin ? 'ДА' : 'НЕТ'}`);
    } else {
      console.log('\n❌ Ошибка входа:', result.message);
    }
  } catch (error) {
    console.error('\n❌ Ошибка подключения:', error.message);
  }
}

testAdminLogin();
