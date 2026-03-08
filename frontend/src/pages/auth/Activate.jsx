import { useState } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import api from '../../api/axios';

export default function Activate() {
  const [otp, setOtp] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const { email, mode } = location.state || {};

  if (!email) {
    return (
      <div className="auth-container">
        <p>Invalid access. Please <Link to="/register">register</Link> first.</p>
      </div>
    );
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setMessage('');
    try {
      const res = await api.post('/api/auth/activate', { email, otp, mode });
      if (mode === 'student_forgot' || mode === 'teacher_forgot') {
        const role = mode === 'student_forgot' ? 'student' : 'teacher';
        navigate('/reset-password', { state: { email, role } });
      } else {
        setMessage(res.data.message);
        setTimeout(() => navigate('/login'), 2000);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Verification failed');
    }
  };

  const handleResend = async () => {
    setError('');
    try {
      const res = await api.post(`/api/auth/resend-otp?email=${encodeURIComponent(email)}`);
      setMessage(res.data.message);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to resend OTP');
    }
  };

  return (
    <div className="auth-container">
      <h2>Verify OTP</h2>
      <p>An OTP code has been sent to <strong>{email}</strong></p>
      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>OTP Code</label>
          <input type="text" value={otp} onChange={(e) => setOtp(e.target.value)} required maxLength={4} />
        </div>
        <button type="submit">Verify</button>
      </form>
      <button onClick={handleResend} className="link-btn">Resend OTP</button>
    </div>
  );
}
