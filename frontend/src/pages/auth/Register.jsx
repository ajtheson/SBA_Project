import { Link } from 'react-router-dom';

export default function Register() {
  return (
    <div className="auth-container">
      <h2>Register</h2>
      <p>Choose your role:</p>
      <div className="role-selection">
        <Link to="/register/student" className="role-btn">Student</Link>
        <Link to="/register/teacher" className="role-btn">Teacher</Link>
      </div>
      <div className="auth-links">
        <Link to="/login">Already have an account? Login</Link>
      </div>
    </div>
  );
}
