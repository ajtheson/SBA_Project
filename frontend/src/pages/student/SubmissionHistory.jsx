import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/axios';

export default function SubmissionHistory() {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const res = await api.get('/api/student/exams/history');
        setSubmissions(res.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to load submission history');
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, []);

  const formatDateTime = (dt) => {
    if (!dt) return '-';
    return new Date(dt).toLocaleString('vi-VN');
  };

  const formatDuration = (seconds) => {
    if (!seconds) return '-';
    const min = Math.floor(seconds / 60);
    const sec = seconds % 60;
    return `${min}m ${sec}s`;
  };

  if (loading) return <div className="page-container"><p>Loading...</p></div>;

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Submission History</h2>
        <Link to="/student/join" className="btn btn-primary">Join Exam</Link>
      </div>

      {error && <div className="error-message">{error}</div>}

      {submissions.length === 0 ? (
        <p className="empty-text">No submissions yet. Join an exam to get started!</p>
      ) : (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Exam</th>
                <th>Score</th>
                <th>Correct</th>
                <th>Duration</th>
                <th>Submit Time</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {submissions.map((sub, index) => (
                <tr key={sub.submissionId}>
                  <td>{index + 1}</td>
                  <td>{sub.examName}</td>
                  <td><strong>{sub.score}</strong></td>
                  <td>{sub.correctAnswers}/{sub.selected}</td>
                  <td>{formatDuration(sub.duration)}</td>
                  <td>{formatDateTime(sub.submitTime)}</td>
                  <td>
                    {sub.isReview ? (
                      <Link to={`/student/submissions/${sub.submissionId}`} className="btn btn-sm btn-primary">
                        View Detail
                      </Link>
                    ) : (
                      <span className="text-muted">Review OFF</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
