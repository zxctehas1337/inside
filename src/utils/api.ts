// API для работы с backend
const API_URL = import.meta.env.VITE_API_URL || 'https://insidenew.onrender.com'

export interface ApiResponse<T> {
  success: boolean
  data?: T
  message?: string
}

// Регистрация пользователя
export async function registerUser(username: string, email: string, password: string) {
  try {
    const response = await fetch(`${API_URL}/api/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, email, password }),
    })
    return await response.json()
  } catch (error) {
    console.error('Registration error:', error)
    return { success: false, message: 'Ошибка подключения к серверу' }
  }
}

// Вход пользователя
export async function loginUser(usernameOrEmail: string, password: string) {
  try {
    const response = await fetch(`${API_URL}/api/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ usernameOrEmail, password }),
    })
    return await response.json()
  } catch (error) {
    console.error('Login error:', error)
    return { success: false, message: 'Ошибка подключения к серверу' }
  }
}

// Обновление пользователя
export async function updateUser(userId: number, updates: any) {
  try {
    const response = await fetch(`${API_URL}/api/users/${userId}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(updates),
    })
    return await response.json()
  } catch (error) {
    console.error('Update error:', error)
    return { success: false, message: 'Ошибка подключения к серверу' }
  }
}

// Получить информацию о пользователе
export async function getUserInfo(userId: number) {
  try {
    const response = await fetch(`${API_URL}/api/users/${userId}`)
    return await response.json()
  } catch (error) {
    console.error('Get user error:', error)
    return { success: false, message: 'Ошибка подключения к серверу' }
  }
}

// Проверка доступности сервера
export async function checkServerHealth() {
  try {
    const response = await fetch(`${API_URL}/api/health`)
    return response.ok
  } catch (error) {
    return false
  }
}

// Получить всех пользователей (для админки)
export async function getAllUsers() {
  try {
    const response = await fetch(`${API_URL}/api/users`)
    return await response.json()
  } catch (error) {
    console.error('Get all users error:', error)
    return { success: false, message: 'Ошибка подключения к серверу' }
  }
}

// Подтверждение email по коду
export async function verifyEmailCode(userId: number, code: string) {
  try {
    const response = await fetch(`${API_URL}/api/auth/verify-code`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ userId, code }),
    })
    return await response.json()
  } catch (error) {
    console.error('Verify code error:', error)
    return { success: false, message: 'Ошибка подключения к серверу' }
  }
}

// Повторная отправка кода
export async function resendVerificationCode(userId: number) {
  try {
    const response = await fetch(`${API_URL}/api/auth/resend-code`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ userId }),
    })
    return await response.json()
  } catch (error) {
    console.error('Resend code error:', error)
    return { success: false, message: 'Ошибка подключения к серверу' }
  }
}
