import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/axios';

export default function CompletedExams() {
  const [exams, setExams] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchExams = async () => {
    try {
      setLoading(true);
      const res = await api.get('/api/teacher/exams/completed');
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
        const res = await api.get(`/api/teacher/exams/completed/search?name=${encodeURIComponent(search)}`);
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
        <h2>Completed Exams</h2>
        <div className="header-actions">
          <Link to="/teacher/exams/ongoing" className="btn btn-secondary">Ongoing Exams</Link>
        </div>
      </div>

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
        <p className="empty-text">No completed exams.</p>
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
                    <Link to={`/teacher/exams/${exam.examId}/results`} className="btn btn-sm btn-info">Results</Link>
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
