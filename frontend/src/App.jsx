import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';
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
import TeacherSubmissionDetail from './pages/exam/TeacherSubmissionDetail';
import TeacherDashboard from './pages/dashboard/TeacherDashboard';
import StudentDashboard from './pages/dashboard/StudentDashboard';
import TeacherSettings from './pages/settings/TeacherSettings';
import StudentSettings from './pages/settings/StudentSettings';
import JoinExam from './pages/student/JoinExam';
import TakeExam from './pages/student/TakeExam';
import SubmissionHistory from './pages/student/SubmissionHistory';
import SubmissionDetail from './pages/student/SubmissionDetail';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            {/* Auth routes (no sidebar) */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/register/student" element={<RegisterStudent />} />
            <Route path="/register/teacher" element={<RegisterTeacher />} />
            <Route path="/activate" element={<Activate />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/reset-password" element={<ResetPassword />} />

            {/* Teacher routes (with sidebar) */}
            <Route path="/teacher/dashboard" element={<ProtectedRoute role="teacher"><Layout><TeacherDashboard /></Layout></ProtectedRoute>} />
            <Route path="/teacher/quizzes" element={<ProtectedRoute role="teacher"><Layout><QuizList /></Layout></ProtectedRoute>} />
            <Route path="/teacher/quizzes/create" element={<ProtectedRoute role="teacher"><Layout><CreateQuiz /></Layout></ProtectedRoute>} />
            <Route path="/teacher/quizzes/:id" element={<ProtectedRoute role="teacher"><Layout><ViewQuiz /></Layout></ProtectedRoute>} />
            <Route path="/teacher/quizzes/:id/edit" element={<ProtectedRoute role="teacher"><Layout><UpdateQuiz /></Layout></ProtectedRoute>} />
            <Route path="/teacher/exams/ongoing" element={<ProtectedRoute role="teacher"><Layout><OngoingExams /></Layout></ProtectedRoute>} />
            <Route path="/teacher/exams/create" element={<ProtectedRoute role="teacher"><Layout><CreateExam /></Layout></ProtectedRoute>} />
            <Route path="/teacher/exams/completed" element={<ProtectedRoute role="teacher"><Layout><CompletedExams /></Layout></ProtectedRoute>} />
            <Route path="/teacher/exams/:id/results" element={<ProtectedRoute role="teacher"><Layout><ExamResults /></Layout></ProtectedRoute>} />
            <Route path="/teacher/exams/ongoing/:id/detail" element={<ProtectedRoute role="teacher"><Layout><OngoingExamDetail /></Layout></ProtectedRoute>} />
            <Route path="/teacher/submissions/:submissionId" element={<ProtectedRoute role="teacher"><Layout><TeacherSubmissionDetail /></Layout></ProtectedRoute>} />
            <Route path="/teacher/settings" element={<ProtectedRoute role="teacher"><Layout><TeacherSettings /></Layout></ProtectedRoute>} />

            {/* Student routes (with sidebar) */}
            <Route path="/student/dashboard" element={<ProtectedRoute role="student"><Layout><StudentDashboard /></Layout></ProtectedRoute>} />
            <Route path="/student/join" element={<ProtectedRoute role="student"><Layout><JoinExam /></Layout></ProtectedRoute>} />
            <Route path="/student/exams/:examId/take" element={<ProtectedRoute role="student"><Layout><TakeExam /></Layout></ProtectedRoute>} />
            <Route path="/student/submissions" element={<ProtectedRoute role="student"><Layout><SubmissionHistory /></Layout></ProtectedRoute>} />
            <Route path="/student/submissions/:submissionId" element={<ProtectedRoute role="student"><Layout><SubmissionDetail /></Layout></ProtectedRoute>} />
            <Route path="/student/settings" element={<ProtectedRoute role="student"><Layout><StudentSettings /></Layout></ProtectedRoute>} />

            {/* Default redirect */}
            <Route path="/" element={<Navigate to="/login" />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
