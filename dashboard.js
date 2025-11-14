// Проверка авторизации
const currentUser = JSON.parse(localStorage.getItem('currentUser'));

if (!currentUser) {
    window.location.href = 'auth.html';
}

// Загрузка данных пользователя
function loadUserData() {
    document.getElementById('userName').textContent = currentUser.username;
    document.getElementById('profileUsername').textContent = currentUser.username;
    document.getElementById('profileEmail').textContent = currentUser.email;
    document.getElementById('userId').textContent = currentUser.id;
    
    // Форматирование даты
    const regDate = new Date(currentUser.registeredAt);
    const formattedDate = regDate.toLocaleDateString('ru-RU', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
    
    document.getElementById('regDate').textContent = formattedDate;
    document.getElementById('profileRegDate').textContent = formattedDate;
    
    // Подписка
    const subscriptionNames = {
        'free': 'Бесплатная',
        'premium': 'Премиум',
        'alpha': 'Альфа'
    };
    
    const subName = subscriptionNames[currentUser.subscription] || 'Бесплатная';
    document.getElementById('subscriptionType').textContent = subName;
    document.getElementById('profileSubscription').textContent = subName + ' версия';
    
    // Загрузка настроек
    if (currentUser.settings) {
        document.getElementById('settingNotifications').checked = currentUser.settings.notifications !== false;
        document.getElementById('settingAutoUpdate').checked = currentUser.settings.autoUpdate !== false;
        document.getElementById('settingTheme').value = currentUser.settings.theme || 'dark';
        document.getElementById('settingLanguage').value = currentUser.settings.language || 'ru';
        
        // Применяем тему и язык при загрузке
        applyTheme(currentUser.settings.theme || 'dark');
        applyLanguage(currentUser.settings.language || 'ru');
    }
}

// Применяем тему при загрузке страницы
if (currentUser && currentUser.settings && currentUser.settings.theme) {
    applyTheme(currentUser.settings.theme);
}

// Применение темы
function applyTheme(theme) {
    const root = document.documentElement;
    if (theme === 'light') {
        root.style.setProperty('--bg-primary', '#FFFFFF');
        root.style.setProperty('--bg-secondary', '#F8F9FA');
        root.style.setProperty('--bg-tertiary', '#E9ECEF');
        root.style.setProperty('--text-primary', '#212529');
        root.style.setProperty('--text-secondary', '#6C757D');
        root.style.setProperty('--text-tertiary', '#ADB5BD');
        document.body.style.color = '#212529';
    } else {
        root.style.setProperty('--bg-primary', '#0A0A0F');
        root.style.setProperty('--bg-secondary', '#12121A');
        root.style.setProperty('--bg-tertiary', '#1A1A24');
        root.style.setProperty('--text-primary', '#FFFFFF');
        root.style.setProperty('--text-secondary', '#A0A0B0');
        root.style.setProperty('--text-tertiary', '#6B6B80');
        document.body.style.color = '#FFFFFF';
    }
}

// Переводы
const translations = {
    ru: {
        welcome: 'Добро пожаловать',
        subscription: 'Подписка',
        regDate: 'Дата регистрации',
        status: 'Статус',
        active: 'Активен',
        profile: 'Профиль',
        settings: 'Настройки',
        logout: 'Выйти',
        save: 'Сохранить настройки',
        saved: 'Настройки сохранены и применены!'
    },
    en: {
        welcome: 'Welcome',
        subscription: 'Subscription',
        regDate: 'Registration Date',
        status: 'Status',
        active: 'Active',
        profile: 'Profile',
        settings: 'Settings',
        logout: 'Logout',
        save: 'Save Settings',
        saved: 'Settings saved and applied!'
    },
    uk: {
        welcome: 'Ласкаво просимо',
        subscription: 'Підписка',
        regDate: 'Дата реєстрації',
        status: 'Статус',
        active: 'Активний',
        profile: 'Профіль',
        settings: 'Налаштування',
        logout: 'Вийти',
        save: 'Зберегти налаштування',
        saved: 'Налаштування збережено та застосовано!'
    }
};

// Применение языка
function applyLanguage(lang) {
    const t = translations[lang] || translations.ru;
    
    // Обновляем тексты
    const welcomeText = document.querySelector('.page-header h1');
    if (welcomeText && welcomeText.textContent.includes('Добро пожаловать')) {
        const username = document.getElementById('userName').textContent;
        welcomeText.innerHTML = `${t.welcome}, <span id="userName">${username}</span>!`;
    }
    
    // Сохраняем язык
    currentUser.language = lang;
    localStorage.setItem('currentUser', JSON.stringify(currentUser));
}

// База данных
class Database {
    constructor() {
        this.users = JSON.parse(localStorage.getItem('insideUsers')) || [];
    }
    
    save() {
        localStorage.setItem('insideUsers', JSON.stringify(this.users));
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
    
    setTimeout(() => notification.classList.add('show'), 100);
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

// Отображение аватарки
function displayAvatar(avatarUrl) {
    const img = document.getElementById('profileAvatarImg');
    const defaultSvg = document.getElementById('profileAvatarDefault');
    
    if (avatarUrl) {
        img.src = avatarUrl;
        img.style.display = 'block';
        defaultSvg.style.display = 'none';
    } else {
        img.style.display = 'none';
        defaultSvg.style.display = 'block';
    }
}

// Инициализация после загрузки DOM
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM loaded, initializing dashboard...');
    
    // Загружаем данные пользователя
    loadUserData();
    
    // Загружаем аватарку при старте
    if (currentUser.avatar) {
        displayAvatar(currentUser.avatar);
    }
    
    // Загрузка аватарки
    document.getElementById('avatarUpload').addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(event) {
                const avatarUrl = event.target.result;
                
                // Сохраняем аватарку
                currentUser.avatar = avatarUrl;
                localStorage.setItem('currentUser', JSON.stringify(currentUser));
                
                // Обновляем в базе
                const db = new Database();
                db.updateUser(currentUser.id, { avatar: avatarUrl });
                
                // Отображаем аватарку
                displayAvatar(avatarUrl);
                
                showNotification('Аватарка загружена!', 'success');
            };
            reader.readAsDataURL(file);
        }
    });
    
    // НАВИГАЦИЯ МЕЖДУ СТРАНИЦАМИ - ГЛАВНОЕ!
    const navItems = document.querySelectorAll('.nav-item[data-page]');
    const pages = document.querySelectorAll('.page');
    
    console.log('Navigation items found:', navItems.length);
    console.log('Pages found:', pages.length);
    
    navItems.forEach(item => {
        console.log('Adding listener to:', item.dataset.page);
        item.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            const pageName = this.dataset.page;
            console.log('Navigation clicked:', pageName);
            
            if (pageName) {
                // Убираем active у всех кнопок
                navItems.forEach(nav => nav.classList.remove('active'));
                pages.forEach(page => page.classList.remove('active'));
                
                // Добавляем active к текущей кнопке
                this.classList.add('active');
                
                // Показываем нужную страницу
                const targetPage = document.getElementById(pageName + 'Page');
                console.log('Target page ID:', pageName + 'Page', 'Found:', !!targetPage);
                
                if (targetPage) {
                    targetPage.classList.add('active');
                    console.log('✓ Page activated successfully');
                } else {
                    console.error('✗ Page not found!');
                }
            }
        });
    });
    
    // Быстрые действия
    document.querySelectorAll('.action-card[data-page]').forEach(card => {
        card.addEventListener('click', function() {
            const pageName = this.dataset.page;
            const navItem = document.querySelector(`.nav-item[data-page="${pageName}"]`);
            if (navItem) {
                navItem.click();
            }
        });
    });
    
    // Кнопка скачивания клиента
    const downloadBtn = document.getElementById('downloadClientBtn');
    if (downloadBtn) {
        downloadBtn.addEventListener('click', () => {
            window.location.href = 'index.html#download';
        });
    }
    
    // Кнопка улучшения подписки
    const upgradeBtn = document.getElementById('upgradeSubBtn');
    if (upgradeBtn) {
        upgradeBtn.addEventListener('click', () => {
            window.location.href = 'index.html#pricing';
        });
    }
    
    // Обработчики изменения настроек в реальном времени
    document.getElementById('settingTheme').addEventListener('change', (e) => {
        applyTheme(e.target.value);
    });
    
    document.getElementById('settingLanguage').addEventListener('change', (e) => {
        applyLanguage(e.target.value);
    });
    
    // Сохранение настроек
    document.getElementById('saveSettingsBtn').addEventListener('click', () => {
        const settings = {
            notifications: document.getElementById('settingNotifications').checked,
            autoUpdate: document.getElementById('settingAutoUpdate').checked,
            theme: document.getElementById('settingTheme').value,
            language: document.getElementById('settingLanguage').value
        };
        
        currentUser.settings = settings;
        localStorage.setItem('currentUser', JSON.stringify(currentUser));
        
        // Обновление в базе
        const db = new Database();
        db.updateUser(currentUser.id, { settings });
        
        // Применяем настройки
        applyTheme(settings.theme);
        applyLanguage(settings.language);
        
        const t = translations[settings.language] || translations.ru;
        showNotification(t.saved, 'success');
    });
    
    // Выход
    document.getElementById('logoutBtn').addEventListener('click', () => {
        if (confirm('Вы уверены, что хотите выйти?')) {
            localStorage.removeItem('currentUser');
            window.location.href = 'auth.html';
        }
    });
    
    console.log('✓ Inside Client Dashboard v3.0.0 loaded! 🎮');
});
