import { useState } from 'react'
import '../styles/DeleteAccountModal.css'

interface DeleteAccountModalProps {
  isOpen: boolean
  onConfirm: () => void
  onCancel: () => void
}

export function DeleteAccountModal({ isOpen, onConfirm, onCancel }: DeleteAccountModalProps) {
  const [confirmText, setConfirmText] = useState('')
  const [isDeleting, setIsDeleting] = useState(false)

  if (!isOpen) return null

  const handleConfirm = async () => {
    if (confirmText.toLowerCase() === 'удалить') {
      setIsDeleting(true)
      await onConfirm()
      setIsDeleting(false)
    }
  }

  const isConfirmValid = confirmText.toLowerCase() === 'удалить'

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="delete-modal" onClick={(e) => e.stopPropagation()}>
        <div className="delete-modal-icon">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
            <circle cx="24" cy="24" r="22" stroke="#ef4444" strokeWidth="2"/>
            <path d="M24 16V26M24 32H24.02" stroke="#ef4444" strokeWidth="2" strokeLinecap="round"/>
          </svg>
        </div>
        
        <h2>Удалить аккаунт?</h2>
        <p className="delete-warning">
          Это действие необратимо! Все ваши данные будут безвозвратно удалены из базы данных.
        </p>
        
        <div className="delete-info">
          <p>Будет удалено:</p>
          <ul>
            <li>Профиль и личные данные</li>
            <li>История активности</li>
            <li>Настройки и предпочтения</li>
            <li>Подписка и доступы</li>
          </ul>
        </div>

        <div className="confirm-input-group">
          <label htmlFor="confirmDelete">
            Введите <strong>удалить</strong> для подтверждения:
          </label>
          <input
            id="confirmDelete"
            type="text"
            value={confirmText}
            onChange={(e) => setConfirmText(e.target.value)}
            placeholder="удалить"
            autoComplete="off"
            disabled={isDeleting}
          />
        </div>

        <div className="delete-modal-actions">
          <button 
            className="btn btn-secondary" 
            onClick={onCancel}
            disabled={isDeleting}
          >
            Отмена
          </button>
          <button 
            className="btn btn-danger" 
            onClick={handleConfirm}
            disabled={!isConfirmValid || isDeleting}
          >
            {isDeleting ? (
              <>
                <span className="spinner"></span>
                Удаление...
              </>
            ) : (
              'Удалить навсегда'
            )}
          </button>
        </div>
      </div>
    </div>
  )
}
