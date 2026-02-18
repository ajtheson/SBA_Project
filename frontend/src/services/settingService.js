import api from './api';

// Generic setting service
const getSettings = (endpoint, activeOnly = false) => {
  const params = activeOnly ? { activeOnly: true } : {};
  return api.get(endpoint, { params });
};

const getSettingById = (endpoint, id) => {
  return api.get(`${endpoint}/${id}`);
};

const createSetting = (endpoint, data) => {
  return api.post(endpoint, data);
};

const updateSetting = (endpoint, id, data) => {
  return api.put(`${endpoint}/${id}`, data);
};

const deleteSetting = (endpoint, id) => {
  return api.delete(`${endpoint}/${id}`);
};

const toggleSettingStatus = (endpoint, id) => {
  return api.patch(`${endpoint}/${id}/toggle-status`);
};

// Role Service
export const roleService = {
  getAll: (activeOnly) => getSettings('/settings/roles', activeOnly),
  getById: (id) => getSettingById('/settings/roles', id),
  create: (data) => createSetting('/settings/roles', data),
  update: (id, data) => updateSetting('/settings/roles', id, data),
  delete: (id) => deleteSetting('/settings/roles', id),
  toggleStatus: (id) => toggleSettingStatus('/settings/roles', id),
};

// Subject Category Service
export const categoryService = {
  getAll: (activeOnly) => getSettings('/settings/categories', activeOnly),
  getById: (id) => getSettingById('/settings/categories', id),
  create: (data) => createSetting('/settings/categories', data),
  update: (id, data) => updateSetting('/settings/categories', id, data),
  delete: (id) => deleteSetting('/settings/categories', id),
  toggleStatus: (id) => toggleSettingStatus('/settings/categories', id),
};

// Test Type Service
export const testTypeService = {
  getAll: (activeOnly) => getSettings('/settings/test-types', activeOnly),
  getById: (id) => getSettingById('/settings/test-types', id),
  create: (data) => createSetting('/settings/test-types', data),
  update: (id, data) => updateSetting('/settings/test-types', id, data),
  delete: (id) => deleteSetting('/settings/test-types', id),
  toggleStatus: (id) => toggleSettingStatus('/settings/test-types', id),
};

// Question Level Service
export const questionLevelService = {
  getAll: (activeOnly) => getSettings('/settings/question-levels', activeOnly),
  getById: (id) => getSettingById('/settings/question-levels', id),
  create: (data) => createSetting('/settings/question-levels', data),
  update: (id, data) => updateSetting('/settings/question-levels', id, data),
  delete: (id) => deleteSetting('/settings/question-levels', id),
  toggleStatus: (id) => toggleSettingStatus('/settings/question-levels', id),
};

// Lesson Type Service
export const lessonTypeService = {
  getAll: (activeOnly) => getSettings('/settings/lesson-types', activeOnly),
  getById: (id) => getSettingById('/settings/lesson-types', id),
  create: (data) => createSetting('/settings/lesson-types', data),
  update: (id, data) => updateSetting('/settings/lesson-types', id, data),
  delete: (id) => deleteSetting('/settings/lesson-types', id),
  toggleStatus: (id) => toggleSettingStatus('/settings/lesson-types', id),
};
