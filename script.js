// Smooth scrolling
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// Header scroll effect
let lastScroll = 0;
const header = document.querySelector('.header');

window.addEventListener('scroll', () => {
    const currentScroll = window.pageYOffset;
    
    if (currentScroll > 100) {
        header.style.background = 'rgba(10, 10, 15, 0.95)';
        header.style.boxShadow = '0 4px 20px rgba(0, 0, 0, 0.3)';
    } else {
        header.style.background = 'rgba(10, 10, 15, 0.8)';
        header.style.boxShadow = 'none';
    }
    
    lastScroll = currentScroll;
});

// Intersection Observer for animations
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
        }
    });
}, observerOptions);

// Animate elements on scroll
document.querySelectorAll('.feature-card, .pricing-card').forEach(el => {
    el.style.opacity = '0';
    el.style.transform = 'translateY(30px)';
    el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
    observer.observe(el);
});

// Pricing card hover effects
document.querySelectorAll('.pricing-card').forEach(card => {
    card.addEventListener('mouseenter', () => {
        if (!card.classList.contains('featured')) {
            card.style.transform = 'translateY(-12px) scale(1.02)';
        }
    });
    
    card.addEventListener('mouseleave', () => {
        if (!card.classList.contains('featured')) {
            card.style.transform = 'translateY(0) scale(1)';
        } else {
            card.style.transform = 'translateY(-8px) scale(1.05)';
        }
    });
});

// Button ripple effect
document.querySelectorAll('.btn').forEach(btn => {
    btn.addEventListener('click', function(e) {
        const ripple = document.createElement('span');
        const rect = this.getBoundingClientRect();
        const size = Math.max(rect.width, rect.height);
        const x = e.clientX - rect.left - size / 2;
        const y = e.clientY - rect.top - size / 2;
        
        ripple.style.width = ripple.style.height = size + 'px';
        ripple.style.left = x + 'px';
        ripple.style.top = y + 'px';
        ripple.classList.add('ripple');
        
        this.appendChild(ripple);
        
        setTimeout(() => {
            ripple.remove();
        }, 600);
    });
});

// Add ripple CSS
const style = document.createElement('style');
style.textContent = `
    .btn {
        position: relative;
        overflow: hidden;
    }
    
    .ripple {
        position: absolute;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.3);
        transform: scale(0);
        animation: ripple-animation 0.6s linear;
        pointer-events: none;
    }
    
    @keyframes ripple-animation {
        to {
            transform: scale(4);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

// Parallax effect for background cubes
window.addEventListener('scroll', () => {
    const scrolled = window.pageYOffset;
    const cubes = document.querySelectorAll('.cube');
    
    cubes.forEach((cube, index) => {
        const speed = 0.5 + (index * 0.2);
        cube.style.transform = `translateY(${scrolled * speed}px) rotate(${scrolled * 0.1}deg)`;
    });
});

// Counter animation for stats with continuous growth
let playerCount = 50000;
let isCounterAnimated = false;
let playerCountElement = null;

function animateCounter(element, target, duration = 2000) {
    let start = 0;
    const increment = target / (duration / 16);
    
    const timer = setInterval(() => {
        start += increment;
        if (start >= target) {
            clearInterval(timer);
            playerCount = Math.floor(target);
            element.textContent = playerCount.toLocaleString('ru-RU') + '+';
            
            // Запускаем постоянный рост после первой анимации
            if (!isCounterAnimated) {
                isCounterAnimated = true;
                playerCountElement = element;
                startContinuousGrowth();
            }
        } else {
            element.textContent = Math.floor(start).toLocaleString('ru-RU') + '+';
        }
    }, 16);
}

// Постоянный рост счетчика игроков с анимацией
function startContinuousGrowth() {
    setInterval(() => {
        const increment = Math.floor(Math.random() * 3) + 1;
        playerCount += increment;
        
        if (playerCountElement) {
            // Анимация прибавления
            playerCountElement.style.transform = 'scale(1.1)';
            playerCountElement.style.color = '#A855F7';
            
            setTimeout(() => {
                playerCountElement.textContent = playerCount.toLocaleString('ru-RU') + '+';
                playerCountElement.style.transform = 'scale(1)';
                playerCountElement.style.color = '';
            }, 200);
        }
    }, 2000); // Каждые 2 секунды
}

// Trigger counter animation when stats come into view
const statsObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting && !isCounterAnimated) {
            const statNumbers = entry.target.querySelectorAll('.stat-value');
            statNumbers.forEach((stat, index) => {
                const text = stat.textContent;
                if (text.includes('K') && index === 0) {
                    // Первый счетчик - игроки (с постоянным ростом)
                    animateCounter(stat, playerCount);
                } else if (text.includes('%')) {
                    // Процент
                    const value = parseFloat(text);
                    let current = 0;
                    const timer = setInterval(() => {
                        current += 0.1;
                        if (current >= value) {
                            stat.textContent = value + '%';
                            clearInterval(timer);
                        } else {
                            stat.textContent = current.toFixed(1) + '%';
                        }
                    }, 20);
                } else {
                    stat.textContent = text;
                }
            });
            statsObserver.unobserve(entry.target);
        }
    });
}, { threshold: 0.5 });

const heroStats = document.querySelector('.hero-stats');
if (heroStats) {
    statsObserver.observe(heroStats);
}

// Loading animation
window.addEventListener('load', () => {
    document.body.style.opacity = '0';
    document.body.style.transition = 'opacity 0.5s ease';
    setTimeout(() => {
        document.body.style.opacity = '1';
    }, 100);
});

// Mobile menu toggle (if needed)
const createMobileMenu = () => {
    const nav = document.querySelector('.nav');
    const navLinks = document.querySelector('.nav-links');
    
    if (window.innerWidth <= 768) {
        const menuBtn = document.createElement('button');
        menuBtn.innerHTML = '☰';
        menuBtn.style.cssText = `
            background: none;
            border: none;
            color: white;
            font-size: 24px;
            cursor: pointer;
            display: block;
        `;
        
        menuBtn.addEventListener('click', () => {
            navLinks.style.display = navLinks.style.display === 'flex' ? 'none' : 'flex';
            navLinks.style.flexDirection = 'column';
            navLinks.style.position = 'absolute';
            navLinks.style.top = '60px';
            navLinks.style.right = '24px';
            navLinks.style.background = 'rgba(10, 10, 15, 0.95)';
            navLinks.style.padding = '20px';
            navLinks.style.borderRadius = '12px';
        });
        
        if (!document.querySelector('.mobile-menu-btn')) {
            menuBtn.classList.add('mobile-menu-btn');
            nav.insertBefore(menuBtn, document.querySelector('.btn-nav'));
        }
    }
};

window.addEventListener('resize', createMobileMenu);
createMobileMenu();

// Smooth reveal on page load
const revealElements = document.querySelectorAll('.hero-content, .hero-visual');
revealElements.forEach((el, index) => {
    el.style.opacity = '0';
    el.style.transform = 'translateY(30px)';
    setTimeout(() => {
        el.style.transition = 'opacity 0.8s ease, transform 0.8s ease';
        el.style.opacity = '1';
        el.style.transform = 'translateY(0)';
    }, 200 + (index * 200));
});

// Add hover effect to feature cards
document.querySelectorAll('.feature-card').forEach(card => {
    card.addEventListener('mouseenter', function() {
        this.style.background = 'linear-gradient(135deg, rgba(168, 85, 247, 0.1), rgba(236, 72, 153, 0.1))';
    });
    
    card.addEventListener('mouseleave', function() {
        this.style.background = 'var(--bg-secondary)';
    });
});

// FAQ Accordion
document.querySelectorAll('.faq-question').forEach(question => {
    question.addEventListener('click', () => {
        const faqItem = question.parentElement;
        const isActive = faqItem.classList.contains('active');
        
        // Закрываем все остальные
        document.querySelectorAll('.faq-item').forEach(item => {
            item.classList.remove('active');
        });
        
        // Открываем текущий, если он был закрыт
        if (!isActive) {
            faqItem.classList.add('active');
        }
    });
});

// Переключение вкладок в превью лаунчера
document.querySelectorAll('.sidebar-item').forEach(item => {
    item.addEventListener('click', function() {
        // Убираем active у всех
        document.querySelectorAll('.sidebar-item').forEach(i => i.classList.remove('active'));
        // Добавляем active к текущему
        this.classList.add('active');
    });
});

console.log('Inside Client v3.0.0 - Website loaded successfully! 🚀');


// Обработка кнопок "Купить"
document.querySelectorAll('.pricing-card .btn').forEach(btn => {
    btn.addEventListener('click', function(e) {
        e.preventDefault();
        const card = this.closest('.pricing-card');
        const planName = card.querySelector('h3').textContent;
        
        // Проверяем авторизацию
        const currentUser = JSON.parse(localStorage.getItem('currentUser'));
        
        if (!currentUser) {
            // Если не авторизован - перенаправляем на страницу входа
            if (confirm(`Для покупки тарифа "${planName}" необходимо войти в аккаунт. Перейти на страницу входа?`)) {
                window.location.href = 'auth.html';
            }
        } else {
            // Если авторизован - показываем модальное окно покупки
            showPurchaseModal(planName, card);
        }
    });
});

// Модальное окно покупки
function showPurchaseModal(planName, card) {
    const priceElement = card.querySelector('.amount');
    const price = priceElement ? priceElement.textContent : '0';
    
    const modal = document.createElement('div');
    modal.className = 'purchase-modal';
    modal.innerHTML = `
        <div class="modal-overlay"></div>
        <div class="modal-content">
            <button class="modal-close">&times;</button>
            <h2>Покупка подписки</h2>
            <div class="modal-plan">
                <h3>${planName}</h3>
                <div class="modal-price">
                    <span class="currency">₽</span>
                    <span class="amount">${price}</span>
                    <span class="period">/месяц</span>
                </div>
            </div>
            <div class="payment-methods">
                <h4>Выберите способ оплаты:</h4>
                <button class="payment-btn" data-method="card">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <rect x="2" y="6" width="20" height="12" rx="2" stroke="currentColor" stroke-width="2"/>
                        <path d="M2 10H22" stroke="currentColor" stroke-width="2"/>
                    </svg>
                    Банковская карта
                </button>
                <button class="payment-btn" data-method="qiwi">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
                        <path d="M12 8V12L15 15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                    QIWI
                </button>
                <button class="payment-btn" data-method="crypto">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2"/>
                        <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2"/>
                        <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2"/>
                    </svg>
                    Криптовалюта
                </button>
            </div>
            <p class="modal-note">После оплаты подписка активируется автоматически</p>
        </div>
    `;
    
    document.body.appendChild(modal);
    setTimeout(() => modal.classList.add('show'), 10);
    
    // Закрытие модального окна
    modal.querySelector('.modal-close').addEventListener('click', () => closeModal(modal));
    modal.querySelector('.modal-overlay').addEventListener('click', () => closeModal(modal));
    
    // Обработка выбора способа оплаты
    modal.querySelectorAll('.payment-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const method = btn.dataset.method;
            processPurchase(planName, price, method);
            closeModal(modal);
        });
    });
}

function closeModal(modal) {
    modal.classList.remove('show');
    setTimeout(() => modal.remove(), 300);
}

function processPurchase(plan, price, method) {
    // Имитация процесса оплаты
    showNotification(`Перенаправление на оплату ${plan} через ${method}...`, 'info');
    
    setTimeout(() => {
        // Здесь должна быть реальная интеграция с платежной системой
        if (confirm(`Это демо-версия. Подписка "${plan}" будет активирована для демонстрации. Продолжить?`)) {
            const currentUser = JSON.parse(localStorage.getItem('currentUser'));
            const subscriptionType = plan === 'Премиум' ? 'premium' : plan === 'Альфа' ? 'alpha' : 'free';
            
            currentUser.subscription = subscriptionType;
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            // Обновляем в базе
            const users = JSON.parse(localStorage.getItem('insideUsers')) || [];
            const userIndex = users.findIndex(u => u.id === currentUser.id);
            if (userIndex !== -1) {
                users[userIndex].subscription = subscriptionType;
                localStorage.setItem('insideUsers', JSON.stringify(users));
            }
            
            showNotification(`Подписка "${plan}" успешно активирована!`, 'success');
            
            setTimeout(() => {
                window.location.href = 'dashboard.html';
            }, 2000);
        }
    }, 1500);
}

// Стили для модального окна
const modalStyles = document.createElement('style');
modalStyles.textContent = `
    .purchase-modal {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 10000;
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: opacity 0.3s ease;
    }
    
    .purchase-modal.show {
        opacity: 1;
    }
    
    .modal-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.8);
        backdrop-filter: blur(10px);
    }
    
    .modal-content {
        position: relative;
        background: var(--bg-secondary);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 24px;
        padding: 40px;
        max-width: 500px;
        width: 90%;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
        transform: scale(0.9);
        transition: transform 0.3s ease;
    }
    
    .purchase-modal.show .modal-content {
        transform: scale(1);
    }
    
    .modal-close {
        position: absolute;
        top: 16px;
        right: 16px;
        background: none;
        border: none;
        color: var(--text-secondary);
        font-size: 32px;
        cursor: pointer;
        line-height: 1;
        transition: color 0.3s;
    }
    
    .modal-close:hover {
        color: var(--text-primary);
    }
    
    .modal-content h2 {
        font-size: 28px;
        font-weight: 800;
        margin-bottom: 24px;
        text-align: center;
    }
    
    .modal-plan {
        text-align: center;
        padding: 24px;
        background: rgba(168, 85, 247, 0.1);
        border-radius: 16px;
        margin-bottom: 32px;
    }
    
    .modal-plan h3 {
        font-size: 20px;
        font-weight: 700;
        margin-bottom: 12px;
    }
    
    .modal-price {
        display: flex;
        align-items: baseline;
        justify-content: center;
        gap: 4px;
    }
    
    .modal-price .amount {
        font-size: 40px;
        font-weight: 800;
        color: var(--purple-500);
    }
    
    .payment-methods h4 {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 16px;
    }
    
    .payment-btn {
        width: 100%;
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px 20px;
        background: rgba(255, 255, 255, 0.05);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 12px;
        color: white;
        font-size: 16px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s;
        margin-bottom: 12px;
    }
    
    .payment-btn:hover {
        background: rgba(168, 85, 247, 0.2);
        border-color: var(--purple-500);
        transform: translateX(4px);
    }
    
    .modal-note {
        text-align: center;
        font-size: 13px;
        color: var(--text-tertiary);
        margin-top: 24px;
    }
`;
document.head.appendChild(modalStyles);
