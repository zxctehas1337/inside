import { useState, useEffect, useRef } from 'react'
import { getCurrentUser } from '../utils/database'
import '../styles/PremiumChat.css'

interface ChatMessage {
  id: number
  message: string
  created_at: string
  user_id: number
  username: string
  avatar: string
  subscription: string
}

export default function PremiumChat() {
  const [messages, setMessages] = useState<ChatMessage[]>([])
  const [newMessage, setNewMessage] = useState('')
  const [loading, setLoading] = useState(false)
  const [hasAccess, setHasAccess] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const currentUser = getCurrentUser()

  useEffect(() => {
    if (currentUser && ['premium', 'alpha'].includes(currentUser.subscription)) {
      setHasAccess(true)
      loadMessages()
      const interval = setInterval(loadMessages, 5000)
      return () => clearInterval(interval)
    }
  }, [currentUser])

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  const loadMessages = async () => {
    if (!currentUser) return

    try {
      const response = await fetch(
        `${import.meta.env.VITE_API_URL}/api/premium-chat?userId=${currentUser.id}`
      )
      const data = await response.json()
      if (data.success) {
        setMessages(data.data)
      }
    } catch (error) {
      console.error('Failed to load messages:', error)
    }
  }

  const handleSendMessage = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!currentUser || !newMessage.trim()) return

    setLoading(true)
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/api/premium-chat`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.id,
          message: newMessage.trim()
        })
      })

      const data = await response.json()
      if (data.success) {
        setMessages([...messages, data.data])
        setNewMessage('')
      }
    } catch (error) {
      console.error('Failed to send message:', error)
    } finally {
      setLoading(false)
    }
  }

  const formatTime = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })
  }

  if (!hasAccess) {
    return (
      <div className="premium-chat-locked">
        <svg className="lock-icon" width="80" height="80" viewBox="0 0 80 80" fill="none">
          <rect x="20" y="35" width="40" height="30" rx="4" stroke="currentColor" strokeWidth="3"/>
          <path d="M28 35V25C28 18.3726 33.3726 13 40 13C46.6274 13 52 18.3726 52 25V35" stroke="currentColor" strokeWidth="3" strokeLinecap="round"/>
          <circle cx="40" cy="50" r="3" fill="currentColor"/>
        </svg>
        <h3>Chat</h3>
        <p>Доступен только для Premium и Alpha подписчиков</p>
        <div className="premium-features">
          <div className="premium-feature">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H7L3 21V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z" stroke="currentColor" strokeWidth="2"/>
            </svg>
            <span>Общение с элитой сообщества</span>
          </div>
          <div className="premium-feature">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M13 2L3 14H12L11 22L21 10H12L13 2Z" stroke="currentColor" strokeWidth="2" strokeLinejoin="round"/>
            </svg>
            <span>Ранний доступ к новостям</span>
          </div>
          <div className="premium-feature">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
              <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
              <path d="M12 6V12L16 14" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            </svg>
            <span>Эксклюзивные обновления</span>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="premium-chat">
      <div className="premium-chat-header">
        <div className="chat-header-left">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H7L3 21V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z" stroke="currentColor" strokeWidth="2"/>
          </svg>
          <div>
            <h3>Chat</h3>
            <span className="chat-subtitle">Эксклюзивное общение</span>
          </div>
        </div>
        <div className="chat-header-right">
          <div className="online-indicator">
            <span className="online-dot"></span>
            <span className="online-text">{new Set(messages.map(m => m.user_id)).size} участников</span>
          </div>
        </div>
      </div>

      <div className="premium-chat-messages">
        {messages.map((msg, index) => {
          const isOwn = currentUser ? msg.user_id === currentUser.id : false
          const showAvatar = index === 0 || messages[index - 1].user_id !== msg.user_id

          return (
            <div key={msg.id} className={`chat-message ${isOwn ? 'own' : ''}`}>
              {!isOwn && showAvatar && (
                <img src={msg.avatar || '/icon.ico'} alt={msg.username} className="chat-avatar" />
              )}
              {!isOwn && !showAvatar && <div className="chat-avatar-spacer" />}
              
              <div className="chat-message-content">
                {!isOwn && showAvatar && (
                  <div className="chat-message-author">
                    <span className="chat-username">{msg.username}</span>
                    {msg.subscription === 'alpha' && (
                      <span className="chat-badge alpha">ALPHA</span>
                    )}
                    {msg.subscription === 'premium' && (
                      <span className="chat-badge premium">PREMIUM</span>
                    )}
                  </div>
                )}
                <div className="chat-message-bubble">
                  <p>{msg.message}</p>
                  <span className="chat-message-time">{formatTime(msg.created_at)}</span>
                </div>
              </div>
            </div>
          )
        })}
        <div ref={messagesEndRef} />

        {messages.length === 0 && (
          <div className="chat-empty">
            <svg width="80" height="80" viewBox="0 0 80 80" fill="none">
              <path d="M15 20C15 17.2386 17.2386 15 20 15H60C62.7614 15 65 17.2386 65 20V50C65 52.7614 62.7614 55 60 55H30L15 65V20Z" stroke="currentColor" strokeWidth="3"/>
              <circle cx="30" cy="35" r="3" fill="currentColor"/>
              <circle cx="40" cy="35" r="3" fill="currentColor"/>
              <circle cx="50" cy="35" r="3" fill="currentColor"/>
            </svg>
            <p>Пока нет сообщений</p>
            <span>Начните общение первым</span>
          </div>
        )}
      </div>

      <form className="premium-chat-input" onSubmit={handleSendMessage}>
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder="Введите сообщение..."
          maxLength={300}
          disabled={loading}
        />
        <button type="submit" disabled={loading || !newMessage.trim()}>
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M2 10L18 2L12 10L18 18L2 10Z" fill="currentColor"/>
          </svg>
        </button>
      </form>
    </div>
  )
}
