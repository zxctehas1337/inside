import { useEffect } from 'react'
import { Link } from 'react-router-dom'
import AnimatedBackground from '../components/AnimatedBackground'
import PremiumChat from '../components/PremiumChat'
import { getCurrentUser } from '../utils/database'
import { initAnalytics, trackPageView } from '../utils/analytics'
import '../styles/PremiumChatPage.css'

export default function PremiumChatPage() {
  const currentUser = getCurrentUser()

  useEffect(() => {
    initAnalytics(currentUser?.id)
    trackPageView('/premium-chat')
  }, [])

  return (
    <div className="premium-chat-page">
      <AnimatedBackground />
      
      {/* Header */}
      <header className="header">
        <nav className="nav">
          <div className="nav-brand">
            <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '12px', textDecoration: 'none' }}>
              <img src="/icon.ico" alt="ShakeDown" width="32" height="32" style={{ borderRadius: '8px' }} />
              <span className="version">v3.1.9</span>
            </Link>
          </div>
          <div className="nav-links">
            <Link to="/">Главная</Link>
            <Link to="/news">Новости</Link>
            <Link to="/premium-chat" className="active">Premium Chat</Link>
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
      <section className="chat-hero">
        <div className="container">
          <div className="chat-hero-content">
            <h1>
              <span className="premium-icon">⭐</span>
              Premium <span className="gradient-text">Chat</span>
            </h1>
            <p>Эксклюзивное общение для Premium и Alpha пользователей</p>
          </div>
        </div>
      </section>

      {/* Chat Section */}
      <section className="chat-section">
        <div className="container">
          <div className="chat-container">
            <PremiumChat />
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="footer">
        <div className="container">
          <div className="footer-content">
            <div className="footer-brand">
              <img src="/icon.ico" alt="ShakeDown" width="32" height="32" style={{ borderRadius: '8px' }} />
              <span>ShakeDown Client</span>
            </div>
            <div className="footer-links">
              <Link to="/">Главная</Link>
              <Link to="/news">Новости</Link>
              <Link to="/premium-chat">Premium Chat</Link>
              <Link to="/auth">Войти</Link>
            </div>
          </div>
          <div className="footer-bottom">
            <p>© 2025 ShakeDown Client. Все права защищены.</p>
          </div>
        </div>
      </footer>
    </div>
  )
}
