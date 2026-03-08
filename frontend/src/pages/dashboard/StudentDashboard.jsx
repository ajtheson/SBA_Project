import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/axios';

export default function StudentDashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const res = await api.get('/api/student/dashboard');
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
        <h2>Welcome, {data.fullname}!</h2>
      </div>

      <div className="dashboard-grid">
        <div className="dashboard-card">
          <div className="dashboard-card-value">{data.totalExams}</div>
          <div className="dashboard-card-label">Exams Taken</div>
        </div>
        <div className="dashboard-card">
          <div className="dashboard-card-value">{data.totalSubmissions}</div>
          <div className="dashboard-card-label">Total Submissions</div>
        </div>
      </div>

      <div className="dashboard-links">
        <h3>Quick Actions</h3>
        <div className="quick-actions">
          <Link to="/student/join" className="btn btn-primary">Join Exam</Link>
          <Link to="/student/submissions" className="btn btn-secondary">Submission History</Link>
          <Link to="/student/settings" className="btn btn-secondary">Settings</Link>
        </div>
      </div>
    </div>
  );
}
