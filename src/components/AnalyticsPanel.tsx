import { useEffect, useState } from 'react'
import { getAnalyticsStats } from '../utils/api'
import '../styles/AnalyticsPanel.css'

interface AnalyticsData {
  totalVisits: number
  uniqueUsers: number
  weeklyVisits: { date: string; visits: number }[]
  popularPages: { page: string; visits: number }[]
  clickEvents: { element: string; clicks: number }[]
  avgSessionTime: number
  hourlyActivity: { hour: number; activity: number }[]
}

export default function AnalyticsPanel() {
  const [analytics, setAnalytics] = useState<AnalyticsData | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadAnalytics()
    const interval = setInterval(loadAnalytics, 30000) // Обновление каждые 30 секунд
    return () => clearInterval(interval)
  }, [])

  const loadAnalytics = async () => {
    const result = await getAnalyticsStats()
    if (result.success) {
      setAnalytics(result.data)
    }
    setLoading(false)
  }

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60)
    const secs = Math.floor(seconds % 60)
    return `${mins}м ${secs}с`
  }

  if (loading) {
    return <div className="analytics-loading">Загрузка аналитики...</div>
  }

  if (!analytics) {
    return <div className="analytics-error">Ошибка загрузки аналитики</div>
  }

  const maxWeeklyVisits = Math.max(...analytics.weeklyVisits.map(d => d.visits), 1)
  const maxHourlyActivity = Math.max(...analytics.hourlyActivity.map(d => d.activity), 1)

  return (
    <div className="analytics-panel">
      <h2>Аналитика сайта</h2>

      {/* Основные метрики */}
      <div className="analytics-stats">
        <div className="stat-card">
          <div className="stat-icon"></div>
          <div className="stat-info">
            <div className="stat-value">{analytics.uniqueUsers}</div>
            <div className="stat-label">Уникальных пользователей</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon"></div>
          <div className="stat-info">
            <div className="stat-value">{analytics.totalVisits}</div>
            <div className="stat-label">Всего посещений</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon"></div>
          <div className="stat-info">
            <div className="stat-value">{formatTime(analytics.avgSessionTime)}</div>
            <div className="stat-label">Среднее время на сайте</div>
          </div>
        </div>
      </div>

      {/* График посещений за неделю */}
      <div className="analytics-section">
        <h3>Посещения за последние 7 дней</h3>
        <div className="chart-container">
          <div className="bar-chart">
            {analytics.weeklyVisits.map((day, index) => (
              <div key={index} className="bar-item">
                <div className="bar-wrapper">
                  <div
                    className="bar"
                    style={{ height: `${(day.visits / maxWeeklyVisits) * 100}%` }}
                  >
                    <span className="bar-value">{day.visits}</span>
                  </div>
                </div>
                <div className="bar-label">
                  {new Date(day.date).toLocaleDateString('ru', { day: '2-digit', month: '2-digit' })}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Активность по часам */}
      <div className="analytics-section">
        <h3>Активность за последние 24 часа</h3>
        <div className="chart-container">
          <div className="line-chart">
            {Array.from({ length: 24 }, (_, i) => {
              const hourData = analytics.hourlyActivity.find(h => h.hour === i)
              const activity = hourData?.activity || 0
              return (
                <div key={i} className="line-item">
                  <div className="line-wrapper">
                    <div
                      className="line-bar"
                      style={{ height: `${(activity / maxHourlyActivity) * 100}%` }}
                    >
                      {activity > 0 && <span className="line-value">{activity}</span>}
                    </div>
                  </div>
                  <div className="line-label">{i}:00</div>
                </div>
              )
            })}
          </div>
        </div>
      </div>

      {/* Популярные страницы */}
      <div className="analytics-section">
        <h3>Популярные страницы</h3>
        <div className="list-container">
          {analytics.popularPages.map((page, index) => (
            <div key={index} className="list-item">
              <div className="list-rank">#{index + 1}</div>
              <div className="list-name">{page.page || 'Главная'}</div>
              <div className="list-value">{page.visits} посещений</div>
            </div>
          ))}
        </div>
      </div>

      {/* Клики по элементам */}
      {analytics.clickEvents.length > 0 && (
        <div className="analytics-section">
          <h3>Популярные элементы</h3>
          <div className="list-container">
            {analytics.clickEvents.map((event, index) => (
              <div key={index} className="list-item">
                <div className="list-rank">#{index + 1}</div>
                <div className="list-name">{event.element}</div>
                <div className="list-value">{event.clicks} кликов</div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
