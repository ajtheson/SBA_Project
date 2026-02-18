import SettingManager from '../components/SettingManager';
import { categoryService } from '../services/settingService';

const CategoriesPage = () => {
  return <SettingManager title="Subject Categories" service={categoryService} />;
};

export default CategoriesPage;
