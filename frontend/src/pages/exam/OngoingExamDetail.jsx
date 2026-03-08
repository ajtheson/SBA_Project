import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../../api/axios';

export default function OngoingExamDetail() {
  const { id } = useParams();
  const [submissions, setSubmissions] = useState([]);
  const [examName, setExamName] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const fetchDetail = async () => {
    try {
      setLoading(true);
      const res = await api.get(`/api/teacher/exams/ongoing/${id}/detail`);
      setSubmissions(res.data);
      if (res.data.length > 0 && res.data[0].examName) {
        setExamName(res.data[0].examName);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load detail');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchDetail(); }, [id]);

  const handleForceSubmit = async (submissionId, studentName) => {
    if (!window.confirm(`Force submit for "${studentName}"?`)) return;
    try {
      await api.put(`/api/teacher/exams/${id}/force-submit/${submissionId}`);
      setMessage('Submission force submitted');
      setSubmissions(submissions.map(s => s.submissionId === submissionId ? { ...s, isSubmit: true } : s));
      setTimeout(() => setMessage(''), 3000);
    } catch (err) {
      setError(err.response?.data?.message || 'Force submit failed');
    }
  };

  const formatDateTime = (dt) => {
    if (!dt) return '-';
    return new Date(dt).toLocaleString('vi-VN');
  };

  if (loading) return <div className="page-container"><p>Loading...</p></div>;

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>{examName ? `Exam: ${examName}` : 'Ongoing Exam Detail'}</h2>
        <Link to="/teacher/exams/ongoing" className="btn btn-secondary">Back to Exams</Link>
      </div>

      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}

      {submissions.length === 0 ? (
        <p className="empty-text">No students have started this exam yet.</p>
      ) : (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Student</th>
                <th>Email</th>
                <th>Status</th>
                <th>Score</th>
                <th>Submit Time</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {submissions.map((sub, index) => (
                <tr key={sub.submissionId}>
                  <td>{index + 1}</td>
                  <td>{sub.studentName}</td>
                  <td>{sub.studentEmail}</td>
                  <td>
                    <span className={`status-badge ${sub.isSubmit ? 'status-submitted' : 'status-in-progress'}`}>
                      {sub.isSubmit ? 'Submitted' : 'In Progress'}
                    </span>
                  </td>
                  <td>{sub.isSubmit ? sub.score : '-'}</td>
                  <td>{sub.isSubmit ? formatDateTime(sub.submitTime) : '-'}</td>
                  <td>
                    {!sub.isSubmit && (
                      <button
                        onClick={() => handleForceSubmit(sub.submissionId, sub.studentName)}
                        className="btn btn-sm btn-warning"
                      >
                        Force Submit
                      </button>
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
