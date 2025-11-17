import { trackEvent } from './api'

let currentUserId: number | undefined = undefined

export const setAnalyticsUserId = (userId: number | undefined) => {
  currentUserId = userId
}

export const trackPageView = (page: string) => {
  trackEvent('page_view', page, undefined, currentUserId)
}

export const trackClick = (element: string, additionalData?: any) => {
  trackEvent('click', window.location.pathname, { element, ...additionalData }, currentUserId)
}

export const trackButtonClick = (buttonName: string) => {
  trackClick(`button_${buttonName}`)
}

export const trackLinkClick = (linkName: string) => {
  trackClick(`link_${linkName}`)
}

export const trackFormSubmit = (formName: string) => {
  trackEvent('form_submit', window.location.pathname, { form: formName }, currentUserId)
}

export const trackError = (errorMessage: string) => {
  trackEvent('error', window.location.pathname, { error: errorMessage }, currentUserId)
}

// Автоматическое отслеживание времени на странице
let pageStartTime = Date.now()

export const startPageTimer = () => {
  pageStartTime = Date.now()
}

export const trackPageExit = () => {
  const timeSpent = Math.floor((Date.now() - pageStartTime) / 1000)
  trackEvent('page_exit', window.location.pathname, { timeSpent }, currentUserId)
}

// Инициализация отслеживания
export const initAnalytics = (userId?: number) => {
  setAnalyticsUserId(userId)
  startPageTimer()
  
  // Отслеживание выхода со страницы
  window.addEventListener('beforeunload', trackPageExit)
}
