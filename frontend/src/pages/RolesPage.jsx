import SettingManager from '../components/SettingManager';
import { roleService } from '../services/settingService';

const RolesPage = () => {
  return <SettingManager title="Roles" service={roleService} />;
};

export default RolesPage;
