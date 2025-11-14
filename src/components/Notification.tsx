import { useEffect } from 'react'
import { NotificationType } from '../types'
import '../styles/Notification.css'

interface NotificationProps {
  message: string
  type: NotificationType
  onClose: () => void
}

export default function Notification({ message, type, onClose }: NotificationProps) {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose()
    }, 3000)

    return () => clearTimeout(timer)
  }, [onClose])

  return (
    <div className={`notification notification-${type} show`}>
      <div className="notification-content">
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
          {type === 'success' ? (
            <path d="M16 6L8 14L4 10" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
          ) : (
            <>
              <path d="M10 6V10M10 14H10.01" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              <circle cx="10" cy="10" r="8" stroke="currentColor" strokeWidth="2"/>
            </>
          )}
        </svg>
        <span>{message}</span>
      </div>
    </div>
  )
}
