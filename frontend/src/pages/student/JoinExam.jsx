import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../api/axios';

export default function JoinExam() {
  const [code, setCode] = useState('');
  const [exam, setExam] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!code.trim()) { setError('Please enter exam code'); return; }
    setError('');
    setLoading(true);
    try {
      const res = await api.get(`/api/student/exams/join?code=${encodeURIComponent(code.trim())}`);
      setExam(res.data);
    } catch (err) {
      setExam(null);
      setError(err.response?.data?.message || 'Exam not found or not active');
    } finally {
      setLoading(false);
    }
  };

  const handleStartExam = () => {
    navigate(`/student/exams/${exam.examId}/take`);
  };

  const formatDateTime = (dt) => {
    if (!dt) return '';
    return new Date(dt).toLocaleString('vi-VN');
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Join Exam</h2>
      </div>

      {error && <div className="error-message">{error}</div>}

      <form className="join-exam-form" onSubmit={handleSearch}>
        <div className="form-group">
          <label>Enter Exam Code</label>
          <input
            type="text"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            placeholder="Enter 5-digit exam code"
            maxLength={5}
          />
        </div>
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Searching...' : 'Find Exam'}
        </button>
      </form>

      {exam && (
        <div className="exam-info-card">
          <h3>{exam.examName}</h3>
          <div className="exam-info-details">
            <p><strong>Quiz:</strong> {exam.quizName}</p>
            <p><strong>Duration:</strong> {exam.duration} minutes</p>
            <p><strong>Attempts:</strong> {exam.attempts}</p>
            <p><strong>Start:</strong> {formatDateTime(exam.startTime)}</p>
            <p><strong>End:</strong> {formatDateTime(exam.endTime)}</p>
          </div>
          <div className="exam-warning">
            <p>⚠️ Once you start, the timer will begin counting down.</p>
            <p>⚠️ Exiting fullscreen or switching tabs will auto-submit your exam.</p>
          </div>
          <button onClick={handleStartExam} className="btn btn-primary btn-lg">
            Start Exam
          </button>
        </div>
      )}
    </div>
  );
}
