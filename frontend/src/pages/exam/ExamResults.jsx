import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../../api/axios';

export default function ExamResults() {
  const { id } = useParams();
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [sortField, setSortField] = useState('score');
  const [sortDir, setSortDir] = useState('desc');

  useEffect(() => {
    const fetchResults = async () => {
      try {
        const res = await api.get(`/api/teacher/exams/${id}/results`);
        setResult(res.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to load results');
      } finally {
        setLoading(false);
      }
    };
    fetchResults();
  }, [id]);

  const handleSort = (field) => {
    if (sortField === field) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDir('desc');
    }
  };

  const getSortedSubmissions = () => {
    if (!result?.submissions) return [];
    return [...result.submissions].sort((a, b) => {
      let valA = a[sortField] ?? 0;
      let valB = b[sortField] ?? 0;
      if (sortDir === 'asc') return valA - valB;
      return valB - valA;
    });
  };

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
        <h2>Exam Results</h2>
        <Link to="/teacher/exams/completed" className="btn btn-secondary">Back to Completed</Link>
      </div>

      {error && <div className="error-message">{error}</div>}

      {result && (
        <>
          <div className="stats-row">
            <div className="stat-card">
              <div className="stat-label">Exam</div>
              <div className="stat-value">{result.examName}</div>
            </div>
            <div className="stat-card">
              <div className="stat-label">Total Submissions</div>
              <div className="stat-value">{result.totalSubmission}</div>
            </div>
            <div className="stat-card">
              <div className="stat-label">Average Score</div>
              <div className="stat-value">{result.averageScore}</div>
            </div>
            <div className="stat-card">
              <div className="stat-label">Highest Score</div>
              <div className="stat-value">{result.highestScore}</div>
            </div>
          </div>

          {result.submissions.length === 0 ? (
            <p className="empty-text">No submissions yet.</p>
          ) : (
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Student</th>
                    <th>Email</th>
                    <th className="sortable" onClick={() => handleSort('score')}>
                      Score {sortField === 'score' && (sortDir === 'asc' ? '↑' : '↓')}
                    </th>
                    <th>Correct</th>
                    <th className="sortable" onClick={() => handleSort('duration')}>
                      Duration {sortField === 'duration' && (sortDir === 'asc' ? '↑' : '↓')}
                    </th>
                    <th>Submit Time</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {getSortedSubmissions().map((sub, index) => (
                    <tr key={sub.submissionId}>
                      <td>{index + 1}</td>
                      <td>{sub.studentName}</td>
                      <td>{sub.studentEmail}</td>
                      <td><strong>{sub.score}</strong></td>
                      <td>{sub.correctAnswers}/{sub.selected}</td>
                      <td>{formatDuration(sub.duration)}</td>
                      <td>{formatDateTime(sub.submitTime)}</td>
                      <td>
                        <Link to={`/teacher/submissions/${sub.submissionId}`} className="btn btn-sm btn-primary">
                          View Detail
                        </Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </>
      )}
    </div>
  );
}
