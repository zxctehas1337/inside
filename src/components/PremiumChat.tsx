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

  if (!currentUser) {
    return (
      <div className="premium-chat-locked">
        <div className="premium-chat-lock-icon">🔒</div>
        <h3>Премиум Чат</h3>
        <p>Войдите, чтобы получить доступ</p>
      </div>
    )
  }

  if (!hasAccess) {
    return (
      <div className="premium-chat-locked">
        <div className="premium-chat-lock-icon">⭐</div>
        <h3>Эксклюзивный Чат</h3>
        <p>Доступен только для Premium и Alpha пользователей</p>
        <div className="premium-features">
          <div className="premium-feature">
            <span className="feature-icon">💬</span>
            <span>Общение с элитой</span>
          </div>
          <div className="premium-feature">
            <span className="feature-icon">⚡</span>
            <span>Ранний доступ к новостям</span>
          </div>
          <div className="premium-feature">
            <span className="feature-icon">🎯</span>
            <span>Эксклюзивные обновления</span>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="premium-chat">
      <div className="premium-chat-header">
        <div className="premium-chat-title">
          <span className="premium-icon">⭐</span>
          <h3>Premium Chat</h3>
          <span className="online-count">{messages.length > 0 ? `${new Set(messages.map(m => m.user_id)).size} онлайн` : ''}</span>
        </div>
      </div>

      <div className="premium-chat-messages">
        {messages.map((msg, index) => {
          const isOwn = msg.user_id === currentUser.id
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
                      <span className="chat-badge alpha">⚡ Alpha</span>
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
            <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
              <path d="M12 16C12 13.7909 13.7909 12 16 12H48C50.2091 12 52 13.7909 52 16V40C52 42.2091 50.2091 44 48 44H24L12 52V16Z" stroke="currentColor" strokeWidth="2"/>
              <circle cx="24" cy="28" r="2" fill="currentColor"/>
              <circle cx="32" cy="28" r="2" fill="currentColor"/>
              <circle cx="40" cy="28" r="2" fill="currentColor"/>
            </svg>
            <p>Начните общение!</p>
          </div>
        )}
      </div>

      <form className="premium-chat-input" onSubmit={handleSendMessage}>
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder="Напишите сообщение..."
          maxLength={300}
          disabled={loading}
        />
        <button type="submit" disabled={loading || !newMessage.trim()}>
          <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
            <path d="M2 10L18 2L12 10L18 18L2 10Z"/>
          </svg>
        </button>
      </form>
    </div>
  )
}
