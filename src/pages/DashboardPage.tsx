import { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import AnimatedBackground from '../components/AnimatedBackground'
import Notification from '../components/Notification'
import { LogoutModal } from '../components/LogoutModal'
import { DeleteAccountModal } from '../components/DeleteAccountModal'
import { getCurrentUser, setCurrentUser, Database } from '../utils/database'
import { User, NotificationType } from '../types'
import { DOWNLOAD_LINKS } from '../utils/constants'
import '../styles/DashboardPage.css'

type PageType = 'home' | 'profile' | 'settings'

export default function DashboardPage() {
  const [currentPage, setCurrentPage] = useState<PageType>('home')
  const [user, setUser] = useState<User | null>(null)
  const [notification, setNotification] = useState<{ message: string; type: NotificationType } | null>(null)
  const [avatarPreview, setAvatarPreview] = useState<string | null>(null)
  const [showLogoutModal, setShowLogoutModal] = useState(false)
  const [showDeleteModal, setShowDeleteModal] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    // Проверяем URL параметры для Google OAuth callback
    const params = new URLSearchParams(window.location.search)
    const authStatus = params.get('auth')
    const userDataParam = params.get('user')

    if (authStatus === 'success' && userDataParam) {
      try {
        const userData = JSON.parse(decodeURIComponent(userDataParam))
        setCurrentUser(userData)
        setUser(userData)
        if (userData.avatar) {
          setAvatarPreview(userData.avatar)
        }
        setNotification({ message: 'Вход выполнен успешно!', type: 'success' })
        
        // Очищаем URL от параметров
        window.history.replaceState({}, '', '/dashboard')
      } catch (error) {
        console.error('Error parsing user data:', error)
      }
    } else {
      const userData = getCurrentUser()
      if (!userData) {
        navigate('/auth')
      } else {
        setUser(userData)
        if (userData.avatar) {
          setAvatarPreview(userData.avatar)
        }
      }
    }

    // Обработчик события удаления пользователя
    const handleUserDeleted = (event: CustomEvent) => {
      const reason = event.detail?.reason || 'Ваш аккаунт был удален'
      setNotification({ message: reason, type: 'error' })
      setTimeout(() => {
        setCurrentUser(null)
        navigate('/auth')
      }, 3000)
    }

    window.addEventListener('userDeleted', handleUserDeleted as EventListener)
    
    return () => {
      window.removeEventListener('userDeleted', handleUserDeleted as EventListener)
    }
  }, [navigate])

  const handleLogout = () => {
    setCurrentUser(null)
    navigate('/auth')
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

  const handleDeleteAccount = async () => {
    if (!user) return

    const db = new Database()
    const result = await db.deleteAccount(user.id)

    if (result.success) {
      setNotification({ message: 'Аккаунт успешно удален', type: 'success' })
      setTimeout(() => {
        setCurrentUser(null)
        navigate('/auth')
      }, 2000)
    } else {
      setNotification({ message: result.message || 'Ошибка удаления аккаунта', type: 'error' })
      setShowDeleteModal(false)
    }
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

      <LogoutModal
        isOpen={showLogoutModal}
        onConfirm={handleLogout}
        onCancel={() => setShowLogoutModal(false)}
      />

      <DeleteAccountModal
        isOpen={showDeleteModal}
        onConfirm={handleDeleteAccount}
        onCancel={() => setShowDeleteModal(false)}
      />

      <div className="dashboard-container">
        {/* Sidebar */}
        <aside className="dashboard-sidebar">
          <div className="sidebar-header">
            <img src="/icon.ico" alt="Inside Client" width="40" height="40" style={{ borderRadius: '8px' }} />
            <div>
              <div className="brand">INSIDE</div>
              <div className="version">v3.1.9</div>
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
              Дашборд
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
                <path d="M10 2V4M10 16V18M18 10H16M4 10H2M15.657 15.657L14.243 14.243M5.757 5.757L4.343 4.343M15.657 4.343L14.243 5.757M5.757 14.243L4.343 15.657" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              </svg>
              Настройки
            </button>
          </nav>

          <div className="sidebar-footer">
            <button className="logout-btn" onClick={() => setShowLogoutModal(true)}>
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
                  <a 
                    href={user.subscription === 'alpha' ? DOWNLOAD_LINKS.alpha : user.subscription === 'premium' ? DOWNLOAD_LINKS.premium : DOWNLOAD_LINKS.free} 
                    className="action-card"
                    download
                  >
                    <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
                      <path d="M16 6V20M16 20L22 14M16 20L10 14" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                      <path d="M6 24V25C6 25.5304 6.21071 26.0391 6.58579 26.4142C6.96086 26.7893 7.46957 27 8 27H24C24.5304 27 25.0391 26.7893 25.4142 26.4142C25.7893 26.0391 26 25.5304 26 25V24" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                    </svg>
                    <h3>Скачать клиент</h3>
                    <p>Загрузите последнюю версию</p>
                  </a>

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
                      <circle cx="16" cy="16" r="5" stroke="currentColor" strokeWidth="2.5"/>
                      <path d="M16 3V7M16 25V29M29 16H25M7 16H3M24.85 24.85L21.9 21.9M10.1 10.1L7.15 7.15M24.85 7.15L21.9 10.1M10.1 21.9L7.15 24.85" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round"/>
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

              <div className="profile-header">
                <div className="profile-avatar-section">
                  <div className="profile-avatar">
                    {avatarPreview ? (
                      <img src={avatarPreview} alt="Avatar" />
                    ) : (
                      <svg width="120" height="120" viewBox="0 0 120 120" fill="none">
                        <circle cx="60" cy="60" r="60" fill="url(#avatarGradient)"/>
                        <path d="M60 30C48.402 30 39 39.402 39 51C39 62.598 48.402 72 60 72C71.598 72 81 62.598 81 51C81 39.402 71.598 30 60 30Z" fill="white" fillOpacity="0.2"/>
                        <path d="M30 90C30 77.574 40.074 67.5 52.5 67.5H67.5C79.926 67.5 90 77.574 90 90V97.5H30V90Z" fill="white" fillOpacity="0.2"/>
                        <defs>
                          <linearGradient id="avatarGradient" x1="0" y1="0" x2="120" y2="120">
                            <stop offset="0%" stopColor="#A855F7"/>
                            <stop offset="100%" stopColor="#EC4899"/>
                          </linearGradient>
                        </defs>
                      </svg>
                    )}
                  </div>
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
                    Изменить аватар
                  </button>
                </div>
                <div className="profile-info">
                  <h2>{user.username}</h2>
                  <p className="profile-email">{user.email}</p>
                  <div className="profile-meta">
                    <div className="profile-meta-item">
                      <span className="meta-label">ID:</span>
                      <span className="meta-value">{user.id}</span>
                    </div>
                    <div className="profile-meta-item">
                      <span className="meta-label">Дата регистрации:</span>
                      <span className="meta-value">{formatDate(user.registeredAt)}</span>
                    </div>
                    <div className="profile-meta-item">
                      <span className="meta-label">Статус:</span>
                      <span className="status-badge active">Активен</span>
                    </div>
                  </div>
                  <div className="subscription-badge">
                    {subscriptionNames[user.subscription]} версия
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

              <div className="settings-section danger-zone">
                <h2>
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M10 2L2 7L10 12L18 7L10 2Z" stroke="currentColor" strokeWidth="2"/>
                    <path d="M2 12L10 17L18 12" stroke="currentColor" strokeWidth="2"/>
                  </svg>
                  Опасная зона
                </h2>
                <div className="danger-zone-content">
                  <div className="danger-zone-info">
                    <h3>Удалить аккаунт</h3>
                    <p>После удаления аккаунта все ваши данные будут безвозвратно удалены из базы данных. Это действие необратимо.</p>
                  </div>
                  <button className="btn btn-danger" onClick={() => setShowDeleteModal(true)}>
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                      <path d="M3 5H17M8 9V15M12 9V15M4 5L5 17C5 17.5523 5.44772 18 6 18H14C14.5523 18 15 17.5523 15 17L16 5M7 5V3C7 2.44772 7.44772 2 8 2H12C12.5523 2 13 2.44772 13 3V5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                    Удалить аккаунт
                  </button>
                </div>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  )
}
