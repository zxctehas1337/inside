export interface User {
  id: number
  username: string
  email: string
  password: string
  subscription: 'free' | 'premium' | 'alpha'
  registeredAt: string
  avatar?: string
  settings: UserSettings
  isAdmin?: boolean
  isBanned?: boolean
  emailVerified?: boolean
}

export interface UserSettings {
  notifications: boolean
  autoUpdate: boolean
  theme: 'dark' | 'light' | 'auto'
  language: 'ru' | 'en' | 'uk'
}

export type NotificationType = 'success' | 'error' | 'info'

export interface NewsPost {
  id: number
  title: string
  content: string
  date: string
  author: string
  type: 'launcher' | 'website'
}
