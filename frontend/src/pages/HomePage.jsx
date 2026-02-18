import { Link } from 'react-router-dom';
import './HomePage.css';

const HomePage = () => {
  const settings = [
    { name: 'Roles', path: '/settings/roles', description: 'Manage user roles' },
    { name: 'Subject Categories', path: '/settings/categories', description: 'Manage subject categories' },
    { name: 'Test Types', path: '/settings/test-types', description: 'Manage test types' },
    { name: 'Question Levels', path: '/settings/question-levels', description: 'Manage question difficulty levels' },
    { name: 'Lesson Types', path: '/settings/lesson-types', description: 'Manage lesson types' },
  ];

  return (
    <div className="home-page">
      <h1>Quiz Practicing System</h1>
      <p className="subtitle">Admin Dashboard - System Settings Management</p>
      
      <div className="settings-grid">
        {settings.map((setting) => (
          <Link to={setting.path} key={setting.path} className="setting-card">
            <h3>{setting.name}</h3>
            <p>{setting.description}</p>
          </Link>
        ))}
      </div>
    </div>
  );
};

export default HomePage;
