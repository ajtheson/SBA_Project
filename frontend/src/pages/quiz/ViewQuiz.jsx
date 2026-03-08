import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../../api/axios';

export default function ViewQuiz() {
  const { id } = useParams();
  const [quiz, setQuiz] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        const res = await api.get(`/api/teacher/quizzes/${id}`);
        setQuiz(res.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to load quiz');
      } finally {
        setLoading(false);
      }
    };
    fetchQuiz();
  }, [id]);

  if (loading) return <div className="page-container"><p>Loading...</p></div>;
  if (error) return <div className="page-container"><div className="error-message">{error}</div></div>;
  if (!quiz) return null;

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>{quiz.quizName}</h2>
        <div className="header-actions">
          <Link to={`/teacher/quizzes/${id}/edit`} className="btn btn-warning">Edit</Link>
          <Link to="/teacher/quizzes" className="btn btn-secondary">Back</Link>
        </div>
      </div>

      <p className="quiz-info">Total questions: {quiz.quantity}</p>

      <div className="questions-container">
        {quiz.questions && quiz.questions.map((q, qIdx) => (
          <div key={q.questionId} className="question-card view-mode">
            <div className="question-header">
              <span className="question-number">Question {qIdx + 1}</span>
              {q.isMultipleChoice && <span className="badge">Multiple Choice</span>}
            </div>
            <p className="question-content">{q.content}</p>
            <ul className="choices-list">
              {q.choices && q.choices.map((c) => (
                <li key={c.choiceId} className={c.isCorrectChoice ? 'correct' : ''}>
                  {c.isCorrectChoice && <span className="correct-mark">✓</span>}
                  {c.choiceContent}
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>
    </div>
  );
}
