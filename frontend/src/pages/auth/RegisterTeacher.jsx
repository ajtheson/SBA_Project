import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api/axios';

export default function RegisterTeacher() {
  const [form, setForm] = useState({
    email: '', password: '', repassword: '', fullname: '', school: ''
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/api/auth/register/teacher', form);
      navigate('/activate', { state: { email: form.email, mode: 'teacher_register' } });
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    }
  };

  return (
    <div className="auth-container">
      <h2>Teacher Registration</h2>
      {error && <div className="error-message">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Email</label>
          <input type="email" name="email" value={form.email} onChange={handleChange} required />
        </div>
        <div className="form-group">
          <label>Password</label>
          <input type="password" name="password" value={form.password} onChange={handleChange} required />
        </div>
        <div className="form-group">
          <label>Confirm Password</label>
          <input type="password" name="repassword" value={form.repassword} onChange={handleChange} required />
        </div>
        <div className="form-group">
          <label>Full Name</label>
          <input type="text" name="fullname" value={form.fullname} onChange={handleChange} required />
        </div>
        <div className="form-group">
          <label>School</label>
          <input type="text" name="school" value={form.school} onChange={handleChange} required />
        </div>
        <button type="submit">Register</button>
      </form>
      <div className="auth-links">
        <Link to="/login">Already have an account? Login</Link>
      </div>
    </div>
  );
}
