import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const teacherMenu = [
  { path: '/teacher/dashboard', label: 'Dashboard', icon: '📊' },
  { path: '/teacher/quizzes', label: 'Quizzes', icon: '📝' },
  { path: '/teacher/exams/ongoing', label: 'Ongoing Exams', icon: '▶️' },
  { path: '/teacher/exams/completed', label: 'Completed Exams', icon: '✅' },
  { path: '/teacher/settings', label: 'Settings', icon: '⚙️' },
];

const studentMenu = [
  { path: '/student/dashboard', label: 'Dashboard', icon: '📊' },
  { path: '/student/join', label: 'Join Exam', icon: '🎯' },
  { path: '/student/submissions', label: 'My Submissions', icon: '📋' },
  { path: '/student/settings', label: 'Settings', icon: '⚙️' },
];

export default function Layout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const menu = user?.role === 'teacher' ? teacherMenu : studentMenu;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="layout">
      <aside className="sidebar">
        <div className="sidebar-brand">
          <h2>Quiz Online</h2>
        </div>
        <nav className="sidebar-nav">
          {menu.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
            >
              <span className="sidebar-icon">{item.icon}</span>
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <div className="sidebar-user">
            <span className="sidebar-user-role">{user?.role === 'teacher' ? 'Teacher' : 'Student'}</span>
            <span className="sidebar-user-email">{user?.email}</span>
          </div>
          <button className="sidebar-logout" onClick={handleLogout}>Logout</button>
        </div>
      </aside>
      <main className="main-content">
        {children}
      </main>
    </div>
  );
}
