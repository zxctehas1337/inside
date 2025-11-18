import { User } from '../types'
import * as api from './api'

export class Database {
  constructor() {
    console.log('✅ Database initialized - using API only')
  }

  async updateUser(userId: number, updates: Partial<User>) {
    const result = await api.updateUser(userId, updates)
    
    if (result.success && result.data) {
      return { success: true, user: result.data }
    }
    
    return { success: false, message: result.message || 'Ошибка обновления пользователя' }
  }

  async getUserById(userId: number) {
    console.log('🔍 Database.getUserById called with userId:', userId)
    
    const result = await api.getUserInfo(userId)
    console.log('🔍 API getUserInfo result:', result)
    
    if (result.success && result.data) {
      console.log('✅ User found via API:', result.data)
      return { success: true, user: result.data }
    }
    
    console.error('❌ User not found, userId:', userId)
    return { success: false, message: result.message || 'Пользователь не найден' }
  }

  async deleteAccount(userId: number) {
    console.log('', userId)
    
    const result = await api.deleteUser(userId)
    console.log('', result)
    
    if (result.success) {
      console.log('✅ Account deleted successfully')
      return { success: true, message: result.message || 'Аккаунт удален' }
    }
    
    console.error('❌ Failed to delete account:', result.message)
    return { success: false, message: result.message || 'Ошибка удаления аккаунта' }
  }
}

// Хранение текущего пользователя в localStorage с JWT токеном
const USER_STORAGE_KEY = 'shakedown_user'
const TOKEN_STORAGE_KEY = 'shakedown_token'

export const getCurrentUser = (): User | null => {
  try {
    const userData = localStorage.getItem(USER_STORAGE_KEY)
    if (userData) {
      return JSON.parse(userData)
    }
  } catch (error) {
    console.error('Error reading user from localStorage:', error)
  }
  return null
}

export const setCurrentUser = (user: User | null) => {
  try {
    if (user) {
      // Сохраняем токен отдельно
      if ('token' in user) {
        localStorage.setItem(TOKEN_STORAGE_KEY, (user as any).token)
      }
      // Сохраняем пользователя без токена
      const userWithoutToken = { ...user }
      delete (userWithoutToken as any).token
      localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(userWithoutToken))
    } else {
      localStorage.removeItem(USER_STORAGE_KEY)
      localStorage.removeItem(TOKEN_STORAGE_KEY)
    }
  } catch (error) {
    console.error('Error saving user to localStorage:', error)
  }
}

export const getAuthToken = (): string | null => {
  return localStorage.getItem(TOKEN_STORAGE_KEY)
}
