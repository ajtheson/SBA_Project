import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/axios';

export default function TeacherDashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const res = await api.get('/api/teacher/dashboard');
        setData(res.data);
      } catch {
        setData(null);
      } finally {
        setLoading(false);
      }
    };
    fetchDashboard();
  }, []);

  if (loading) return <div className="page-container"><p>Loading...</p></div>;
  if (!data) return <div className="page-container"><p>Failed to load dashboard.</p></div>;

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Teacher Dashboard</h2>
      </div>

      <div className="dashboard-grid">
        <div className="dashboard-card">
          <div className="dashboard-card-value">{data.totalQuizzes}</div>
          <div className="dashboard-card-label">Total Quizzes</div>
        </div>
        <div className="dashboard-card">
          <div className="dashboard-card-value">{data.validQuizzes}</div>
          <div className="dashboard-card-label">Active Quizzes</div>
        </div>
        <div className="dashboard-card">
          <div className="dashboard-card-value">{data.usedQuizzes}</div>
          <div className="dashboard-card-label">Used in Exams</div>
        </div>
        <div className="dashboard-card">
          <div className="dashboard-card-value">{data.totalExams}</div>
          <div className="dashboard-card-label">Total Exams</div>
        </div>
        <div className="dashboard-card">
          <div className="dashboard-card-value">{data.onGoingExams}</div>
          <div className="dashboard-card-label">Ongoing Exams</div>
        </div>
        <div className="dashboard-card">
          <div className="dashboard-card-value">{data.totalSubmissions}</div>
          <div className="dashboard-card-label">Total Submissions</div>
        </div>
      </div>

      <div className="dashboard-links">
        <h3>Quick Actions</h3>
        <div className="quick-actions">
          <Link to="/teacher/quizzes" className="btn btn-primary">Manage Quizzes</Link>
          <Link to="/teacher/exams/ongoing" className="btn btn-primary">Ongoing Exams</Link>
          <Link to="/teacher/exams/completed" className="btn btn-secondary">Completed Exams</Link>
          <Link to="/teacher/settings" className="btn btn-secondary">Settings</Link>
        </div>
      </div>
    </div>
  );
}
