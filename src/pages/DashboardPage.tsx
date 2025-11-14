import { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import AnimatedBackground from '../components/AnimatedBackground'
import Notification from '../components/Notification'
import { getCurrentUser, setCurrentUser, Database } from '../utils/database'
import { User, NotificationType } from '../types'
import '../styles/DashboardPage.css'

type PageType = 'home' | 'profile' | 'settings'

export default function DashboardPage() {
  const [currentPage, setCurrentPage] = useState<PageType>('home')
  const [user, setUser] = useState<User | null>(null)
  const [notification, setNotification] = useState<{ message: string; type: NotificationType } | null>(null)
  const [avatarPreview, setAvatarPreview] = useState<string | null>(null)
  const navigate = useNavigate()

  useEffect(() => {
    const userData = getCurrentUser()
    if (!userData) {
      // Проверяем, не был ли пользователь авторизован ранее
      const rememberMe = localStorage.getItem('rememberMe')
      if (!rememberMe) {
        navigate('/auth')
      }
    } else {
      setUser(userData)
      if (userData.avatar) {
        setAvatarPreview(userData.avatar)
      }
    }
  }, [navigate])

  const handleLogout = () => {
    if (confirm('Вы уверены, что хотите выйти?')) {
      setCurrentUser(null)
      navigate('/auth')
    }
  }

  const handleAvatarUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file && user) {
      const reader = new FileReader()
      reader.onload = async (event) => {
        const avatarUrl = event.target?.result as string
        setAvatarPreview(avatarUrl)
        
        const updatedUser = { ...user, avatar: avatarUrl }
        setUser(updatedUser)
        setCurrentUser(updatedUser)
        
        const db = new Database()
        await db.updateUser(user.id, { avatar: avatarUrl })
        
        setNotification({ message: 'Аватарка загружена!', type: 'success' })
      }
      reader.readAsDataURL(file)
    }
  }

  const handleSaveSettings = async () => {
    if (!user) return

    const notifications = (document.getElementById('settingNotifications') as HTMLInputElement)?.checked
    const autoUpdate = (document.getElementById('settingAutoUpdate') as HTMLInputElement)?.checked
    const theme = (document.getElementById('settingTheme') as HTMLSelectElement)?.value as 'dark' | 'light' | 'auto'
    const language = (document.getElementById('settingLanguage') as HTMLSelectElement)?.value as 'ru' | 'en' | 'uk'

    const settings = {
      notifications,
      autoUpdate,
      theme,
      language
    }

    const updatedUser = { ...user, settings }
    setUser(updatedUser)
    setCurrentUser(updatedUser)

    const db = new Database()
    await db.updateUser(user.id, { settings })

    setNotification({ message: 'Настройки сохранены!', type: 'success' })
  }

  if (!user) return null

  const subscriptionNames = {
    free: 'Бесплатная',
    premium: 'Премиум',
    alpha: 'Альфа'
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('ru-RU', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  }

  return (
    <div className="dashboard-page">
      <AnimatedBackground />
      
      {notification && (
        <Notification
          message={notification.message}
          type={notification.type}
          onClose={() => setNotification(null)}
        />
      )}

      <div className="dashboard-container">
        {/* Sidebar */}
        <aside className="dashboard-sidebar">
          <div className="sidebar-header">
            <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
              <rect width="40" height="40" rx="10" fill="url(#gradient)"/>
              <path d="M20 10L30 20L20 30L10 20L20 10Z" fill="white"/>
              <defs>
                <linearGradient id="gradient" x1="0" y1="0" x2="40" y2="40">
                  <stop offset="0%" stopColor="#A855F7"/>
                  <stop offset="100%" stopColor="#EC4899"/>
                </linearGradient>
              </defs>
            </svg>
            <div>
              <div className="brand">INSIDE</div>
              <div className="version">v3.0.0</div>
            </div>
          </div>

          <nav className="sidebar-nav">
            <Link to="/" className="nav-item" title="Вернуться на главную страницу сайта">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M3 7L10 2L17 7V17C17 17.5523 16.5523 18 16 18H4C3.44772 18 3 17.5523 3 17V7Z" stroke="currentColor" strokeWidth="2"/>
                <path d="M7 18V10H13V18" stroke="currentColor" strokeWidth="2"/>
              </svg>
              На сайт
            </Link>
            {user?.isAdmin && (
              <Link to="/admin" className="nav-item admin-link" title="Админ-панель">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                  <path d="M10 2L2 7L10 12L18 7L10 2Z" stroke="currentColor" strokeWidth="2"/>
                  <path d="M2 12L10 17L18 12" stroke="currentColor" strokeWidth="2"/>
                </svg>
                Админ-панель
              </Link>
            )}
            <button
              className={`nav-item ${currentPage === 'home' ? 'active' : ''}`}
              onClick={() => setCurrentPage('home')}
            >
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <rect x="3" y="4" width="14" height="12" rx="2" stroke="currentColor" strokeWidth="2"/>
                <path d="M7 8H13M7 12H10" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              </svg>
              Кабинет
            </button>
            <button
              className={`nav-item ${currentPage === 'profile' ? 'active' : ''}`}
              onClick={() => setCurrentPage('profile')}
            >
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M16 17V15C16 13.9391 15.5786 12.9217 14.8284 12.1716C14.0783 11.4214 13.0609 11 12 11H8C6.93913 11 5.92172 11.4214 5.17157 12.1716C4.42143 12.9217 4 13.9391 4 15V17" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                <circle cx="10" cy="5" r="3" stroke="currentColor" strokeWidth="2"/>
              </svg>
              Профиль
            </button>
            <button
              className={`nav-item ${currentPage === 'settings' ? 'active' : ''}`}
              onClick={() => setCurrentPage('settings')}
            >
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <circle cx="10" cy="10" r="3" stroke="currentColor" strokeWidth="2"/>
                <path d="M10 1V3M10 17V19M19 10H17M3 10H1M16.364 16.364L14.95 14.95M5.05 5.05L3.636 3.636M16.364 3.636L14.95 5.05M5.05 14.95L3.636 16.364" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              </svg>
              Настройки
            </button>
          </nav>

          <div className="sidebar-footer">
            <button className="logout-btn" onClick={handleLogout}>
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                <path d="M11 3H4C3.44772 3 3 3.44772 3 4V16C3 16.5523 3.44772 17 4 17H11" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                <path d="M13 13L17 10L13 7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                <path d="M17 10H7" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              </svg>
              Выйти
            </button>
          </div>
        </aside>

        {/* Main Content */}
        <main className="dashboard-main">
          {/* Home Page */}
          {currentPage === 'home' && (
            <div className="page active">
              <div className="page-header">
                <h1>Добро пожаловать, {user.username}!</h1>
                <p>Управляйте своим аккаунтом Inside Client</p>
              </div>

              <div className="stats-grid">
                <div className="stat-card">
                  <div className="stat-icon">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                      <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                      <path d="M2 17L12 22L22 17" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                      <path d="M2 12L12 17L22 12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                  </div>
                  <div className="stat-info">
                    <div className="stat-label">Подписка</div>
                    <div className="stat-value">{subscriptionNames[user.subscription]}</div>
                  </div>
                </div>

                <div className="stat-card">
                  <div className="stat-icon">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                      <rect x="3" y="4" width="18" height="16" rx="2" stroke="currentColor" strokeWidth="2"/>
                      <path d="M7 4V2M17 4V2M3 10H21" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                    </svg>
                  </div>
                  <div className="stat-info">
                    <div className="stat-label">Дата регистрации</div>
                    <div className="stat-value">{formatDate(user.registeredAt)}</div>
                  </div>
                </div>

                <div className="stat-card">
                  <div className="stat-icon">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                      <path d="M12 2V6M12 18V22M6 12H2M22 12H18" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                      <circle cx="12" cy="12" r="4" stroke="currentColor" strokeWidth="2"/>
                    </svg>
                  </div>
                  <div className="stat-info">
                    <div className="stat-label">Статус</div>
                    <div className="stat-value">
                      <span className="status-badge active">Активен</span>
                    </div>
                  </div>
                </div>
              </div>

              <div className="quick-actions">
                <h2>Быстрые действия</h2>
                <div className="actions-grid">
                  <Link to="/#download" className="action-card">
                    <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
                      <path d="M16 6V20M16 20L22 14M16 20L10 14" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                      <path d="M6 24V25C6 25.5304 6.21071 26.0391 6.58579 26.4142C6.96086 26.7893 7.46957 27 8 27H24C24.5304 27 25.0391 26.7893 25.4142 26.4142C25.7893 26.0391 26 25.5304 26 25V24" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                    </svg>
                    <h3>Скачать клиент</h3>
                    <p>Загрузите последнюю версию</p>
                  </Link>

                  <Link to="/#pricing" className="action-card">
                    <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
                      <path d="M16 4L28 10V22L16 28L4 22V10L16 4Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                      <path d="M16 16V20M16 12H16.01" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                    </svg>
                    <h3>Улучшить подписку</h3>
                    <p>Получите больше возможностей</p>
                  </Link>

                  <button className="action-card" onClick={() => setCurrentPage('settings')}>
                    <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
                      <circle cx="16" cy="16" r="5" stroke="currentColor" strokeWidth="2"/>
                      <path d="M16 2V6M16 26V30M30 16H26M6 16H2M25.456 25.456L22.627 22.627M9.373 9.373L6.544 6.544M25.456 6.544L22.627 9.373M9.373 22.627L6.544 25.456" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                    </svg>
                    <h3>Настройки</h3>
                    <p>Настройте клиент под себя</p>
                  </button>
                </div>
              </div>
            </div>
          )}

          {/* Profile Page */}
          {currentPage === 'profile' && (
            <div className="page active">
              <div className="page-header">
                <h1>Профиль</h1>
                <p>Управление информацией аккаунта</p>
              </div>

              <div className="profile-card">
                <div className="profile-avatar">
                  {avatarPreview ? (
                    <img src={avatarPreview} alt="Avatar" style={{ width: '80px', height: '80px', borderRadius: '50%', objectFit: 'cover' }} />
                  ) : (
                    <svg width="80" height="80" viewBox="0 0 80 80" fill="none">
                      <circle cx="40" cy="40" r="40" fill="url(#avatarGradient)"/>
                      <path d="M40 20C32.268 20 26 26.268 26 34C26 41.732 32.268 48 40 48C47.732 48 54 41.732 54 34C54 26.268 47.732 20 40 20Z" fill="white" fillOpacity="0.2"/>
                      <path d="M20 60C20 51.716 26.716 45 35 45H45C53.284 45 60 51.716 60 60V65H20V60Z" fill="white" fillOpacity="0.2"/>
                      <defs>
                        <linearGradient id="avatarGradient" x1="0" y1="0" x2="80" y2="80">
                          <stop offset="0%" stopColor="#A855F7"/>
                          <stop offset="100%" stopColor="#EC4899"/>
                        </linearGradient>
                      </defs>
                    </svg>
                  )}
                  <input
                    type="file"
                    id="avatarUpload"
                    accept="image/*"
                    style={{ display: 'none' }}
                    onChange={handleAvatarUpload}
                  />
                  <button className="avatar-upload-btn" onClick={() => document.getElementById('avatarUpload')?.click()}>
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                      <path d="M10 4V16M4 10H16" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                    </svg>
                  </button>
                </div>
                <div className="profile-info">
                  <h2>{user.username}</h2>
                  <p>{user.email}</p>
                  <div className="subscription-badge">
                    {subscriptionNames[user.subscription]} версия
                  </div>
                </div>
              </div>

              <div className="info-grid">
                <div className="info-card">
                  <h3>Информация об аккаунте</h3>
                  <div className="info-row">
                    <span>ID пользователя:</span>
                    <span>{user.id}</span>
                  </div>
                  <div className="info-row">
                    <span>Дата регистрации:</span>
                    <span>{formatDate(user.registeredAt)}</span>
                  </div>
                  <div className="info-row">
                    <span>Статус:</span>
                    <span className="status-badge active">Активен</span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Settings Page */}
          {currentPage === 'settings' && (
            <div className="page active">
              <div className="page-header">
                <h1>Настройки</h1>
                <p>Настройте клиент под свои предпочтения</p>
              </div>

              <div className="settings-section">
                <h2>
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M3 10H17M3 5H17M3 15H17" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                  Основные настройки
                </h2>
                <div className="settings-grid">
                  <div className="setting-item">
                    <div className="setting-info">
                      <div className="setting-label">Уведомления</div>
                      <div className="setting-desc">Получать уведомления об обновлениях</div>
                    </div>
                    <label className="toggle">
                      <input type="checkbox" id="settingNotifications" defaultChecked={user.settings.notifications} />
                      <span className="toggle-slider"></span>
                    </label>
                  </div>

                  <div className="setting-item">
                    <div className="setting-info">
                      <div className="setting-label">Автообновление</div>
                      <div className="setting-desc">Автоматически обновлять клиент</div>
                    </div>
                    <label className="toggle">
                      <input type="checkbox" id="settingAutoUpdate" defaultChecked={user.settings.autoUpdate} />
                      <span className="toggle-slider"></span>
                    </label>
                  </div>
                </div>
              </div>

              <div className="settings-section">
                <h2>
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M10 2C5.58172 2 2 5.58172 2 10C2 14.4183 5.58172 18 10 18C14.4183 18 18 14.4183 18 10C18 5.58172 14.4183 2 10 2Z" stroke="currentColor" strokeWidth="2"/>
                    <path d="M10 6V10L13 13" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                  Интерфейс
                </h2>
                <div className="settings-grid">
                  <div className="setting-item">
                    <div className="setting-info">
                      <div className="setting-label">Тема</div>
                      <div className="setting-desc">Выберите цветовую схему</div>
                    </div>
                    <select id="settingTheme" className="setting-select" defaultValue={user.settings.theme}>
                      <option value="dark">Темная</option>
                      <option value="light">Светлая</option>
                      <option value="auto">Автоматически</option>
                    </select>
                  </div>

                  <div className="setting-item">
                    <div className="setting-info">
                      <div className="setting-label">Язык</div>
                      <div className="setting-desc">Язык интерфейса</div>
                    </div>
                    <select id="settingLanguage" className="setting-select" defaultValue={user.settings.language}>
                      <option value="ru">Русский</option>
                      <option value="en">English</option>
                      <option value="uk">Українська</option>
                    </select>
                  </div>
                </div>
              </div>

              <button className="btn btn-primary" onClick={handleSaveSettings}>
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                  <path d="M15 7L8 14L4 10" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
                Сохранить настройки
              </button>
            </div>
          )}
        </main>
      </div>
    </div>
  )
}
