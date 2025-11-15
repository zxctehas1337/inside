import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { NewsPost } from '../types'
import AnimatedBackground from '../components/AnimatedBackground'
import { getCurrentUser } from '../utils/database'
import '../styles/NewsPage.css'

export default function NewsPage() {
  const [news, setNews] = useState<NewsPost[]>([])
  const [filter, setFilter] = useState<'all' | 'launcher' | 'website'>('all')
  const currentUser = getCurrentUser()

  useEffect(() => {
    loadNews()
  }, [])

  const loadNews = async () => {
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/api/news`)
      if (response.ok) {
        const data = await response.json()
        if (data.success && data.data) {
          setNews(data.data)
          return
        }
      }
    } catch (error) {
      console.error('Failed to load news:', error)
    }
    setNews([])
  }

  const filteredNews = filter === 'all' 
    ? news 
    : news.filter(n => n.type === filter)

  return (
    <div className="news-page">
      <AnimatedBackground />
      
      {/* Header */}
      <header className="header">
        <nav className="nav">
          <div className="nav-brand">
            <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '12px', textDecoration: 'none' }}>
              <img src="/icon.ico" alt="Inside" width="32" height="32" style={{ borderRadius: '8px' }} />
              <span className="version">v3.1.9</span>
            </Link>
          </div>
          <div className="nav-links">
            <Link to="/">Главная</Link>
            <Link to="/news" className="active">Новости</Link>
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
      <section className="news-hero">
        <div className="container">
          <div className="news-hero-content">
            <h1>Новости <span className="gradient-text">Inside Client</span></h1>
            <p>Последние обновления, анонсы и новости проекта</p>
          </div>
          
          {/* Filter */}
          <div className="news-filter">
            <button 
              className={`filter-btn ${filter === 'all' ? 'active' : ''}`}
              onClick={() => setFilter('all')}
            >
              Все новости
            </button>
            <button 
              className={`filter-btn ${filter === 'website' ? 'active' : ''}`}
              onClick={() => setFilter('website')}
            >
              Сайт
            </button>
            <button 
              className={`filter-btn ${filter === 'launcher' ? 'active' : ''}`}
              onClick={() => setFilter('launcher')}
            >
              Лаунчер
            </button>
          </div>
        </div>
      </section>

      {/* News Grid */}
      <section className="news-section">
        <div className="container">
          {filteredNews.length === 0 ? (
            <div className="empty-news">
              <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
                <path d="M8 16C8 11.5817 11.5817 8 16 8H48C52.4183 8 56 11.5817 56 16V48C56 52.4183 52.4183 56 48 56H16C11.5817 56 8 52.4183 8 48V16Z" stroke="currentColor" strokeWidth="2"/>
                <rect x="16" y="16" width="20" height="12" fill="currentColor" opacity="0.3"/>
                <rect x="16" y="32" width="32" height="2" fill="currentColor" opacity="0.3"/>
                <rect x="16" y="40" width="32" height="2" fill="currentColor" opacity="0.3"/>
              </svg>
              <h3>Новостей пока нет</h3>
              <p>Следите за обновлениями</p>
            </div>
          ) : (
            <div className="news-grid">
              {filteredNews.map(post => (
                <article key={post.id} className="news-article">
                  <div className="news-article-header">
                    <span className={`news-type-badge ${post.type}`}>
                      {post.type === 'launcher' ? 'Лаунчер' : 'Сайт'}
                    </span>
                    <span className="news-date">
                      {new Date(post.date).toLocaleDateString('ru-RU', { 
                        year: 'numeric', 
                        month: 'long', 
                        day: 'numeric' 
                      })}
                    </span>
                  </div>
                  <h2>{post.title}</h2>
                  <p>{post.content}</p>
                  <div className="news-article-footer">
                    <div className="news-author">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                        <circle cx="8" cy="5" r="3"/>
                        <path d="M8 10C4.68629 10 2 11.7909 2 14H14C14 11.7909 11.3137 10 8 10Z"/>
                      </svg>
                      <span>{post.author}</span>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          )}
        </div>
      </section>

      {/* Footer */}
      <footer className="footer">
        <div className="container">
          <div className="footer-content">
            <div className="footer-brand">
              <img src="/icon.ico" alt="Inside" width="32" height="32" style={{ borderRadius: '8px' }} />
              <span>Inside Client</span>
            </div>
            <div className="footer-links">
              <Link to="/">Главная</Link>
              <Link to="/news">Новости</Link>
              <Link to="/auth">Войти</Link>
            </div>
          </div>
          <div className="footer-bottom">
            <p>© 2025 Inside Client. Все права защищены.</p>
          </div>
        </div>
      </footer>
    </div>
  )
}
