const fetch = require('node-fetch');

const API_URL = process.env.API_URL || 'http://localhost:8080';

async function testDeleteUser() {
  console.log('\n╔════════════════════════════════════════════════════════════╗');
  console.log('║           🧪 Тест удаления пользователя                   ║');
  console.log('╚════════════════════════════════════════════════════════════╝\n');

  try {
    // 1. Создание тестового пользователя напрямую в БД
    console.log('📝 Шаг 1: Создание тестового пользователя...');
    console.log('⚠️  Примечание: Регистрация через API больше не поддерживается');
    console.log('⚠️  Используйте Google OAuth для входа');
    return;

    // 2. Проверка существования пользователя
    console.log('📝 Шаг 2: Проверка существования пользователя...');
    const getUserResponse = await fetch(`${API_URL}/api/users/${userId}`);
    const getUserData = await getUserResponse.json();
    console.log('Пользователь найден:', getUserData.success);
    console.log('Данные:', getUserData.data?.username, getUserData.data?.email, '\n');

    // 3. Удаление пользователя
    console.log('📝 Шаг 3: Удаление пользователя...');
    const deleteResponse = await fetch(`${API_URL}/api/users/${userId}`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' }
    });

    const deleteData = await deleteResponse.json();
    console.log('Результат удаления:', deleteData);

    if (deleteData.success) {
      console.log(`✅ Пользователь "${deleteData.username}" успешно удален\n`);
    } else {
      console.log('❌ Не удалось удалить пользователя\n');
      return;
    }

    // 4. Проверка, что пользователь действительно удален
    console.log('📝 Шаг 4: Проверка, что пользователь удален...');
    const checkUserResponse = await fetch(`${API_URL}/api/users/${userId}`);
    const checkUserData = await checkUserResponse.json();
    
    if (!checkUserData.success) {
      console.log('✅ Пользователь не найден (удален успешно)');
    } else {
      console.log('❌ Пользователь все еще существует!');
    }

    // 5. Попытка входа с удаленным аккаунтом
    console.log('\n📝 Шаг 5: Попытка входа с удаленным аккаунтом...');
    const loginResponse = await fetch(`${API_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        usernameOrEmail: 'test_delete_user',
        password: 'testpass123'
      })
    });

    const loginData = await loginResponse.json();
    
    if (!loginData.success) {
      console.log('✅ Вход заблокирован (аккаунт удален)');
      console.log('Сообщение:', loginData.message);
    } else {
      console.log('❌ Вход выполнен успешно (ошибка - аккаунт должен быть удален!)');
    }

    console.log('\n╔════════════════════════════════════════════════════════════╗');
    console.log('║                  ✅ Тест завершен                          ║');
    console.log('╚════════════════════════════════════════════════════════════╝\n');

  } catch (error) {
    console.error('\n❌ Ошибка теста:', error.message);
  }
}

testDeleteUser();
