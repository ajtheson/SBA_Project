import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'
import HomePage from './pages/HomePage'
import RolesPage from './pages/RolesPage'
import CategoriesPage from './pages/CategoriesPage'
import TestTypesPage from './pages/TestTypesPage'
import QuestionLevelsPage from './pages/QuestionLevelsPage'
import LessonTypesPage from './pages/LessonTypesPage'

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/settings/roles" element={<RolesPage />} />
          <Route path="/settings/categories" element={<CategoriesPage />} />
          <Route path="/settings/test-types" element={<TestTypesPage />} />
          <Route path="/settings/question-levels" element={<QuestionLevelsPage />} />
          <Route path="/settings/lesson-types" element={<LessonTypesPage />} />
        </Routes>
        <ToastContainer position="top-right" autoClose={3000} />
      </div>
    </Router>
  )
}

export default App
