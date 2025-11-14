const nodemailer = require('nodemailer');
require('dotenv').config();

async function testGoogleSMTP() {
  console.log('🧪 Тестирование Google SMTP (альтернативная конфигурация)...\n');

  // Пробуем разные конфигурации
  const configs = [
    {
      name: 'Порт 465 (SSL)',
      config: {
        host: 'smtp.gmail.com',
        port: 465,
        secure: true,
        auth: {
          user: process.env.SMTP_USER,
          pass: process.env.SMTP_PASS
        }
      }
    },
    {
      name: 'Порт 587 (TLS)',
      config: {
        host: 'smtp.gmail.com',
        port: 587,
        secure: false,
        auth: {
          user: process.env.SMTP_USER,
          pass: process.env.SMTP_PASS
        },
        tls: {
          rejectUnauthorized: false
        }
      }
    },
    {
      name: 'Порт 587 (STARTTLS)',
      config: {
        host: 'smtp.gmail.com',
        port: 587,
        secure: false,
        requireTLS: true,
        auth: {
          user: process.env.SMTP_USER,
          pass: process.env.SMTP_PASS
        },
        tls: {
          ciphers: 'SSLv3'
        }
      }
    }
  ];

  for (const { name, config } of configs) {
    try {
      console.log(`\n📡 Попытка подключения: ${name}...`);
      const transporter = nodemailer.createTransport(config);
      
      await transporter.verify();
      console.log(`✅ Подключение успешно с конфигурацией: ${name}\n`);

      // Отправка тестового письма
      console.log('📧 Отправка тестового письма...');
      const testEmail = process.env.SMTP_USER;
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
                <p>Конфигурация: ${name}</p>
                <p>Если вы получили это письмо, значит отправка работает корректно!</p>
                <div style="text-align: center;">
                  <a href="${verificationUrl}" class="button">Тестовая кнопка</a>
                </div>
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
      console.log(`\n🎉 Рабочая конфигурация найдена: ${name}`);
      console.log('\n📋 Используйте эту конфигурацию в server/index.js');
      
      return; // Успех, выходим

    } catch (error) {
      console.log(`❌ Не удалось подключиться: ${error.message}`);
    }
  }

  console.error('\n⚠️  Все попытки подключения не удались!');
  console.error('\nВозможные причины:');
  console.error('1. Файрвол или антивирус блокирует исходящие SMTP соединения');
  console.error('2. Провайдер блокирует SMTP порты (587, 465)');
  console.error('3. Неверный App Password (должен быть 16 символов без пробелов)');
  console.error('4. Двухфакторная аутентификация не включена в Google аккаунте');
  console.error('\nРешения:');
  console.error('- Проверьте настройки файрвола/антивируса');
  console.error('- Попробуйте запустить с другой сети (мобильный интернет)');
  console.error('- Убедитесь, что App Password создан правильно: https://myaccount.google.com/apppasswords');
}

testGoogleSMTP();
