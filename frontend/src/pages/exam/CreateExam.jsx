import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api/axios';

export default function CreateExam() {
  const [quizzes, setQuizzes] = useState([]);
  const [form, setForm] = useState({
    quizId: '',
    examName: '',
    duration: '',
    startTime: '',
    endTime: '',
    attempts: '',
    isReview: false,
  });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const loadQuizzes = async () => {
      try {
        const res = await api.get('/api/teacher/quizzes');
        setQuizzes(res.data);
      } catch {
        setError('Failed to load quizzes');
      }
    };
    loadQuizzes();
  }, []);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm(prev => ({ ...prev, [name]: type === 'checkbox' ? checked : value }));
  };

  const validate = () => {
    if (!form.quizId) return 'Please select a quiz';
    if (!form.examName.trim()) return 'Exam name is required';
    if (!form.duration || form.duration <= 0) return 'Duration must be greater than 0';
    if (!form.startTime) return 'Start time is required';
    if (!form.endTime) return 'End time is required';
    if (new Date(form.endTime) <= new Date(form.startTime)) return 'End time must be after start time';
    if (!form.attempts || form.attempts <= 0) return 'Attempts must be greater than 0';
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const err = validate();
    if (err) { setError(err); return; }
    setError('');
    setSubmitting(true);

    try {
      await api.post('/api/teacher/exams', {
        quizId: parseInt(form.quizId),
        examName: form.examName,
        duration: parseInt(form.duration),
        startTime: form.startTime,
        endTime: form.endTime,
        attempts: parseInt(form.attempts),
        isReview: form.isReview,
      });
      navigate('/teacher/exams');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create exam');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Create Exam</h2>
        <Link to="/teacher/exams" className="btn btn-secondary">Back to Exams</Link>
      </div>

      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Select Quiz</label>
          <select name="quizId" value={form.quizId} onChange={handleChange}>
            <option value="">-- Select a quiz --</option>
            {quizzes.map(q => (
              <option key={q.quizId} value={q.quizId}>
                {q.quizName} ({q.quantity} questions)
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>Exam Name</label>
          <input type="text" name="examName" value={form.examName} onChange={handleChange} placeholder="Enter exam name" />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Duration (minutes)</label>
            <input type="number" name="duration" value={form.duration} onChange={handleChange} min="1" />
          </div>

          <div className="form-group">
            <label>Attempts</label>
            <input type="number" name="attempts" value={form.attempts} onChange={handleChange} min="1" />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Start Time</label>
            <input type="datetime-local" name="startTime" value={form.startTime} onChange={handleChange} />
          </div>

          <div className="form-group">
            <label>End Time</label>
            <input type="datetime-local" name="endTime" value={form.endTime} onChange={handleChange} />
          </div>
        </div>

        <div className="form-group checkbox-group">
          <label>
            <input type="checkbox" name="isReview" checked={form.isReview} onChange={handleChange} />
            Allow Review
          </label>
        </div>

        <div className="form-actions">
          <button type="submit" disabled={submitting} className="btn btn-primary">
            {submitting ? 'Creating...' : 'Create Exam'}
          </button>
        </div>
      </form>
    </div>
  );
}
