import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../api/axios';

export default function QuizList() {
  const [quizzes, setQuizzes] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const fetchQuizzes = async () => {
    try {
      setLoading(true);
      const res = await api.get('/api/teacher/quizzes');
      setQuizzes(res.data);
    } catch (err) {
      setError('Failed to load quizzes');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchQuizzes(); }, []);

  const handleSearch = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      if (search.trim()) {
        const res = await api.get(`/api/teacher/quizzes/search?name=${encodeURIComponent(search)}`);
        setQuizzes(res.data);
      } else {
        await fetchQuizzes();
      }
    } catch (err) {
      setError('Search failed');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (quizId, quizName) => {
    if (!window.confirm(`Are you sure you want to delete "${quizName}"?`)) return;
    try {
      await api.delete(`/api/teacher/quizzes/${quizId}`);
      setMessage('Quiz deleted successfully');
      setQuizzes(quizzes.filter(q => q.quizId !== quizId));
      setTimeout(() => setMessage(''), 3000);
    } catch (err) {
      setError(err.response?.data?.message || 'Delete failed');
    }
  };

  if (loading) return <div className="page-container"><p>Loading...</p></div>;

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>My Quizzes</h2>
        <Link to="/teacher/quizzes/create" className="btn btn-primary">+ Create Quiz</Link>
      </div>

      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}

      <form className="search-bar" onSubmit={handleSearch}>
        <input
          type="text"
          placeholder="Search by quiz name..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <button type="submit">Search</button>
      </form>

      {quizzes.length === 0 ? (
        <p className="empty-text">No quizzes found. Create your first quiz!</p>
      ) : (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Quiz Name</th>
                <th>Questions</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {quizzes.map((quiz, index) => (
                <tr key={quiz.quizId}>
                  <td>{index + 1}</td>
                  <td>{quiz.quizName}</td>
                  <td>{quiz.quantity}</td>
                  <td className="actions">
                    <Link to={`/teacher/quizzes/${quiz.quizId}`} className="btn btn-sm btn-info">View</Link>
                    <Link to={`/teacher/quizzes/${quiz.quizId}/edit`} className="btn btn-sm btn-warning">Edit</Link>
                    <button onClick={() => handleDelete(quiz.quizId, quiz.quizName)} className="btn btn-sm btn-danger">Delete</button>
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
