import { useState, useEffect } from 'react'
import { getCurrentUser } from '../utils/database'
import '../styles/CommentsSection.css'

interface Comment {
  id: number
  content: string
  created_at: string
  updated_at: string
  user_id: number
  username: string
  avatar: string
  subscription: string
  reactions: Array<{ reaction: string; count: number }>
}

interface CommentsSectionProps {
  newsId: number
}

const REACTIONS = ['👍', '❤️', '🔥', '😂', '😮', '😢']

export default function CommentsSection({ newsId }: CommentsSectionProps) {
  const [comments, setComments] = useState<Comment[]>([])
  const [newComment, setNewComment] = useState('')
  const [loading, setLoading] = useState(false)
  const [isOpen, setIsOpen] = useState(false)
  const [commentsCount, setCommentsCount] = useState(0)
  const currentUser = getCurrentUser()

  useEffect(() => {
    loadCommentsCount()
  }, [newsId])

  useEffect(() => {
    if (isOpen) {
      loadComments()
    }
  }, [isOpen, newsId])

  const loadCommentsCount = async () => {
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/api/news/${newsId}/comments`)
      const data = await response.json()
      if (data.success) {
        setCommentsCount(data.data.length)
      }
    } catch (error) {
      console.error('Failed to load comments count:', error)
    }
  }

  const loadComments = async () => {
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/api/news/${newsId}/comments`)
      const data = await response.json()
      if (data.success) {
        setComments(data.data)
        setCommentsCount(data.data.length)
      }
    } catch (error) {
      console.error('Failed to load comments:', error)
    }
  }

  const handleSubmitComment = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!currentUser || !newComment.trim()) return

    setLoading(true)
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/api/news/${newsId}/comments`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.id,
          content: newComment.trim()
        })
      })

      const data = await response.json()
      if (data.success) {
        setComments([data.data, ...comments])
        setNewComment('')
      }
    } catch (error) {
      console.error('Failed to post comment:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleReaction = async (commentId: number, reaction: string) => {
    if (!currentUser) return

    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/api/comments/${commentId}/reaction`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUser.id,
          reaction
        })
      })

      if (response.ok) {
        loadComments()
      }
    } catch (error) {
      console.error('Failed to add reaction:', error)
    }
  }

  const handleDeleteComment = async (commentId: number) => {
    if (!currentUser) return

    if (!confirm('Удалить комментарий?')) return

    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/api/comments/${commentId}`, {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: currentUser.id })
      })

      if (response.ok) {
        setComments(comments.filter(c => c.id !== commentId))
      }
    } catch (error) {
      console.error('Failed to delete comment:', error)
    }
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    const minutes = Math.floor(diff / 60000)
    const hours = Math.floor(diff / 3600000)
    const days = Math.floor(diff / 86400000)

    if (minutes < 1) return 'только что'
    if (minutes < 60) return `${minutes} мин назад`
    if (hours < 24) return `${hours} ч назад`
    if (days < 7) return `${days} д назад`
    return date.toLocaleDateString('ru-RU')
  }

  return (
    <div className="comments-section">
      <button 
        className="comments-toggle-btn"
        onClick={() => setIsOpen(!isOpen)}
      >
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
          <path d="M3 5C3 3.89543 3.89543 3 5 3H15C16.1046 3 17 3.89543 17 5V12C17 13.1046 16.1046 14 15 14H7L3 17V5Z" stroke="currentColor" strokeWidth="1.5"/>
        </svg>
        <span>Комментарии</span>
        {commentsCount > 0 && <span className="comments-count-badge">{commentsCount}</span>}
        <svg 
          className={`comments-toggle-icon ${isOpen ? 'open' : ''}`}
          width="16" 
          height="16" 
          viewBox="0 0 16 16" 
          fill="none"
        >
          <path d="M4 6L8 10L12 6" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
        </svg>
      </button>

      {isOpen && (
        <div className="comments-content">
          {currentUser ? (
            <form className="comment-form" onSubmit={handleSubmitComment}>
              <img src={currentUser.avatar || '/icon.ico'} alt="Avatar" className="comment-avatar" />
              <div className="comment-input-wrapper">
                <textarea
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                  placeholder="Напишите комментарий..."
                  className="comment-input"
                  rows={2}
                  maxLength={500}
                />
                <div className="comment-form-footer">
                  <span className="comment-char-count">{newComment.length}/500</span>
                  <button type="submit" disabled={loading || !newComment.trim()} className="comment-submit-btn">
                    {loading ? 'Отправка...' : 'Отправить'}
                  </button>
                </div>
              </div>
            </form>
          ) : (
            <div className="comment-login-prompt">
              <p>Войдите, чтобы оставить комментарий</p>
            </div>
          )}

          <div className="comments-list">
            {comments.map(comment => (
              <div key={comment.id} className="comment">
                <img src={comment.avatar || '/icon.ico'} alt={comment.username} className="comment-avatar" />
                <div className="comment-content">
                  <div className="comment-header">
                    <div className="comment-author">
                      <span className="comment-username">{comment.username}</span>
                      {comment.subscription !== 'free' && (
                        <span className={`comment-badge ${comment.subscription}`}>
                          {comment.subscription === 'alpha' ? 'Alpha' : 'Premium'}
                        </span>
                      )}
                    </div>
                    <span className="comment-date">{formatDate(comment.created_at)}</span>
                  </div>
                  <p className="comment-text">{comment.content}</p>
                  <div className="comment-actions">
                    <div className="comment-reactions">
                      {REACTIONS.map(reaction => {
                        const reactionData = comment.reactions.find(r => r.reaction === reaction)
                        const count = reactionData?.count || 0
                        return (
                          <button
                            key={reaction}
                            className={`reaction-btn ${count > 0 ? 'active' : ''}`}
                            onClick={() => handleReaction(comment.id, reaction)}
                            disabled={!currentUser}
                          >
                            {reaction} {count > 0 && <span className="reaction-count">{count}</span>}
                          </button>
                        )
                      })}
                    </div>
                    {currentUser && (currentUser.id === comment.user_id || currentUser.isAdmin) && (
                      <button className="comment-delete-btn" onClick={() => handleDeleteComment(comment.id)}>
                        Удалить
                      </button>
                    )}
                  </div>
                </div>
              </div>
            ))}

            {comments.length === 0 && (
              <div className="comments-empty">
                <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                  <path d="M8 12C8 9.79086 9.79086 8 12 8H36C38.2091 8 40 9.79086 40 12V28C40 30.2091 38.2091 32 36 32H16L8 40V12Z" stroke="currentColor" strokeWidth="2"/>
                </svg>
                <p>Пока нет комментариев</p>
                <span>Будьте первым!</span>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
