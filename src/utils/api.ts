// API для работы с backend
const API_URL = import.meta.env.VITE_API_URL || 'https://oneshakedown.onrender.com'
console.log('🔧 API_URL configured as:', API_URL)

export interface ApiResponse<T> {
  success: boolean
  data?: T
  message?: string
}

// Google OAuth - вход происходит через редирект на /api/auth/google

// Запись события аналитики
export async function trackEvent(eventType: string, page?: string, data?: any, userId?: number) {
  try {
    const response = await fetch(`${API_URL}/api/analytics`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ userId, eventType, page, data }),
    })
    return await response.json()
  } catch (error) {
    console.error('Track event error:', error)
    return { success: false }
  }
}

// Получение статистики аналитики
export async function getAnalyticsStats() {
  try {
    const response = await fetch(`${API_URL}/api/analytics/stats`)
    return await response.json()
  } catch (error) {
    console.error('Get analytics stats error:', error)
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
    const url = `${API_URL}/api/users/${userId}`
    console.log('🔍 API.getUserInfo: Fetching from URL:', url)
    const response = await fetch(url)
    const data = await response.json()
    console.log('🔍 API.getUserInfo: Response:', data)
    return data
  } catch (error) {
    console.error('❌ Get user error:', error)
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



// Изменение подписки пользователя
export async function changeUserSubscription(userId: number, subscription: 'free' | 'premium' | 'alpha') {
  try {
    const response = await fetch(`${API_URL}/api/users/${userId}/subscription`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ subscription }),
    })
    return await response.json()
  } catch (error) {
    console.error('Change subscription error:', error)
    return { success: false, message: 'Ошибка подключения к серверу' }
  }
}

// Удаление пользователя
export async function deleteUser(userId: number) {
  try {
    const response = await fetch(`${API_URL}/api/users/${userId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      },
    })
    return await response.json()
  } catch (error) {
    console.error('Delete user error:', error)
    return { success: false, message: 'Ошибка подключения к серверу' }
  }
}
