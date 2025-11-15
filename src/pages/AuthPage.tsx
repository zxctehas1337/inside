import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import AnimatedBackground from '../components/AnimatedBackground'
import Notification from '../components/Notification'
import { Database, setCurrentUser } from '../utils/database'
import { verifyEmailCode, resendVerificationCode } from '../utils/api'
import { NotificationType } from '../types'
import '../styles/AuthPage.css'

export default function AuthPage() {
  const [activeTab, setActiveTab] = useState<'login' | 'register'>('login')
  const [showPassword, setShowPassword] = useState(false)
  const [notification, setNotification] = useState<{ message: string; type: NotificationType } | null>(null)
  const [showVerificationModal, setShowVerificationModal] = useState(false)
  const [verificationCode, setVerificationCode] = useState(['', '', '', '', '', ''])
  const [pendingUserId, setPendingUserId] = useState<number | null>(null)
  const [isVerifying, setIsVerifying] = useState(false)
  const navigate = useNavigate()

  const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    const formData = new FormData(e.currentTarget)
    const usernameOrEmail = formData.get('username') as string
    const password = formData.get('password') as string
    const rememberMe = formData.get('rememberMe') === 'on'

    const db = new Database()
    const result = await db.login(usernameOrEmail, password)

    if (result.success && result.user) {
      setCurrentUser(result.user)
      
      // Сохраняем флаг "Запомнить меня"
      if (rememberMe) {
        localStorage.setItem('rememberMe', 'true')
      } else {
        localStorage.removeItem('rememberMe')
      }
      
      setNotification({ message: result.message, type: 'success' })
      
      // Перенаправление админа в админ-панель
      if (result.user.isAdmin) {
        setTimeout(() => navigate('/admin'), 1500)
      } else {
        setTimeout(() => navigate('/dashboard'), 1500)
      }
    } else {
      setNotification({ message: result.message, type: 'error' })
    }
  }

  const handleRegister = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    const formData = new FormData(e.currentTarget)
    const username = formData.get('username') as string
    const email = formData.get('email') as string
    const password = formData.get('password') as string
    const passwordConfirm = formData.get('passwordConfirm') as string
    const agreeTerms = formData.get('agreeTerms') === 'on'

    if (password !== passwordConfirm) {
      setNotification({ message: 'Пароли не совпадают', type: 'error' })
      return
    }

    if (password.length < 6) {
      setNotification({ message: 'Пароль должен быть не менее 6 символов', type: 'error' })
      return
    }

    if (!agreeTerms) {
      setNotification({ message: 'Необходимо согласиться с условиями', type: 'error' })
      return
    }

    const db = new Database()
    const result = await db.register(username, email, password)

    if (result.success && result.user) {
      if (result.requiresVerification) {
        // Показываем модальное окно для ввода кода
        setPendingUserId(result.user.id)
        setShowVerificationModal(true)
        setNotification({ message: result.message, type: 'success' })
      } else {
        setCurrentUser(result.user)
        setNotification({ message: result.message, type: 'success' })
        setTimeout(() => navigate('/dashboard'), 1500)
      }
    } else {
      setNotification({ message: result.message, type: 'error' })
    }
  }

  const handleVerificationCodeChange = (index: number, value: string) => {
    if (value.length > 1) return
    if (value && !/^\d$/.test(value)) return

    const newCode = [...verificationCode]
    newCode[index] = value
    setVerificationCode(newCode)

    // Автоматический переход к следующему полю
    if (value && index < 5) {
      const nextInput = document.getElementById(`code-${index + 1}`)
      nextInput?.focus()
    }
  }

  const handleVerificationCodeKeyDown = (index: number, e: React.KeyboardEvent) => {
    if (e.key === 'Backspace' && !verificationCode[index] && index > 0) {
      const prevInput = document.getElementById(`code-${index - 1}`)
      prevInput?.focus()
    }
  }

  const handleVerifyCode = async () => {
    const code = verificationCode.join('')
    if (code.length !== 6) {
      setNotification({ message: 'Введите полный код', type: 'error' })
      return
    }

    if (!pendingUserId) {
      setNotification({ message: 'Ошибка: ID пользователя не найден', type: 'error' })
      return
    }

    setIsVerifying(true)
    const result = await verifyEmailCode(pendingUserId, code)
    setIsVerifying(false)

    if (result.success) {
      setNotification({ message: result.message, type: 'success' })
      setShowVerificationModal(false)
      
      // Получаем обновленные данные пользователя
      const db = new Database()
      const userResult = await db.getUserById(pendingUserId)
      if (userResult.success && userResult.user) {
        setCurrentUser(userResult.user)
        setTimeout(() => navigate('/dashboard'), 1500)
      }
    } else {
      setNotification({ message: result.message, type: 'error' })
    }
  }

  const handleResendCode = async () => {
    if (!pendingUserId) return

    const result = await resendVerificationCode(pendingUserId)
    if (result.success) {
      setNotification({ message: result.message, type: 'success' })
      setVerificationCode(['', '', '', '', '', ''])
    } else {
      setNotification({ message: result.message, type: 'error' })
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
            <img src="/icon.ico" alt="Inside" width="48" height="48" style={{ borderRadius: '12px' }} />
            <div className="logo-text">
              <span className="brand">INSIDE</span>
              <span className="version">v3.1.9</span>
            </div>
          </div>

          <div className="auth-tabs">
            <button
              className={`tab-btn ${activeTab === 'login' ? 'active' : ''}`}
              onClick={() => setActiveTab('login')}
            >
              Вход
            </button>
            <button
              className={`tab-btn ${activeTab === 'register' ? 'active' : ''}`}
              onClick={() => setActiveTab('register')}
            >
              Регистрация
            </button>
          </div>

          {activeTab === 'login' ? (
            <form className="auth-form active" onSubmit={handleLogin}>
              <div className="form-group">
                <label>
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M16 17V15C16 13.9391 15.5786 12.9217 14.8284 12.1716C14.0783 11.4214 13.0609 11 12 11H8C6.93913 11 5.92172 11.4214 5.17157 12.1716C4.42143 12.9217 4 13.9391 4 15V17" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                    <circle cx="10" cy="5" r="3" stroke="currentColor" strokeWidth="2"/>
                  </svg>
                  Логин или Email
                </label>
                <input type="text" name="username" placeholder="Введите логин или email" required />
              </div>

              <div className="form-group">
                <label>
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <rect x="4" y="8" width="12" height="9" rx="2" stroke="currentColor" strokeWidth="2"/>
                    <path d="M7 8V5C7 3.34315 8.34315 2 10 2C11.6569 2 13 3.34315 13 5V8" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                  Пароль
                </label>
                <div className="password-input">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    name="password"
                    placeholder="Введите пароль"
                    required
                  />
                  <button
                    type="button"
                    className="toggle-password"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                      <path d="M10 7C8.34315 7 7 8.34315 7 10C7 11.6569 8.34315 13 10 13C11.6569 13 13 11.6569 13 10C13 8.34315 11.6569 7 10 7Z" stroke="currentColor" strokeWidth="2"/>
                      <path d="M10 3C5 3 2 10 2 10C2 10 5 17 10 17C15 17 18 10 18 10C18 10 15 3 10 3Z" stroke="currentColor" strokeWidth="2"/>
                    </svg>
                  </button>
                </div>
              </div>

              <div className="form-options">
                <label className="checkbox">
                  <input type="checkbox" name="rememberMe" />
                  <span className="checkmark"></span>
                  Запомнить меня
                </label>
              </div>

              <button type="submit" className="btn btn-primary btn-full">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                  <path d="M9 3H4C3.44772 3 3 3.44772 3 4V16C3 16.5523 3.44772 17 4 17H9" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  <path d="M13 13L17 10L13 7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                  <path d="M17 10H7" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                </svg>
                Войти
              </button>
            </form>
          ) : (
            <form className="auth-form active" onSubmit={handleRegister}>
              <div className="form-group">
                <label>
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <path d="M16 17V15C16 13.9391 15.5786 12.9217 14.8284 12.1716C14.0783 11.4214 13.0609 11 12 11H8C6.93913 11 5.92172 11.4214 5.17157 12.1716C4.42143 12.9217 4 13.9391 4 15V17" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                    <circle cx="10" cy="5" r="3" stroke="currentColor" strokeWidth="2"/>
                  </svg>
                  Логин
                </label>
                <input type="text" name="username" placeholder="Придумайте логин" required />
              </div>

              <div className="form-group">
                <label>
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <rect x="3" y="5" width="14" height="11" rx="2" stroke="currentColor" strokeWidth="2"/>
                    <path d="M3 8L10 12L17 8" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                  </svg>
                  Email
                </label>
                <input type="email" name="email" placeholder="Введите email" required />
              </div>

              <div className="form-group">
                <label>
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <rect x="4" y="8" width="12" height="9" rx="2" stroke="currentColor" strokeWidth="2"/>
                    <path d="M7 8V5C7 3.34315 8.34315 2 10 2C11.6569 2 13 3.34315 13 5V8" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                  Пароль
                </label>
                <div className="password-input">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    name="password"
                    placeholder="Придумайте пароль"
                    required
                  />
                  <button
                    type="button"
                    className="toggle-password"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                      <path d="M10 7C8.34315 7 7 8.34315 7 10C7 11.6569 8.34315 13 10 13C11.6569 13 13 11.6569 13 10C13 8.34315 11.6569 7 10 7Z" stroke="currentColor" strokeWidth="2"/>
                      <path d="M10 3C5 3 2 10 2 10C2 10 5 17 10 17C15 17 18 10 18 10C18 10 15 3 10 3Z" stroke="currentColor" strokeWidth="2"/>
                    </svg>
                  </button>
                </div>
              </div>

              <div className="form-group">
                <label>
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                    <rect x="4" y="8" width="12" height="9" rx="2" stroke="currentColor" strokeWidth="2"/>
                    <path d="M7 8V5C7 3.34315 8.34315 2 10 2C11.6569 2 13 3.34315 13 5V8" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                  Подтвердите пароль
                </label>
                <input type="password" name="passwordConfirm" placeholder="Повторите пароль" required />
              </div>

              <label className="checkbox">
                <input type="checkbox" name="agreeTerms" required />
                <span className="checkmark"></span>
                Я согласен с условиями использования
              </label>

              <button type="submit" className="btn btn-primary btn-full">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                  <path d="M16 17V15C16 13.9391 15.5786 12.9217 14.8284 12.1716C14.0783 11.4214 13.0609 11 12 11H8C6.93913 11 5.92172 11.4214 5.17157 12.1716C4.42143 12.9217 4 13.9391 4 15V17" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  <circle cx="10" cy="5" r="3" stroke="currentColor" strokeWidth="2"/>
                </svg>
                Зарегистрироваться
              </button>
            </form>
          )}

          <a href="/" className="back-link">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
              <path d="M10 12L6 8L10 4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
            Вернуться на главную
          </a>
        </div>
      </div>

      {/* Модальное окно для ввода кода */}
      {showVerificationModal && (
        <div className="modal-overlay" onClick={() => setShowVerificationModal(false)}>
          <div className="verification-modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Подтверждение Email</h2>
              <button className="close-btn" onClick={() => setShowVerificationModal(false)}>
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                  <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                </svg>
              </button>
            </div>
            
            <div className="modal-content">
              <p className="verification-text">
                Мы отправили 6-значный код на вашу почту. Введите его ниже:
              </p>
              
              <div className="code-inputs">
                {verificationCode.map((digit, index) => (
                  <input
                    key={index}
                    id={`code-${index}`}
                    type="text"
                    maxLength={1}
                    value={digit}
                    onChange={(e) => handleVerificationCodeChange(index, e.target.value)}
                    onKeyDown={(e) => handleVerificationCodeKeyDown(index, e)}
                    className="code-input"
                  />
                ))}
              </div>

              <button 
                className="btn btn-primary btn-full"
                onClick={handleVerifyCode}
                disabled={isVerifying || verificationCode.join('').length !== 6}
              >
                {isVerifying ? 'Проверка...' : 'Подтвердить'}
              </button>

              <button 
                className="btn btn-secondary btn-full"
                onClick={handleResendCode}
                disabled={isVerifying}
              >
                Отправить код повторно
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
