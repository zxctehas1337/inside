const nodemailer = require('nodemailer');
require('dotenv').config();

// Генерация 6-значного кода
function generateVerificationCode() {
  return Math.floor(100000 + Math.random() * 900000).toString();
}

// Настройка SMTP транспорта
const transporter = nodemailer.createTransport({
  host: process.env.SMTP_HOST,
  port: 465,
  secure: true,
  auth: {
    user: process.env.SMTP_USER,
    pass: process.env.SMTP_PASS
  }
});

async function testCodeEmail() {
  console.log('\n🧪 Тестирование отправки кода подтверждения...\n');
  
  const testCode = generateVerificationCode();
  const testEmail = process.env.SMTP_USER; // Отправляем на свой email для теста
  const testUsername = 'TestUser';

  console.log(`📧 Отправка на: ${testEmail}`);
  console.log(`🔢 Код: ${testCode}\n`);

  try {
    const mailOptions = {
      from: process.env.SMTP_FROM,
      to: testEmail,
      subject: 'Тест: Код подтверждения регистрации - Inside',
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
            .content { padding: 40px 30px; color: #ffffff; text-align: center; }
            .content p { font-size: 16px; line-height: 1.6; color: #cccccc; }
            .code-box { background: rgba(255, 255, 255, 0.1); border: 2px solid #667eea; border-radius: 12px; padding: 30px; margin: 30px 0; }
            .code { font-size: 48px; font-weight: bold; letter-spacing: 8px; color: #00d4ff; font-family: 'Courier New', monospace; }
            .footer { padding: 20px; text-align: center; color: #888888; font-size: 12px; border-top: 1px solid #2a2a3e; }
            .warning { color: #ff9800; font-size: 14px; margin-top: 20px; }
          </style>
        </head>
        <body>
          <div class="container">
            <div class="header">
              <h1>✨ Добро пожаловать, ${testUsername}!</h1>
            </div>
            <div class="content">
              <p>Спасибо за регистрацию на платформе Inside!</p>
              <p>Для завершения регистрации введите этот код подтверждения:</p>
              <div class="code-box">
                <div class="code">${testCode}</div>
              </div>
              <p class="warning">⚠️ Код действителен в течение 10 минут</p>
              <p>Если вы не регистрировались на нашем сайте, просто проигнорируйте это письмо.</p>
            </div>
            <div class="footer">
              <p>© 2024 Inside. Все права защищены.</p>
              <p style="color: #ff9800; margin-top: 10px;">🧪 Это тестовое письмо</p>
            </div>
          </div>
        </body>
        </html>
      `
    };

    const info = await transporter.sendMail(mailOptions);
    
    console.log('✅ Письмо успешно отправлено!');
    console.log(`📬 Message ID: ${info.messageId}`);
    console.log(`\n💡 Проверьте почту ${testEmail}`);
    console.log(`🔢 Ожидаемый код: ${testCode}\n`);
    
  } catch (error) {
    console.error('❌ Ошибка отправки:', error.message);
    if (error.code) {
      console.error(`   Код ошибки: ${error.code}`);
    }
  }
}

testCodeEmail();
