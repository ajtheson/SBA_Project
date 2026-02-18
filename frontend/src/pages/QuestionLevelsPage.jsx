import SettingManager from '../components/SettingManager';
import { questionLevelService } from '../services/settingService';

const QuestionLevelsPage = () => {
  return <SettingManager title="Question Levels" service={questionLevelService} />;
};

export default QuestionLevelsPage;
