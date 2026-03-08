import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import RegisterStudent from './pages/auth/RegisterStudent';
import RegisterTeacher from './pages/auth/RegisterTeacher';
import Activate from './pages/auth/Activate';
import ForgotPassword from './pages/auth/ForgotPassword';
import ResetPassword from './pages/auth/ResetPassword';
import QuizList from './pages/quiz/QuizList';
import CreateQuiz from './pages/quiz/CreateQuiz';
import ViewQuiz from './pages/quiz/ViewQuiz';
import UpdateQuiz from './pages/quiz/UpdateQuiz';
import OngoingExams from './pages/exam/OngoingExams';
import CompletedExams from './pages/exam/CompletedExams';
import CreateExam from './pages/exam/CreateExam';
import ExamResults from './pages/exam/ExamResults';
import OngoingExamDetail from './pages/exam/OngoingExamDetail';
import JoinExam from './pages/student/JoinExam';
import TakeExam from './pages/student/TakeExam';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            {/* Auth routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/register/student" element={<RegisterStudent />} />
            <Route path="/register/teacher" element={<RegisterTeacher />} />
            <Route path="/activate" element={<Activate />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />

            {/* Teacher Quiz routes */}
            <Route path="/teacher/quizzes" element={<ProtectedRoute role="teacher"><QuizList /></ProtectedRoute>} />
            <Route path="/teacher/quizzes/create" element={<ProtectedRoute role="teacher"><CreateQuiz /></ProtectedRoute>} />
            <Route path="/teacher/quizzes/:id" element={<ProtectedRoute role="teacher"><ViewQuiz /></ProtectedRoute>} />
            <Route path="/teacher/quizzes/:id/edit" element={<ProtectedRoute role="teacher"><UpdateQuiz /></ProtectedRoute>} />

            {/* Teacher Exam routes */}
            <Route path="/teacher/exams" element={<ProtectedRoute role="teacher"><OngoingExams /></ProtectedRoute>} />
            <Route path="/teacher/exams/create" element={<ProtectedRoute role="teacher"><CreateExam /></ProtectedRoute>} />
            <Route path="/teacher/exams/completed" element={<ProtectedRoute role="teacher"><CompletedExams /></ProtectedRoute>} />
            <Route path="/teacher/exams/:id/results" element={<ProtectedRoute role="teacher"><ExamResults /></ProtectedRoute>} />
            <Route path="/teacher/exams/ongoing/:id/detail" element={<ProtectedRoute role="teacher"><OngoingExamDetail /></ProtectedRoute>} />

            {/* Student Exam routes */}
            <Route path="/student/join" element={<ProtectedRoute role="student"><JoinExam /></ProtectedRoute>} />
            <Route path="/student/exams/:examId/take" element={<ProtectedRoute role="student"><TakeExam /></ProtectedRoute>} />

            {/* Default redirect */}
            <Route path="/" element={<Navigate to="/login" />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
