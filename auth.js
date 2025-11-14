// Переключение между формами
const tabBtns = document.querySelectorAll('.tab-btn');
const forms = document.querySelectorAll('.auth-form');

tabBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        const tab = btn.dataset.tab;
        
        tabBtns.forEach(b => b.classList.remove('active'));
        forms.forEach(f => f.classList.remove('active'));
        
        btn.classList.add('active');
        document.getElementById(tab + 'Form').classList.add('active');
    });
});

// Toggle password visibility
document.querySelectorAll('.toggle-password').forEach(btn => {
    btn.addEventListener('click', function() {
        const input = this.previousElementSibling;
        const type = input.type === 'password' ? 'text' : 'password';
        input.type = type;
        
        // Меняем иконку
        if (type === 'text') {
            this.innerHTML = `
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M3 3L17 17" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    <path d="M10 7C8.34315 7 7 8.34315 7 10C7 11.6569 8.34315 13 10 13C11.6569 13 13 11.6569 13 10" stroke="currentColor" stroke-width="2"/>
                    <path d="M10 3C5 3 2 10 2 10C2 10 3.5 13 6 15M10 17C15 17 18 10 18 10C18 10 16.5 7 14 5" stroke="currentColor" stroke-width="2"/>
                </svg>
            `;
        } else {
            this.innerHTML = `
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M10 7C8.34315 7 7 8.34315 7 10C7 11.6569 8.34315 13 10 13C11.6569 13 13 11.6569 13 10C13 8.34315 11.6569 7 10 7Z" stroke="currentColor" stroke-width="2"/>
                    <path d="M10 3C5 3 2 10 2 10C2 10 5 17 10 17C15 17 18 10 18 10C18 10 15 3 10 3Z" stroke="currentColor" stroke-width="2"/>
                </svg>
            `;
        }
    });
});

// Простая "база данных" в localStorage
class Database {
    constructor() {
        this.users = JSON.parse(localStorage.getItem('insideUsers')) || [];
    }
    
    save() {
        localStorage.setItem('insideUsers', JSON.stringify(this.users));
    }
    
    register(username, email, password) {
        // Проверка существования пользователя
        if (this.users.find(u => u.username === username)) {
            return { success: false, message: 'Пользователь с таким логином уже существует' };
        }
        
        if (this.users.find(u => u.email === email)) {
            return { success: false, message: 'Email уже зарегистрирован' };
        }
        
        // Создание пользователя
        const user = {
            id: Date.now(),
            username,
            email,
            password: btoa(password), // Простое кодирование (в реальном проекте используйте bcrypt)
            subscription: 'free',
            registeredAt: new Date().toISOString(),
            settings: {
                notifications: true,
                autoUpdate: true,
                theme: 'dark',
                language: 'ru'
            }
        };
        
        this.users.push(user);
        this.save();
        
        return { success: true, message: 'Регистрация успешна!', user };
    }
    
    login(usernameOrEmail, password) {
        const user = this.users.find(u => 
            (u.username === usernameOrEmail || u.email === usernameOrEmail) &&
            u.password === btoa(password)
        );
        
        if (user) {
            return { success: true, message: 'Вход выполнен!', user };
        }
        
        return { success: false, message: 'Неверный логин или пароль' };
    }
    
    updateUser(userId, updates) {
        const userIndex = this.users.findIndex(u => u.id === userId);
        if (userIndex !== -1) {
            this.users[userIndex] = { ...this.users[userIndex], ...updates };
            this.save();
            return { success: true, user: this.users[userIndex] };
        }
        return { success: false, message: 'Пользователь не найден' };
    }
}

const db = new Database();

// Обработка регистрации
document.getElementById('registerForm').addEventListener('submit', (e) => {
    e.preventDefault();
    
    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const passwordConfirm = document.getElementById('regPasswordConfirm').value;
    const agreeTerms = document.getElementById('agreeTerms').checked;
    
    // Валидация
    if (password !== passwordConfirm) {
        showNotification('Пароли не совпадают', 'error');
        return;
    }
    
    if (password.length < 6) {
        showNotification('Пароль должен быть не менее 6 символов', 'error');
        return;
    }
    
    if (!agreeTerms) {
        showNotification('Необходимо согласиться с условиями', 'error');
        return;
    }
    
    // Регистрация
    const result = db.register(username, email, password);
    
    if (result.success) {
        showNotification(result.message, 'success');
        
        // Автоматический вход
        localStorage.setItem('currentUser', JSON.stringify(result.user));
        
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 1500);
    } else {
        showNotification(result.message, 'error');
    }
});

// Обработка входа
document.getElementById('loginForm').addEventListener('submit', (e) => {
    e.preventDefault();
    
    const usernameOrEmail = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    const rememberMe = document.getElementById('rememberMe').checked;
    
    const result = db.login(usernameOrEmail, password);
    
    if (result.success) {
        showNotification(result.message, 'success');
        
        // Сохранение пользователя
        if (rememberMe) {
            localStorage.setItem('rememberMe', 'true');
        }
        localStorage.setItem('currentUser', JSON.stringify(result.user));
        
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 1500);
    } else {
        showNotification(result.message, 'error');
    }
});

// Уведомления
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                ${type === 'success' ? 
                    '<path d="M16 6L8 14L4 10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>' :
                    '<path d="M10 6V10M10 14H10.01" stroke="currentColor" stroke-width="2" stroke-linecap="round"/><circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="2"/>'
                }
            </svg>
            <span>${message}</span>
        </div>
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.classList.add('show');
    }, 100);
    
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Добавляем стили для уведомлений
const notificationStyles = document.createElement('style');
notificationStyles.textContent = `
    .notification {
        position: fixed;
        top: 24px;
        right: 24px;
        padding: 16px 20px;
        background: var(--bg-secondary);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 12px;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
        z-index: 10000;
        transform: translateX(400px);
        opacity: 0;
        transition: all 0.3s ease;
    }
    
    .notification.show {
        transform: translateX(0);
        opacity: 1;
    }
    
    .notification-content {
        display: flex;
        align-items: center;
        gap: 12px;
        color: white;
    }
    
    .notification-success {
        border-left: 3px solid #10B981;
    }
    
    .notification-success svg {
        color: #10B981;
    }
    
    .notification-error {
        border-left: 3px solid #EF4444;
    }
    
    .notification-error svg {
        color: #EF4444;
    }
`;
document.head.appendChild(notificationStyles);

console.log('Inside Client Auth System v3.0.0 loaded! 🔐');
