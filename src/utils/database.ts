import { User } from '../types'

export class Database {
  private users: User[]

  constructor() {
    this.users = JSON.parse(localStorage.getItem('insideUsers') || '[]')
  }

  save() {
    localStorage.setItem('insideUsers', JSON.stringify(this.users))
  }

  register(username: string, email: string, password: string) {
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

    return { success: true, message: 'Регистрация успешна!', user }
  }

  login(usernameOrEmail: string, password: string) {
    const user = this.users.find(u =>
      (u.username === usernameOrEmail || u.email === usernameOrEmail) &&
      u.password === btoa(password)
    )

    if (user) {
      return { success: true, message: 'Вход выполнен!', user }
    }

    return { success: false, message: 'Неверный логин или пароль' }
  }

  updateUser(userId: number, updates: Partial<User>) {
    const userIndex = this.users.findIndex(u => u.id === userId)
    if (userIndex !== -1) {
      this.users[userIndex] = { ...this.users[userIndex], ...updates }
      this.save()
      return { success: true, user: this.users[userIndex] }
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
