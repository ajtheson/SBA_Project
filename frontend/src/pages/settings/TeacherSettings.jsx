import { useState, useEffect } from 'react';
import api from '../../api/axios';

export default function TeacherSettings() {
  const [profile, setProfile] = useState({ fullname: '', school: '' });
  const [passwords, setPasswords] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
  const [profileMsg, setProfileMsg] = useState('');
  const [profileErr, setProfileErr] = useState('');
  const [passMsg, setPassMsg] = useState('');
  const [passErr, setPassErr] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await api.get('/api/teacher/settings/profile');
        setProfile({ fullname: res.data.fullname || '', school: res.data.school || '' });
      } catch {
        setProfileErr('Failed to load profile');
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, []);

  const handleProfileSubmit = async (e) => {
    e.preventDefault();
    setProfileMsg('');
    setProfileErr('');
    try {
      const res = await api.put('/api/teacher/settings/profile', profile);
      setProfileMsg(res.data.message);
      setTimeout(() => setProfileMsg(''), 3000);
    } catch (err) {
      setProfileErr(err.response?.data?.message || 'Failed to update profile');
    }
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    setPassMsg('');
    setPassErr('');

    if (passwords.newPassword !== passwords.confirmPassword) {
      setPassErr('New passwords do not match.');
      return;
    }

    try {
      const res = await api.put('/api/teacher/settings/password', {
        currentPassword: passwords.currentPassword,
        newPassword: passwords.newPassword,
      });
      setPassMsg(res.data.message);
      setPasswords({ currentPassword: '', newPassword: '', confirmPassword: '' });
      setTimeout(() => setPassMsg(''), 3000);
    } catch (err) {
      setPassErr(err.response?.data?.message || 'Failed to change password');
    }
  };

  if (loading) return <div className="page-container"><p>Loading...</p></div>;

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>Settings</h2>
      </div>

      <div className="settings-section">
        <h3>Profile Information</h3>
        {profileMsg && <div className="success-message">{profileMsg}</div>}
        {profileErr && <div className="error-message">{profileErr}</div>}
        <form onSubmit={handleProfileSubmit} className="settings-form">
          <div className="form-group">
            <label>Full Name</label>
            <input
              type="text"
              value={profile.fullname}
              onChange={(e) => setProfile({ ...profile, fullname: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>School</label>
            <input
              type="text"
              value={profile.school}
              onChange={(e) => setProfile({ ...profile, school: e.target.value })}
            />
          </div>
          <button type="submit" className="btn btn-primary">Update Profile</button>
        </form>
      </div>

      <div className="settings-section">
        <h3>Change Password</h3>
        {passMsg && <div className="success-message">{passMsg}</div>}
        {passErr && <div className="error-message">{passErr}</div>}
        <form onSubmit={handlePasswordSubmit} className="settings-form">
          <div className="form-group">
            <label>Current Password</label>
            <input
              type="password"
              value={passwords.currentPassword}
              onChange={(e) => setPasswords({ ...passwords, currentPassword: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>New Password</label>
            <input
              type="password"
              value={passwords.newPassword}
              onChange={(e) => setPasswords({ ...passwords, newPassword: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>Confirm New Password</label>
            <input
              type="password"
              value={passwords.confirmPassword}
              onChange={(e) => setPasswords({ ...passwords, confirmPassword: e.target.value })}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary">Change Password</button>
        </form>
      </div>
    </div>
  );
}
