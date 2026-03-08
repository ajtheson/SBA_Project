import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/axios';

export default function OngoingExams() {
  const [exams, setExams] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const fetchExams = async () => {
    try {
      setLoading(true);
      const res = await api.get('/api/teacher/exams');
      setExams(res.data);
    } catch (err) {
      setError('Failed to load exams');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchExams(); }, []);

  const handleSearch = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      if (search.trim()) {
        const res = await api.get(`/api/teacher/exams/search?name=${encodeURIComponent(search)}`);
        setExams(res.data);
      } else {
        await fetchExams();
      }
    } catch (err) {
      setError('Search failed');
    } finally {
      setLoading(false);
    }
  };

  const handleEnd = async (examId, examName) => {
    if (!window.confirm(`End exam "${examName}"? Students will no longer be able to submit.`)) return;
    try {
      await api.put(`/api/teacher/exams/${examId}/end`);
      setMessage('Exam ended successfully');
      setExams(exams.filter(e => e.examId !== examId));
      setTimeout(() => setMessage(''), 3000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to end exam');
    }
  };

  const handleToggleReview = async (examId) => {
    try {
      await api.put(`/api/teacher/exams/${examId}/toggle-review`);
      setExams(exams.map(e => e.examId === examId ? { ...e, isReview: !e.isReview } : e));
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to toggle review');
    }
  };

  const formatDateTime = (dt) => {
    if (!dt) return '';
    return new Date(dt).toLocaleString('vi-VN');
  };

  if (loading) return <div className="page-container"><p>Loading...</p></div>;

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Ongoing Exams</h2>
        <div className="header-actions">
          <Link to="/teacher/exams/create" className="btn btn-primary">+ Create Exam</Link>
          <Link to="/teacher/exams/completed" className="btn btn-secondary">Completed Exams</Link>
        </div>
      </div>

      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}

      <form className="search-bar" onSubmit={handleSearch}>
        <input
          type="text"
          placeholder="Search by exam name..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <button type="submit">Search</button>
      </form>

      {exams.length === 0 ? (
        <p className="empty-text">No ongoing exams.</p>
      ) : (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Exam Name</th>
                <th>Code</th>
                <th>Quiz</th>
                <th>Duration</th>
                <th>End Time</th>
                <th>Review</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {exams.map((exam, index) => (
                <tr key={exam.examId}>
                  <td>{index + 1}</td>
                  <td>{exam.examName}</td>
                  <td><strong>{exam.examCode}</strong></td>
                  <td>{exam.quizName}</td>
                  <td>{exam.duration} min</td>
                  <td>{formatDateTime(exam.endTime)}</td>
                  <td>
                    <button
                      onClick={() => handleToggleReview(exam.examId)}
                      className={`btn btn-sm ${exam.isReview ? 'btn-primary' : 'btn-secondary'}`}
                    >
                      {exam.isReview ? 'ON' : 'OFF'}
                    </button>
                  </td>
                  <td className="actions">
                    <Link to={`/teacher/exams/ongoing/${exam.examId}/detail`} className="btn btn-sm btn-info">Detail</Link>
                    <button onClick={() => handleEnd(exam.examId, exam.examName)} className="btn btn-sm btn-danger">End</button>
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
