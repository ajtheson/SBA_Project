import { useState } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import api from '../../api/axios';

export default function ResetPassword() {
  const [password, setPassword] = useState('');
  const [repassword, setRepassword] = useState('');
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const { email, role } = location.state || {};

  if (!email) {
    return (
      <div className="auth-container">
        <p>Invalid access. Please <Link to="/forgot-password">request password reset</Link> first.</p>
      </div>
    );
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await api.post('/api/auth/reset-password', { email, password, repassword, role });
      setMessage(res.data.message);
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Password reset failed');
    }
  };

  return (
    <div className="auth-container">
      <h2>Reset Password</h2>
      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>New Password</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </div>
        <div className="form-group">
          <label>Confirm Password</label>
          <input type="password" value={repassword} onChange={(e) => setRepassword(e.target.value)} required />
        </div>
        <button type="submit">Change Password</button>
      </form>
    </div>
  );
}
