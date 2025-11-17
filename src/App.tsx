import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import HomePage from './pages/HomePage.tsx'
import AuthPage from './pages/AuthPage.tsx'
import DashboardPage from './pages/DashboardPage.tsx'
import AdminPage from './pages/AdminPage.tsx'
import NewsPage from './pages/NewsPage.tsx'
import PremiumChatPage from './pages/PremiumChatPage.tsx'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/auth" element={<AuthPage />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/admin" element={<AdminPage />} />
        <Route path="/news" element={<NewsPage />} />
        <Route path="/premium-chat" element={<PremiumChatPage />} />
      </Routes>
    </Router>
  )
}

export default App
