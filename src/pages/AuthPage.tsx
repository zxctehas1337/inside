import { useState } from 'react'
import * as React from 'react'
import { useNavigate } from 'react-router-dom'
import AnimatedBackground from '../components/AnimatedBackground'
import Notification from '../components/Notification'
import { setCurrentUser } from '../utils/database'
import { initAnalytics, trackPageView, trackButtonClick } from '../utils/analytics'
import { NotificationType } from '../types'
import '../styles/AuthPage.css'

export default function AuthPage() {
  const [notification, setNotification] = useState<{ message: string; type: NotificationType } | null>(null)
  const [isAdminMode, setIsAdminMode] = useState(false)
  const [adminEmail, setAdminEmail] = useState('')
  const [adminPassword, setAdminPassword] = useState('')
  const navigate = useNavigate()

  // Инициализация аналитики
  React.useEffect(() => {
    initAnalytics()
    trackPageView('/auth')
  }, [])

  // Проверяем URL параметры для OAuth callback
  React.useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const authStatus = params.get('auth')
    const userData = params.get('user')

    if (authStatus === 'success' && userData) {
      try {
        const user = JSON.parse(decodeURIComponent(userData))
        console.log('✅ OAuth успешен, сохраняем пользователя с токеном')
        setCurrentUser(user) // Токен будет сохранен автоматически
        setNotification({ message: 'Вход выполнен успешно!', type: 'success' })
        
        // Перенаправление
        if (user.isAdmin) {
          setTimeout(() => navigate('/admin'), 1500)
        } else {
          setTimeout(() => navigate('/dashboard'), 1500)
        }
      } catch (error) {
        console.error('Error parsing user data:', error)
        setNotification({ message: 'Ошибка при входе', type: 'error' })
      }
    }
  }, [navigate])

  const handleAdminLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!adminEmail || !adminPassword) {
      setNotification({ message: 'Заполните все поля', type: 'error' })
      return
    }

    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL || 'https://oneshakedown.onrender.com'}/api/auth/admin`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email: adminEmail, password: adminPassword }),
      })

      const result = await response.json()

      if (result.success && result.data) {
        console.log('✅ Вход администратора успешен, сохраняем с токеном')
        setCurrentUser(result.data) // Токен будет сохранен автоматически
        setNotification({ message: 'Вход администратора выполнен!', type: 'success' })
        setTimeout(() => navigate('/admin'), 1500)
      } else {
        setNotification({ message: result.message || 'Неверные данные администратора', type: 'error' })
      }
    } catch (error) {
      console.error('Admin login error:', error)
      setNotification({ message: 'Ошибка подключения к серверу', type: 'error' })
    }
  }

  return (
    <div className="auth-page">
      <AnimatedBackground />
      
      {notification && (
        <Notification
          message={notification.message}
          type={notification.type}
          onClose={() => setNotification(null)}
        />
      )}

      <div className="auth-container">
        <div className="auth-box">
          <div className="auth-logo">
            <img src="/icon.ico" alt="ShakeDown" width="48" height="48" style={{ borderRadius: '12px' }} />
            <div className="logo-text">
              <span className="brand">SHAKEDOWN</span>
              <span className="version">v3.1.9</span>
            </div>
          </div>

          <div className="auth-title">
            <h2>Войти в систему</h2>
            <p>Используйте Google или Yandex аккаунт для входа</p>
          </div>

          <div className="auth-form active">
            {!isAdminMode ? (
              <>
                <a 
                  href={`${import.meta.env.VITE_API_URL || 'https://oneshakedown.onrender.com'}/api/auth/google`}
                  className="btn btn-google btn-full"
                >
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M18.1713 8.36788H17.5001V8.33329H10.0001V11.6666H14.7096C14.0225 13.6069 12.1763 15 10.0001 15C7.23882 15 5.00007 12.7612 5.00007 9.99996C5.00007 7.23871 7.23882 4.99996 10.0001 4.99996C11.2746 4.99996 12.4342 5.48079 13.3171 6.26621L15.6742 3.90913C14.1859 2.52204 12.1951 1.66663 10.0001 1.66663C5.39799 1.66663 1.66675 5.39788 1.66675 9.99996C1.66675 14.602 5.39799 18.3333 10.0001 18.3333C14.6022 18.3333 18.3334 14.602 18.3334 9.99996C18.3334 9.44121 18.2759 8.89579 18.1713 8.36788Z" fill="#FFC107"/>
                    <path d="M2.6275 6.12121L5.36542 8.12954C6.10625 6.29537 7.90042 4.99996 10.0004 4.99996C11.2754 4.99996 12.4346 5.48079 13.3175 6.26621L15.6746 3.90913C14.1863 2.52204 12.1954 1.66663 10.0004 1.66663C6.79917 1.66663 4.02334 3.47371 2.6275 6.12121Z" fill="#FF3D00"/>
                    <path d="M10.0004 18.3333C12.1529 18.3333 14.1083 17.5095 15.5871 16.17L13.0079 13.9875C12.1431 14.6452 11.0864 15.0008 10.0004 15C7.83294 15 5.99211 13.6179 5.29878 11.6891L2.58211 13.7829C3.96044 16.4816 6.76128 18.3333 10.0004 18.3333Z" fill="#4CAF50"/>
                    <path d="M18.1713 8.36796H17.5V8.33337H10V11.6667H14.7096C14.3809 12.5902 13.7889 13.3972 13.0067 13.9879L13.0079 13.9871L15.5871 16.1696C15.4046 16.3355 18.3333 14.1667 18.3333 10C18.3333 9.44129 18.2758 8.89587 18.1713 8.36796Z" fill="#1976D2"/>
                  </svg>
                  Войти через Google
                </a>

                <a 
                  href={`${import.meta.env.VITE_API_URL || 'https://oneshakedown.onrender.com'}/api/auth/yandex`}
                  className="btn btn-yandex btn-full"
                  style={{ marginTop: '12px' }}
                >
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                    <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2Z" fill="#FC3F1D"/>
                    <path d="M13.5 7.5H11.2C9.3 7.5 8.5 8.6 8.5 10.1C8.5 11.9 9.4 12.8 10.7 13.7L12.3 14.8L8.3 20.5H5.5L9.2 15.2C7.4 13.9 6 12.5 6 10.1C6 7.3 7.9 5 11.2 5H16V20.5H13.5V7.5Z" fill="white"/>
                  </svg>
                  Войти через Yandex
                </a>

                <a 
                  href={`${import.meta.env.VITE_API_URL || 'https://oneshakedown.onrender.com'}/api/auth/github`}
                  className="btn btn-github btn-full"
                  style={{ marginTop: '12px' }}
                >
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                    <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.477 2 2 6.477 2 12C2 16.42 4.865 20.17 8.839 21.49C9.339 21.58 9.521 21.27 9.521 21C9.521 20.77 9.513 20.14 9.508 19.31C6.726 19.91 6.139 17.77 6.139 17.77C5.685 16.61 5.029 16.3 5.029 16.3C4.121 15.68 5.098 15.69 5.098 15.69C6.101 15.76 6.629 16.73 6.629 16.73C7.521 18.28 8.97 17.84 9.539 17.58C9.631 16.93 9.889 16.49 10.175 16.24C7.955 16 5.62 15.13 5.62 11.52C5.62 10.43 6.01 9.54 6.649 8.85C6.546 8.6 6.203 7.57 6.747 6.17C6.747 6.17 7.586 5.9 9.497 7.2C10.31 6.98 11.16 6.87 12.01 6.87C12.86 6.87 13.71 6.98 14.523 7.2C16.434 5.9 17.272 6.17 17.272 6.17C17.817 7.57 17.474 8.6 17.371 8.85C18.01 9.54 18.397 10.43 18.397 11.52C18.397 15.14 16.058 15.99 13.833 16.24C14.191 16.56 14.512 17.19 14.512 18.15C14.512 19.53 14.499 20.64 14.499 21C14.499 21.27 14.679 21.58 15.186 21.49C19.157 20.16 22 16.42 22 12C22 6.477 17.523 2 12 2Z" fill="currentColor"/>
                  </svg>
                  Войти через GitHub
                </a>

                <div className="divider">
                  <span>или</span>
                </div>

                <button 
                  onClick={() => {
                    setIsAdminMode(true)
                    trackButtonClick('admin_mode')
                  }}
                  className="btn btn-admin btn-full"
                >
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M10 2C6.13 2 3 5.13 3 9C3 11.17 4.04 13.09 5.65 14.31L5 17L7.69 15.65C8.42 15.88 9.19 16 10 16C13.87 16 17 12.87 17 9C17 5.13 13.87 2 10 2ZM10 14C9.31 14 8.65 13.89 8.03 13.69L7 14.17L7.35 13.14C5.95 12.16 5 10.68 5 9C5 6.24 7.24 4 10 4C12.76 4 15 6.24 15 9C15 11.76 12.76 14 10 14Z" fill="currentColor"/>
                    <path d="M10 6C9.45 6 9 6.45 9 7V10C9 10.55 9.45 11 10 11C10.55 11 11 10.55 11 10V7C11 6.45 10.55 6 10 6Z" fill="currentColor"/>
                    <circle cx="10" cy="12.5" r="0.75" fill="currentColor"/>
                  </svg>
                  Вход для администраторов
                </button>
              </>
            ) : (
              <>
                <form onSubmit={handleAdminLogin} className="admin-form">
                  <div className="form-group">
                    <label htmlFor="admin-email">Email администратора</label>
                    <input
                      id="admin-email"
                      type="email"
                      value={adminEmail}
                      onChange={(e) => setAdminEmail(e.target.value)}
                      placeholder="admin@example.com"
                      className="form-input"
                      required
                    />
                  </div>
                  
                  <div className="form-group">
                    <label htmlFor="admin-password">Пароль</label>
                    <input
                      id="admin-password"
                      type="password"
                      value={adminPassword}
                      onChange={(e) => setAdminPassword(e.target.value)}
                      placeholder="••••••••"
                      className="form-input"
                      required
                    />
                  </div>

                  <button type="submit" className="btn btn-primary btn-full">
                    Войти как администратор
                  </button>
                </form>

                <button 
                  onClick={() => setIsAdminMode(false)}
                  className="btn btn-secondary btn-full"
                  style={{ marginTop: '12px' }}
                >
                  Назад к обычному входу
                </button>
              </>
            )}
          </div>

          <a href="/" className="back-link">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
              <path d="M10 12L6 8L10 4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
            Вернуться на главную
          </a>
        </div>
      </div>
    </div>
  )
}
