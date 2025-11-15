import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import AnimatedBackground from '../components/AnimatedBackground'
import SponsorsSection from '../components/SponsorsSection'
import { DOWNLOAD_LINKS } from '../utils/constants'
import '../styles/HomePage.css'

export default function HomePage() {
  const [activeTab, setActiveTab] = useState('home')
  const [activeFaq, setActiveFaq] = useState<number | null>(null)
  const [isPreviewExpanded, setIsPreviewExpanded] = useState(false)
  const [isPreviewMinimized, setIsPreviewMinimized] = useState(false)
  const [isPreviewClosed, setIsPreviewClosed] = useState(false)
  const [ramAllocation, setRamAllocation] = useState(4096)
  const [newsRefresh, setNewsRefresh] = useState(0)
  
  // Получаем текущего пользователя для отображения в превью
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')

  // Обновляем новости при переключении на вкладку News
  useEffect(() => {
    if (activeTab === 'news') {
      setNewsRefresh(prev => prev + 1)
    }
  }, [activeTab])

  // Слушаем изменения в localStorage для обновления новостей
  useEffect(() => {
    const handleStorageChange = (e: StorageEvent) => {
      if (e.key === 'insideNews') {
        setNewsRefresh(prev => prev + 1)
      }
    }

    window.addEventListener('storage', handleStorageChange)
    
    // Также проверяем изменения каждые 2 секунды (для изменений в том же окне)
    const interval = setInterval(() => {
      if (activeTab === 'news') {
        setNewsRefresh(prev => prev + 1)
      }
    }, 2000)

    return () => {
      window.removeEventListener('storage', handleStorageChange)
      clearInterval(interval)
    }
  }, [activeTab])

  return (
    <div className="home-page">
      <AnimatedBackground />
      
      {/* Header */}
      <header className="header">
        <nav className="nav">
          <div className="nav-brand">
            <img src="/icon.ico" alt="Inside" width="32" height="32" style={{ borderRadius: '8px' }} />
            <span className="version">v3.1.9</span>
          </div>
          <div className="nav-links">
            <a href="#features">Возможности</a>
            <a href="#pricing">Цены</a>
            <Link to="/news">Новости</Link>
            <a href="#download">Скачать</a>
            <a href="#faq">FAQ</a>
            {!currentUser && <Link to="/auth">Войти</Link>}
          </div>
          {currentUser ? (
            <Link to="/dashboard" className="btn-nav">
              <img 
                src={currentUser.avatar || '/icon.ico'} 
                alt="Avatar" 
                style={{ 
                  width: '24px', 
                  height: '24px', 
                  borderRadius: '50%', 
                  marginRight: '8px',
                  objectFit: 'cover'
                }} 
              />
              {currentUser.username}
            </Link>
          ) : (
            <Link to="/auth" className="btn-nav">Личный кабинет</Link>
          )}
        </nav>
      </header>

      {/* Hero Section */}
      <section className="hero">
        <div className="hero-content">
          <div className="hero-badge">
            <span className="badge-dot"></span>
            Версия 3.1.9 уже доступна
          </div>
          <h1 className="hero-title">
            Добро пожаловать в<br />
            <span className="gradient-text">Inside Client</span>
          </h1>
          <p className="hero-subtitle">
            Клиент для Minecraft 1.20.1 с лучшими обходами,<br />
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
        
        {/* Hero Visual - Точная копия лаунчера */}
        <div className="hero-visual">
          {!isPreviewClosed && (
            <div className={`launcher-preview ${isPreviewExpanded ? 'expanded' : ''} ${isPreviewMinimized ? 'minimized' : ''}`}>
              {/* Title Bar */}
              <div 
                className="launcher-titlebar"
                onClick={() => isPreviewMinimized && setIsPreviewMinimized(false)}
                style={{ cursor: isPreviewMinimized ? 'pointer' : 'default' }}
              >
                <div className="titlebar-left">
                  <svg className="app-logo-icon" width="20" height="20" viewBox="0 0 24 24" fill="none">
                    <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="url(#gradient1)"/>
                    <path d="M2 17L12 22L22 17V7L12 12L2 7V17Z" fill="url(#gradient2)"/>
                    <defs>
                      <linearGradient id="gradient1" x1="2" y1="2" x2="22" y2="12">
                        <stop offset="0%" stopColor="#8A4BFF"/>
                        <stop offset="100%" stopColor="#FF6B9D"/>
                      </linearGradient>
                      <linearGradient id="gradient2" x1="2" y1="7" x2="22" y2="22">
                        <stop offset="0%" stopColor="#6C37D7"/>
                        <stop offset="100%" stopColor="#8A4BFF"/>
                      </linearGradient>
                    </defs>
                  </svg>
                  <div className="app-logo">INSIDE</div>
                  <div className="app-version">v3.1.9</div>
                </div>
                <div className="titlebar-right">
                  <div 
                    className="title-btn expand-btn" 
                    onClick={() => setIsPreviewExpanded(!isPreviewExpanded)}
                    title={isPreviewExpanded ? "Уменьшить" : "Развернуть"}
                  >
                    {isPreviewExpanded ? (
                      <svg width="10" height="10" viewBox="0 0 14 14" fill="currentColor">
                        <path d="M0 0H5V2H2V5H0V0ZM12 0V5H14V0H12ZM12 2V0H14V2H12ZM0 9V14H5V12H2V9H0ZM14 9H12V12H9V14H14V9Z"/>
                      </svg>
                    ) : (
                      <svg width="10" height="10" viewBox="0 0 14 14" fill="currentColor">
                        <path d="M2 0H0V5H2V2H5V0H2ZM12 0V2H9V0H12ZM14 0H12V2H14V0ZM0 9H2V12H5V14H0V9ZM12 12V14H14V9H12V12Z"/>
                      </svg>
                    )}
                  </div>
                  <div 
                    className="title-btn minimize-btn" 
                    onClick={() => setIsPreviewMinimized(!isPreviewMinimized)}
                    title={isPreviewMinimized ? "Развернуть" : "Свернуть"}
                  >
                    <svg width="10" height="2" viewBox="0 0 14 2" fill="currentColor">
                      <rect width="14" height="2"/>
                    </svg>
                  </div>
                  <div 
                    className="title-btn close-btn" 
                    onClick={() => setIsPreviewClosed(true)}
                    title="Закрыть"
                  >
                    <svg width="10" height="10" viewBox="0 0 14 14" fill="currentColor">
                      <path d="M14 1.41L12.59 0L7 5.59L1.41 0L0 1.41L5.59 7L0 12.59L1.41 14L7 8.41L12.59 14L14 12.59L8.41 7L14 1.41Z"/>
                    </svg>
                  </div>
                </div>
              </div>

            {/* Main Content */}
            <div className="launcher-main">
              {/* Sidebar */}
              <div className="launcher-sidebar">
                <div 
                  className={`launcher-nav-item ${activeTab === 'home' ? 'active' : ''}`}
                  onClick={() => setActiveTab('home')}
                >
                  <svg className="nav-icon" width="16" height="16" viewBox="0 0 20 20" fill="currentColor">
                    <path d="M10 2L2 8V18C2 18.5304 2.21071 19.0391 2.58579 19.4142C2.96086 19.7893 3.46957 20 4 20H16C16.5304 20 17.0391 19.7893 17.4142 19.4142C17.7893 19.0391 18 18.5304 18 18V8L10 2Z"/>
                    <path d="M8 20V12H12V20" fill="currentColor"/>
                  </svg>
                  <span>Главная</span>
                </div>
                <div 
                  className={`launcher-nav-item ${activeTab === 'profile' ? 'active' : ''}`}
                  onClick={() => setActiveTab('profile')}
                >
                  <svg className="nav-icon" width="16" height="16" viewBox="0 0 20 20" fill="currentColor">
                    <circle cx="10" cy="6" r="4"/>
                    <path d="M10 12C5.58172 12 2 14.6863 2 18H18C18 14.6863 14.4183 12 10 12Z"/>
                  </svg>
                  <span>Профиль</span>
                </div>
                <div 
                  className={`launcher-nav-item ${activeTab === 'settings' ? 'active' : ''}`}
                  onClick={() => setActiveTab('settings')}
                >
                  <svg className="nav-icon" width="16" height="16" viewBox="0 0 20 20" fill="currentColor">
                    <path d="M17.43 10.98c.04-.32.07-.64.07-.98 0-.34-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C12.46 2.18 12.25 2 12 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98 0 .33.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM10 13c-1.65 0-3-1.35-3-3s1.35-3 3-3 3 1.35 3 3-1.35 3-3 3z"/>
                  </svg>
                  <span>Настройки</span>
                </div>
                <div 
                  className={`launcher-nav-item ${activeTab === 'news' ? 'active' : ''}`}
                  onClick={() => setActiveTab('news')}
                >
                  <svg className="nav-icon" width="16" height="16" viewBox="0 0 20 20" fill="currentColor">
                    <path d="M2 4C2 2.89543 2.89543 2 4 2H16C17.1046 2 18 2.89543 18 4V16C18 17.1046 17.1046 18 16 18H4C2.89543 18 2 17.1046 2 16V4Z"/>
                    <rect x="5" y="5" width="6" height="4" fill="#0A0A0F"/>
                    <rect x="5" y="11" width="10" height="1" fill="#0A0A0F"/>
                    <rect x="5" y="14" width="10" height="1" fill="#0A0A0F"/>
                  </svg>
                  <span>Новости</span>
                </div>
              </div>

              {/* Content Area */}
              <div className="launcher-content">
                {activeTab === 'home' && (
                  <div className="launcher-page">
                    <div className="page-header">
                      <h1>Добро пожаловать в Inside Client</h1>
                      <p>Приятной игры</p>
                    </div>
                    <div className="client-card">
                      <div className="client-info">
                        <div className="client-version">
                          <span className="version-number">1.20.1</span>
                          <span className="version-label">Клиент</span>
                        </div>
                        <div className="client-description">
                          <h3>Inside Client</h3>
                          <ul className="feature-list">
                            <li>Все лучшие функции читов в одном месте</li>
                            <li>Оптимизированный клиент</li>
                            <li>Поддержка новых версий игры</li>
                            <li>Автообновления</li>
                            <li>Безопасность - нам не нужна плохая репутация</li> 
                          </ul>
                        </div>
                      </div>
                    </div>
                    <button className="launcher-launch-btn">
                      <svg width="16" height="16" viewBox="0 0 20 20" fill="none">
                        <path d="M5 4.5L15 10L5 15.5V4.5Z" fill="currentColor"/>
                      </svg>
                      Launch
                    </button>
                  </div>
                )}

                {activeTab === 'profile' && (
                  <div className="launcher-page">
                    <div className="page-header">
                      <h1>Profile</h1>
                      <p>Info for your account</p>
                    </div>
                    <div className="profile-card">
                      <div className="profile-avatar-container">
                        <div className="profile-avatar">
                          {currentUser?.avatar ? (
                            <img src={currentUser.avatar} alt="Avatar" />
                          ) : (
                            <svg className="avatar-placeholder" width="48" height="48" viewBox="0 0 60 60" fill="currentColor">
                              <circle cx="30" cy="20" r="12"/>
                              <path d="M30 35C18 35 10 40 10 50H50C50 40 42 35 30 35Z"/>
                            </svg>
                          )}
                        </div>
                      </div>
                      <div className="profile-info">
                        <div className="profile-field">
                          <label>Nickname:</label>
                          <span className="profile-value">{currentUser?.username || 'Player'}</span>
                        </div>
                        <div className="profile-field">
                          <label>UID:</label>
                          <span className="uid-value">AZ-2024-001</span>
                        </div>
                        <div className="profile-field">
                          <label>Status:</label>
                          <span className="status-active">Активен</span>
                        </div>
                        <div className="profile-field">
                          <label>Subscription:</label>
                          <span className={`subscription-${currentUser?.subscription || 'free'}`}>
                            {currentUser?.subscription === 'premium' ? 'Premium' : 
                             currentUser?.subscription === 'alpha' ? 'Alpha' : 'Free'}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                )}

                {activeTab === 'settings' && (
                  <div className="launcher-page">
                    <div className="page-header">
                      <h1>Settings</h1>
                      <p>Launcher and game configuration</p>
                    </div>
                    <div className="settings-grid">
                      <div className="settings-section">
                        <h3>Game</h3>
                        <div className="setting-item">
                          <label className="installation-label">Installation path:</label>
                          <div className="path-input">
                            <input type="text" value="C:\Arizon" readOnly />
                            <button className="browse-btn">Обзор</button>
                          </div>
                        </div>
                        <div className="setting-item">
                          <label>RAM allocation:</label>
                          <div className="ram-slider">
                            <input 
                              type="range" 
                              min="1024" 
                              max="16384" 
                              step="512" 
                              value={ramAllocation}
                              onChange={(e) => setRamAllocation(Number(e.target.value))}
                            />
                            <span className="ram-value">{(ramAllocation / 1024).toFixed(1)} GB</span>
                          </div>
                        </div>
                      </div>
                      <div className="settings-section">
                        <h3>Launcher</h3>
                        <div className="setting-item">
                          <label>Auto-update:</label>
                          <div className="toggle-switch">
                            <input type="checkbox" defaultChecked />
                            <span className="toggle-slider"></span>
                          </div>
                        </div>
                        <div className="setting-item">
                          <label>Interface sounds:</label>
                          <div className="toggle-switch">
                            <input type="checkbox" defaultChecked />
                            <span className="toggle-slider"></span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                )}

                {activeTab === 'news' && (
                  <div className="launcher-page" key={newsRefresh}>
                    <div className="page-header">
                      <h1>News</h1>
                      <p>Latest Inside Client Updates</p>
                    </div>
                    <div className="news-container">
                      {(() => {
                        try {
                          const allNews = JSON.parse(localStorage.getItem('insideNews') || '[]')
                          const launcherNews = allNews.filter((n: any) => n.type === 'launcher')
                          
                          if (launcherNews.length === 0) {
                            return (
                              <>
                                <article className="news-item">
                                  <div className="news-date">November 14, 2025</div>
                                  <h3>Inside Client 2.0 Release</h3>
                                  <p>A new Electron-based launcher with an improved interface and performance.</p>
                                </article>
                                <article className="news-item">
                                  <div className="news-date">November 10, 2025</div>
                                  <h3>Optimization update</h3>
                                  <p>Improved client performance, added new graphics settings.</p>
                                </article>
                              </>
                            )
                          }
                          
                          return launcherNews.slice(0, 5).map((news: any) => (
                            <article key={news.id} className="news-item">
                              <div className="news-date">{new Date(news.date).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })}</div>
                              <h3>{news.title}</h3>
                              <p>{news.content}</p>
                            </article>
                          ))
                        } catch (error) {
                          console.error('Error loading news:', error)
                          return (
                            <article className="news-item">
                              <div className="news-date">November 14, 2025</div>
                              <h3>Inside Client 2.0 Release</h3>
                              <p>A new Electron-based launcher with an improved interface and performance.</p>
                            </article>
                          )
                        }
                      })()}
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
          )}
          
          {/* Кнопка восстановления закрытого окна */}
          {isPreviewClosed && (
            <div className="preview-restore-hint">
              <p>Предпросмотр лаунчера скрыт</p>
              <button 
                className="btn btn-secondary btn-small" 
                onClick={() => {
                  setIsPreviewClosed(false)
                  setIsPreviewMinimized(false)
                }}
              >
                Показать снова
              </button>
            </div>
          )}
        </div>
      </section>

      {/* Sponsors Marquee Section */}
      <SponsorsSection />

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
              { 
                icon: 'shield', 
                title: 'Лучшие обходы', 
                desc: 'Непробиваемые обходы античитов с постоянными обновлениями',
                svg: <path d="M24 4L40 12V28C40 35.732 33.732 42 26 42H22C14.268 42 8 35.732 8 28V12L24 4Z" stroke="currentColor" strokeWidth="2"/>
              },
              { 
                icon: 'zap', 
                title: 'Высокая производительность', 
                desc: 'Оптимизированный код без просадок FPS',
                svg: <path d="M26 4L14 24H24L22 44L34 24H24L26 4Z" fill="currentColor"/>
              },
              { 
                icon: 'monitor', 
                title: 'Стильный интерфейс', 
                desc: 'Современный GUI с темами и настройками',
                svg: <><rect x="6" y="8" width="36" height="26" rx="2" stroke="currentColor" strokeWidth="2"/><path d="M6 30H42" stroke="currentColor" strokeWidth="2"/><path d="M18 40H30" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/><path d="M24 34V40" stroke="currentColor" strokeWidth="2"/></>
              },
              { 
                icon: 'list', 
                title: 'Богатый функционал', 
                desc: 'Более 100 модулей для всех аспектов игры',
                svg: <><rect x="10" y="10" width="28" height="4" rx="1" fill="currentColor"/><rect x="10" y="18" width="28" height="4" rx="1" fill="currentColor"/><rect x="10" y="26" width="28" height="4" rx="1" fill="currentColor"/><rect x="10" y="34" width="20" height="4" rx="1" fill="currentColor"/></>
              },
              { 
                icon: 'clock', 
                title: 'Регулярные обновления', 
                desc: 'Еженедельные обновления с новыми функциями',
                svg: <><circle cx="24" cy="24" r="18" stroke="currentColor" strokeWidth="2"/><path d="M24 10V24L32 32" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/></>
              },
              { 
                icon: 'user', 
                title: 'Поддержка 24/7', 
                desc: 'Круглосуточная помощь и активное сообщество',
                svg: <><circle cx="24" cy="16" r="8" fill="currentColor"/><path d="M10 42C10 33.163 16.268 26 24 26C31.732 26 38 33.163 38 42" fill="currentColor"/></>
              }
            ].map((feature, i) => (
              <div key={i} className="feature-card">
                <div className="feature-icon">
                  <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                    {feature.svg}
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
              {currentUser ? (
                <a href={DOWNLOAD_LINKS.free} className="btn btn-outline" download>Скачать бесплатно</a>
              ) : (
                <Link to="/auth" className="btn btn-outline">Скачать бесплатно</Link>
              )}
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
              {currentUser ? (
                <Link to="/dashboard" className="btn btn-primary">Купить премиум</Link>
              ) : (
                <Link to="/auth" className="btn btn-primary">Купить премиум</Link>
              )}
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
                  Лучшие функции и новые
                </li>
              </ul>
              {currentUser ? (
                <Link to="/dashboard" className="btn btn-gradient">Купить альфа</Link>
              ) : (
                <Link to="/auth" className="btn btn-gradient">Купить альфа</Link>
              )}
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
              Скачать Inside Client v3.1.9
            </a>
            <div className="download-info">
              <span>Windows 10/11 • Minecraft 1.20.1</span>
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
                <div className="version">v3.1.9</div>
              </div>
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
            <p>&copy; 2025 Inside Client. Все права защищены.</p>
          </div>
        </div>
      </footer>
    </div>
  )
}
