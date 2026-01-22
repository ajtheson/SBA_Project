// API Base URL
export const API_BASE_URL =
  import.meta.env.VITE_API_URL || "http://localhost:8080/api";

// API Endpoints
export const API_ENDPOINTS = {
  // Auth
  AUTH: {
    LOGIN: "/auth/login",
    REGISTER: "/auth/register",
    LOGOUT: "/auth/logout",
    REFRESH_TOKEN: "/auth/refresh-token",
    FORGOT_PASSWORD: "/auth/forgot-password",
    RESET_PASSWORD: "/auth/reset-password",
  },

  // Users
  USERS: {
    BASE: "/users",
    PROFILE: "/users/profile",
    UPDATE_PROFILE: "/users/profile",
    CHANGE_PASSWORD: "/users/change-password",
  },

  // Subjects
  SUBJECTS: {
    BASE: "/subjects",
    FEATURED: "/subjects/featured",
    BY_CATEGORY: "/subjects/category",
  },

  // Questions
  QUESTIONS: {
    BASE: "/questions",
    BY_LESSON: "/questions/lesson",
  },

  // Quizzes
  QUIZZES: {
    BASE: "/quizzes",
    SUBMIT: "/quizzes/submit",
    RESULTS: "/quizzes/results",
  },

  // Registrations
  REGISTRATIONS: {
    BASE: "/registrations",
    MY_COURSES: "/registrations/my-courses",
  },
};

// Application Constants
export const ROLES = {
  ADMIN: "Admin",
  EXPERT: "Expert",
  CUSTOMER: "Customer",
  SALE: "Sale",
};

// Pagination
export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZE_OPTIONS: [5, 10, 20, 50],
};
