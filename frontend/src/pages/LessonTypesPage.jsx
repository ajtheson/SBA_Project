import SettingManager from '../components/SettingManager';
import { lessonTypeService } from '../services/settingService';

const LessonTypesPage = () => {
  return <SettingManager title="Lesson Types" service={lessonTypeService} />;
};

export default LessonTypesPage;
