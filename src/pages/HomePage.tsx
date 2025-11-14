import { useState } from 'react'
import { Link } from 'react-router-dom'
import AnimatedBackground from '../components/AnimatedBackground'
import '../styles/HomePage.css'

export default function HomePage() {
  const [activeTab, setActiveTab] = useState('home')
  const [activeFaq, setActiveFaq] = useState<number | null>(null)

  return (
    <div className="home-page">
      <AnimatedBackground />
      
      {/* Header */}
      <header className="header">
        <nav className="nav">
          <div className="nav-brand">
            <img src="/icon.ico" alt="Inside" width="32" height="32" style={{ borderRadius: '8px' }} />
            <span className="brand-name">INSIDE</span>
            <span className="version">v3.0.0</span>
          </div>
          <div className="nav-links">
            <a href="#features">Возможности</a>
            <a href="#pricing">Цены</a>
            <a href="#download">Скачать</a>
            <a href="#faq">FAQ</a>
            <Link to="/auth">Войти</Link>
          </div>
          <Link to="/auth" className="btn-nav">Личный кабинет</Link>
        </nav>
      </header>

      {/* Hero Section */}
      <section className="hero">
        <div className="hero-content">
          <div className="hero-badge">
            <span className="badge-dot"></span>
            Версия 3.0.0 уже доступна
          </div>
          <h1 className="hero-title">
            Добро пожаловать в<br />
            <span className="gradient-text">Inside Client</span>
          </h1>
          <p className="hero-subtitle">
            Продвинутый клиент для Minecraft 1.20.1 с лучшими обходами,<br />
            оптимизацией производительности и современным интерфейсом
          </p>
          <div className="hero-buttons">
            <a href="#pricing" className="btn btn-primary">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M10 3V17M10 17L16 11M10 17L4 11" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              </svg>
              Скачать клиент
            </a>
            <a href="#features" className="btn btn-secondary">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M10 14L16 8M16 8H10M16 8V14" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              </svg>
              Узнать больше
            </a>
          </div>
          <div className="hero-stats">
            <div className="stat">
              <div className="stat-value">50K+</div>
              <div className="stat-label">Активных игроков</div>
            </div>
            <div className="stat">
              <div className="stat-value">99.9%</div>
              <div className="stat-label">Время работы</div>
            </div>
            <div className="stat">
              <div className="stat-value">24/7</div>
              <div className="stat-label">Поддержка</div>
            </div>
          </div>
        </div>
        
        {/* Hero Visual - ИСПРАВЛЕНО: теперь переключатели работают */}
        <div className="hero-visual">
          <div className="client-preview">
            <div className="preview-header">
              <div className="preview-dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
              <div className="preview-title">Inside Client</div>
            </div>
            <div className="preview-content">
              <div className="preview-sidebar">
                <div 
                  className={`sidebar-item ${activeTab === 'home' ? 'active' : ''}`}
                  onClick={() => setActiveTab('home')}
                >
                  Главная
                </div>
                <div 
                  className={`sidebar-item ${activeTab === 'profile' ? 'active' : ''}`}
                  onClick={() => setActiveTab('profile')}
                >
                  Профиль
                </div>
                <div 
                  className={`sidebar-item ${activeTab === 'settings' ? 'active' : ''}`}
                  onClick={() => setActiveTab('settings')}
                >
                  Настройки
                </div>
                <div 
                  className={`sidebar-item ${activeTab === 'news' ? 'active' : ''}`}
                  onClick={() => setActiveTab('news')}
                >
                  Новости
                </div>
              </div>
              <div className="preview-main">
                {activeTab === 'home' && (
                  <>
                    <img src="/icon.ico" alt="Inside" className="preview-logo" width="64" height="64" />
                    <div className="version-badge">1.20.1</div>
                    <div className="client-title">Inside Client</div>
                    <div className="feature-list">
                      <div className="feature-item">Оптимизация производительности</div>
                      <div className="feature-item">Улучшенная графика</div>
                      <div className="feature-item">Встроенные моды</div>
                      <div className="feature-item">Автообновления</div>
                    </div>
                    <div className="launch-button">▶ ЗАПУСТИТЬ</div>
                  </>
                )}
                {activeTab === 'profile' && (
                  <div className="tab-content">
                    <h3>Профиль</h3>
                    <div className="profile-info">
                      <div className="info-item">
                        <span>Имя:</span>
                        <span>Player</span>
                      </div>
                      <div className="info-item">
                        <span>Подписка:</span>
                        <span className="badge-premium">Premium</span>
                      </div>
                      <div className="info-item">
                        <span>Статус:</span>
                        <span className="badge-active">Активен</span>
                      </div>
                    </div>
                  </div>
                )}
                {activeTab === 'settings' && (
                  <div className="tab-content">
                    <h3>Настройки</h3>
                    <div className="settings-list">
                      <div className="setting-row">
                        <span>Автообновление</span>
                        <div className="toggle-switch active"></div>
                      </div>
                      <div className="setting-row">
                        <span>Уведомления</span>
                        <div className="toggle-switch active"></div>
                      </div>
                      <div className="setting-row">
                        <span>Темная тема</span>
                        <div className="toggle-switch active"></div>
                      </div>
                    </div>
                  </div>
                )}
                {activeTab === 'news' && (
                  <div className="tab-content">
                    <h3>Новости</h3>
                    <div className="news-list">
                      <div className="news-item">
                        <div className="news-date">14.11.2025</div>
                        <div className="news-title">Обновление 3.0.0</div>
                      </div>
                      <div className="news-item">
                        <div className="news-date">10.11.2025</div>
                        <div className="news-title">Новые обходы</div>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="features">
        <div className="container">
          <div className="section-header">
            <span className="section-badge">Возможности</span>
            <h2>Почему выбирают <span className="gradient-text">Inside</span>?</h2>
            <p>Максимальная производительность и надежность для вашей игры</p>
          </div>
          <div className="features-grid">
            {[
              { icon: 'shield', title: 'Лучшие обходы', desc: 'Непробиваемые обходы античитов с постоянными обновлениями' },
              { icon: 'layers', title: 'Высокая производительность', desc: 'Оптимизированный код без просадок FPS' },
              { icon: 'layout', title: 'Стильный интерфейс', desc: 'Современный GUI с темами и настройками' },
              { icon: 'grid', title: 'Богатый функционал', desc: 'Более 100 модулей для всех аспектов игры' },
              { icon: 'clock', title: 'Регулярные обновления', desc: 'Еженедельные обновления с новыми функциями' },
              { icon: 'support', title: 'Поддержка 24/7', desc: 'Круглосуточная помощь и активное сообщество' }
            ].map((feature, i) => (
              <div key={i} className="feature-card">
                <div className="feature-icon">
                  <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                    <path d="M24 4L40 12V28C40 35.732 33.732 42 26 42H22C14.268 42 8 35.732 8 28V12L24 4Z" stroke="currentColor" strokeWidth="2"/>
                  </svg>
                </div>
                <h3>{feature.title}</h3>
                <p>{feature.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Pricing Section - ИСПРАВЛЕНО: бесплатная версия без карты */}
      <section id="pricing" className="pricing">
        <div className="container">
          <div className="section-header">
            <span className="section-badge">Тарифы</span>
            <h2>Выберите свой <span className="gradient-text">план</span></h2>
            <p>Гибкие тарифы для любых потребностей</p>
          </div>
          <div className="pricing-grid">
            {/* Free - БЕЗ ТРЕБОВАНИЯ КАРТЫ */}
            <div className="pricing-card">
              <div className="pricing-header">
                <h3>Бесплатная версия</h3>
                <div className="price">
                  <span className="currency">₽</span>
                  <span className="amount">0</span>
                  <span className="period">/навсегда</span>
                </div>
              </div>
              <ul className="pricing-features">
                <li className="feature-item">
                  <svg className="check" width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M16 6L8 14L4 10" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                  Базовый функционал
                </li>
                <li className="feature-item">
                  <svg className="check" width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M16 6L8 14L4 10" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                  Регулярные обновления
                </li>
                <li className="feature-item disabled">
                  <svg className="cross" width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M6 6L14 14M14 6L6 14" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                  Премиум модули
                </li>
              </ul>
              <Link to="/auth" className="btn btn-outline">Скачать бесплатно</Link>
            </div>

            {/* Premium */}
            <div className="pricing-card featured">
              <div className="popular-badge">Популярный</div>
              <div className="pricing-header">
                <h3>Премиум</h3>
                <div className="price">
                  <span className="currency">₽</span>
                  <span className="amount">299</span>
                  <span className="period">/месяц</span>
                </div>
              </div>
              <ul className="pricing-features">
                <li className="feature-item">
                  <svg className="check" width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M16 6L8 14L4 10" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                  Полный функционал
                </li>
                <li className="feature-item">
                  <svg className="check" width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M16 6L8 14L4 10" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                  Приоритетная поддержка
                </li>
              </ul>
              <Link to="/auth" className="btn btn-primary">Купить премиум</Link>
            </div>

            {/* Alpha */}
            <div className="pricing-card">
              <div className="pricing-header">
                <h3>Альфа</h3>
                <div className="price">
                  <span className="currency">₽</span>
                  <span className="amount">599</span>
                  <span className="period">/месяц</span>
                </div>
              </div>
              <ul className="pricing-features">
                <li className="feature-item">
                  <svg className="check" width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M16 6L8 14L4 10" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                  Ранний доступ
                </li>
                <li className="feature-item">
                  <svg className="check" width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M16 6L8 14L4 10" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                  Лучшие обходы
                </li>
              </ul>
              <Link to="/auth" className="btn btn-gradient">Купить альфа</Link>
            </div>
          </div>
        </div>
      </section>

      {/* Download Section */}
      <section id="download" className="download">
        <div className="container">
          <div className="download-content">
            <h2>Готовы начать?</h2>
            <p>Присоединяйтесь к тысячам игроков уже сейчас</p>
            <a href="#pricing" className="btn btn-large">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                <path d="M12 4V20M12 20L18 14M12 20L6 14" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              </svg>
              Скачать Inside Client v3.0.0
            </a>
            <div className="download-info">
              <span>Windows 10/11 • Minecraft 1.20.1 • 25 МБ</span>
            </div>
          </div>
        </div>
      </section>

      {/* FAQ Section */}
      <section id="faq" className="faq">
        <div className="container">
          <div className="section-header">
            <span className="section-badge">FAQ</span>
            <h2>Часто задаваемые <span className="gradient-text">вопросы</span></h2>
            <p>Ответы на популярные вопросы о Inside Client</p>
          </div>
          <div className="faq-grid">
            {[
              { q: 'Как установить Inside Client?', a: 'Скачайте лаунчер с нашего сайта, запустите его и следуйте инструкциям.' },
              { q: 'Безопасен ли Inside Client?', a: 'Да, абсолютно безопасен! Мы используем современные методы защиты.' },
              { q: 'Какие версии Minecraft поддерживаются?', a: 'В данный момент поддерживается Minecraft 1.20.1.' },
              { q: 'Можно ли использовать на серверах?', a: 'Да, Inside Client имеет продвинутые обходы античитов.' },
              { q: 'Как получить поддержку?', a: 'Вы можете обратиться в наш Discord или Telegram.' },
              { q: 'Чем отличаются тарифы?', a: 'Бесплатная версия имеет базовый функционал. Премиум дает полный доступ.' }
            ].map((faq, i) => (
              <div key={i} className={`faq-item ${activeFaq === i ? 'active' : ''}`}>
                <div className="faq-question" onClick={() => setActiveFaq(activeFaq === i ? null : i)}>
                  <h3>{faq.q}</h3>
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                    <path d="M19 9L12 16L5 9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                  </svg>
                </div>
                <div className="faq-answer">
                  <p>{faq.a}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="footer">
        <div className="container">
          <div className="footer-content">
            <div className="footer-brand">
              <div className="logo-section">
                <div className="brand-name">INSIDE</div>
                <div className="version">v3.0.0</div>
              </div>
              <p>Лучший клиент для Minecraft с передовыми технологиями</p>
            </div>
            <div className="footer-links">
              <div className="footer-column">
                <h4>Продукт</h4>
                <a href="#features">Возможности</a>
                <a href="#pricing">Цены</a>
                <a href="#download">Скачать</a>
              </div>
              <div className="footer-column">
                <h4>Аккаунт</h4>
                <Link to="/auth">Войти</Link>
                <Link to="/dashboard">Личный кабинет</Link>
              </div>
            </div>
          </div>
          <div className="footer-bottom">
            <p>&copy; 2024 Inside Client. Все права защищены.</p>
          </div>
        </div>
      </footer>
    </div>
  )
}
