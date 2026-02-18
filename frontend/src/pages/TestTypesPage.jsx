import SettingManager from '../components/SettingManager';
import { testTypeService } from '../services/settingService';

const TestTypesPage = () => {
  return <SettingManager title="Test Types" service={testTypeService} />;
};

export default TestTypesPage;
