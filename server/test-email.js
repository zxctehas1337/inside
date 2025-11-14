const nodemailer = require('nodemailer');
require('dotenv').config();

async function testGoogleSMTP() {
  console.log('🧪 Тестирование Google SMTP...\n');

  try {
    // Создание транспорта
    console.log('1️⃣ Подключение к Google SMTP...');
    const transporter = nodemailer.createTransport({
      host: process.env.SMTP_HOST,
      port: 465,
      secure: true, // true для порта 465
      auth: {
        user: process.env.SMTP_USER,
        pass: process.env.SMTP_PASS
      }
    });

    // Проверка подключения
    await transporter.verify();
    console.log('✅ Подключение к SMTP серверу успешно!\n');

    // Отправка тестового письма
    console.log('2️⃣ Отправка тестового письма...');
    const testEmail = process.env.SMTP_USER; // Отправляем самому себе для теста
    const testToken = `test_token_${Date.now()}`;
    const verificationUrl = `https://insidenew.onrender.com/api/auth/verify-email?token=${testToken}`;
    
    const mailOptions = {
      from: process.env.SMTP_FROM,
      to: testEmail,
      subject: 'Тест отправки - Vansono',
      html: `
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
          <style>
            body { font-family: Arial, sans-serif; background-color: #0a0a0a; margin: 0; padding: 0; }
            .container { max-width: 600px; margin: 0 auto; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); border-radius: 12px; overflow: hidden; }
            .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; }
            .header h1 { color: #ffffff; margin: 0; font-size: 28px; }
            .content { padding: 40px 30px; color: #ffffff; }
            .content p { font-size: 16px; line-height: 1.6; color: #cccccc; }
            .button { display: inline-block; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px 40px; text-decoration: none; border-radius: 8px; margin: 20px 0; font-weight: bold; }
            .footer { padding: 20px; text-align: center; color: #888888; font-size: 12px; border-top: 1px solid #2a2a3e; }
          </style>
        </head>
        <body>
          <div class="container">
            <div class="header">
              <h1>✨ Тестовое письмо</h1>
            </div>
            <div class="content">
              <p>Это тестовое письмо для проверки работы Google SMTP.</p>
              <p>Если вы получили это письмо, значит отправка работает корректно!</p>
              <div style="text-align: center;">
                <a href="${verificationUrl}" class="button">Тестовая кнопка</a>
              </div>
              <p>Тестовая ссылка:</p>
              <p style="word-break: break-all; color: #00d4ff;">${verificationUrl}</p>
            </div>
            <div class="footer">
              <p>© 2024 Vansono. Все права защищены.</p>
            </div>
          </div>
        </body>
        </html>
      `
    };

    const info = await transporter.sendMail(mailOptions);
    
    console.log('✅ Письмо успешно отправлено!');
    console.log('   Получатель:', testEmail);
    console.log('   Message ID:', info.messageId);
    console.log('\n📋 Проверьте почту:', testEmail);
    console.log('   (Проверьте также папку "Спам")');

  } catch (error) {
    console.error('❌ Ошибка:', error.message);
    
    if (error.code === 'EAUTH') {
      console.error('\n⚠️  Ошибка аутентификации!');
      console.error('   Проверьте SMTP_USER и SMTP_PASS в файле .env');
      console.error('   Убедитесь, что используете App Password, а не обычный пароль Gmail');
    } else if (error.code === 'ECONNECTION') {
      console.error('\n⚠️  Ошибка подключения!');
      console.error('   Проверьте SMTP_HOST и SMTP_PORT в файле .env');
      console.error('   Проверьте подключение к интернету');
    } else {
      console.error('\n⚠️  Неизвестная ошибка. Проверьте:');
      console.error('   1. Настройки в .env файле');
      console.error('   2. Подключение к интернету');
      console.error('   3. Правильность App Password для Gmail');
    }
  }
}

testGoogleSMTP();
