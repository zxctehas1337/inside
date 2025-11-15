import { User } from '../types'
import * as api from './api'

export class Database {
  private users: User[]
  private useApi: boolean = false

  constructor() {
    this.users = JSON.parse(localStorage.getItem('insideUsers') || '[]')
    // Проверяем доступность API при инициализации
    this.checkApiAvailability()
  }

  private async checkApiAvailability() {
    this.useApi = await api.checkServerHealth()
    if (this.useApi) {
      console.log('✅ Подключено к серверу базы данных')
    } else {
      console.log('⚠️ Работа в автономном режиме (localStorage)')
    }
  }

  save() {
    localStorage.setItem('insideUsers', JSON.stringify(this.users))
  }

  async register(username: string, email: string, password: string) {
    // Пробуем использовать API
    if (this.useApi) {
      const result = await api.registerUser(username, email, password)
      if (result.success && result.data) {
        return { 
          success: true, 
          message: result.message || 'Регистрация успешна!', 
          user: result.data,
          requiresVerification: (result as any).requiresVerification || false
        }
      }
      // Если API не сработал, fallback на localStorage
      this.useApi = false
    }

    // Fallback на localStorage
    if (this.users.find(u => u.username === username)) {
      return { success: false, message: 'Пользователь с таким логином уже существует' }
    }

    if (this.users.find(u => u.email === email)) {
      return { success: false, message: 'Email уже зарегистрирован' }
    }

    const user: User = {
      id: Date.now(),
      username,
      email,
      password: btoa(password),
      subscription: 'free',
      registeredAt: new Date().toISOString(),
      settings: {
        notifications: true,
        autoUpdate: true,
        theme: 'dark',
        language: 'ru'
      }
    }

    this.users.push(user)
    this.save()

    return { success: true, message: 'Регистрация успешна!', user, requiresVerification: false }
  }

  async login(usernameOrEmail: string, password: string) {
    // Проверка админа
    if (usernameOrEmail === 'admin' && password === 'InsideSecurity208009') {
      const adminUser: User = {
        id: 0,
        username: 'Administrator',
        email: 'admin@inside.local',
        password: btoa(password),
        subscription: 'alpha',
        registeredAt: new Date().toISOString(),
        isAdmin: true,
        settings: {
          notifications: true,
          autoUpdate: true,
          theme: 'dark',
          language: 'ru'
        }
      }
      return { success: true, message: 'Добро пожаловать, администратор!', user: adminUser }
    }

    // Пробуем использовать API
    if (this.useApi) {
      const result = await api.loginUser(usernameOrEmail, password)
      if (result.success && result.data) {
        return { success: true, message: 'Вход выполнен!', user: result.data }
      }
      // Если API не сработал, fallback на localStorage
      this.useApi = false
    }

    // Fallback на localStorage
    const user = this.users.find(u =>
      (u.username === usernameOrEmail || u.email === usernameOrEmail) &&
      u.password === btoa(password)
    )

    if (user) {
      // Проверка на бан
      if (user.isBanned) {
        return { success: false, message: 'Ваш аккаунт заблокирован' }
      }
      return { success: true, message: 'Вход выполнен!', user }
    }

    return { success: false, message: 'Неверный логин или пароль' }
  }

  async updateUser(userId: number, updates: Partial<User>) {
    // Пробуем использовать API
    if (this.useApi) {
      const result = await api.updateUser(userId, updates)
      if (result.success && result.data) {
        return { success: true, user: result.data }
      }
      // Если API не сработал, fallback на localStorage
      this.useApi = false
    }

    // Fallback на localStorage
    const userIndex = this.users.findIndex(u => u.id === userId)
    if (userIndex !== -1) {
      this.users[userIndex] = { ...this.users[userIndex], ...updates }
      this.save()
      return { success: true, user: this.users[userIndex] }
    }
    return { success: false, message: 'Пользователь не найден' }
  }

  async getUserById(userId: number) {
    // Пробуем использовать API
    if (this.useApi) {
      const result = await api.getUserInfo(userId)
      if (result.success && result.data) {
        return { success: true, user: result.data }
      }
      // Если API не сработал, fallback на localStorage
      this.useApi = false
    }

    // Fallback на localStorage
    const user = this.users.find(u => u.id === userId)
    if (user) {
      return { success: true, user }
    }
    return { success: false, message: 'Пользователь не найден' }
  }
}

export const getCurrentUser = (): User | null => {
  const userStr = localStorage.getItem('currentUser')
  return userStr ? JSON.parse(userStr) : null
}

export const setCurrentUser = (user: User | null) => {
  if (user) {
    localStorage.setItem('currentUser', JSON.stringify(user))
  } else {
    localStorage.removeItem('currentUser')
  }
}
