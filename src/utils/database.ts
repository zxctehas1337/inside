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
    console.log('🗑️  Database.deleteAccount called with userId:', userId)
    
    const result = await api.deleteUser(userId)
    console.log('🗑️  API deleteUser result:', result)
    
    if (result.success) {
      console.log('✅ Account deleted successfully')
      return { success: true, message: result.message || 'Аккаунт удален' }
    }
    
    console.error('❌ Failed to delete account:', result.message)
    return { success: false, message: result.message || 'Ошибка удаления аккаунта' }
  }
}

// Хранение текущего пользователя в памяти (session)
let currentUserSession: User | null = null

export const getCurrentUser = (): User | null => {
  return currentUserSession
}

export const setCurrentUser = (user: User | null) => {
  currentUserSession = user
}
