const axios = require('axios');

const API_URL = 'https://insidenew.onrender.com';

async function testRegistration() {
  console.log('🧪 Тестирование системы регистрации с email подтверждением\n');

  try {
    // Генерируем уникальный email
    const timestamp = Date.now();
    const testUser = {
      username: `testuser${timestamp}`,
      email: `test${timestamp}@example.com`,
      password: 'Test123456'
    };

    console.log('1️⃣ Регистрация пользователя...');
    console.log(`   Username: ${testUser.username}`);
    console.log(`   Email: ${testUser.email}`);

    const registerResponse = await axios.post(`${API_URL}/api/auth/register`, testUser);

    if (registerResponse.data.success) {
      console.log('✅ Регистрация успешна!');
      console.log(`   User ID: ${registerResponse.data.data.id}`);
      console.log(`   Email Verified: ${registerResponse.data.data.emailVerified}`);
      
      console.log('\n2️⃣ Проверка статуса в базе данных...');
      const userResponse = await axios.get(`${API_URL}/api/users/${registerResponse.data.data.id}`);
      
      if (userResponse.data.success) {
        console.log('✅ Пользователь найден в БД');
        console.log(`   Email Verified: ${userResponse.data.data.emailVerified}`);
        console.log(`   Username: ${userResponse.data.data.username}`);
        console.log(`   Email: ${userResponse.data.data.email}`);
      }

      console.log('\n3️⃣ Проверка MailerLite...');
      console.log('   Откройте https://dashboard.mailerlite.com/subscribers');
      console.log(`   Найдите подписчика: ${testUser.email}`);
      console.log('   Проверьте поля: name и verification_url');

      console.log('\n4️⃣ Следующие шаги:');
      console.log('   ✓ Проверьте консоль сервера - там должна быть ссылка для подтверждения');
      console.log('   ✓ Скопируйте ссылку и откройте в браузере');
      console.log('   ✓ Должна открыться страница подтверждения');
      console.log('   ✓ После подтверждения emailVerified должен стать true');

      console.log('\n✅ Тест завершен успешно!');
    } else {
      console.error('❌ Ошибка регистрации:', registerResponse.data.message);
    }

  } catch (error) {
    console.error('❌ Ошибка:', error.response?.data || error.message);
    
    if (error.code === 'ECONNREFUSED') {
      console.error('\n⚠️  Сервер не запущен!');
      console.error('   Запустите сервер командой: npm run server');
    }
  }
}

// Запуск теста
testRegistration();
