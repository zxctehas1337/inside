import React from 'react'
import '../styles/LogoutModal.css'

interface LogoutModalProps {
  isOpen: boolean
  onConfirm: () => void
  onCancel: () => void
}

export const LogoutModal: React.FC<LogoutModalProps> = ({ isOpen, onConfirm, onCancel }) => {
  if (!isOpen) return null

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h3>Подтверждение выхода</h3>
        </div>
        <div className="modal-body">
          <p>Вы уверены, что хотите выйти из аккаунта?</p>
        </div>
        <div className="modal-footer">
          <button className="btn-cancel" onClick={onCancel}>
            Отмена
          </button>
          <button className="btn-confirm" onClick={onConfirm}>
            Выйти
          </button>
        </div>
      </div>
    </div>
  )
}
