import apiClient from './client';

export const authAPI = {
  register: (data) => apiClient.post('/auth/register', data),
  login: (data) => apiClient.post('/auth/login', data),
  getCurrentUser: () => apiClient.get('/auth/me'),
  updateProfile: (data) => apiClient.put('/auth/profile', data),
};

export const vacancyAPI = {
  getAll: (params) => apiClient.get('/vacancies', { params }),
  getById: (id) => apiClient.get('/vacancies/${id}'),
  create: (data) => apiClient.post('/vacancies', data),
  update: (id, data) => apiClient.put('/vacancies/${id}', data),
  delete: (id) => apiClient.delete('/vacancies/${id}'),
  close: (id) => apiClient.patch('/vacancies/${id}/close'),
  publish: (id) => apiClient.patch('/vacancies/${id}/publish'), // Опубликовать из черновика
};

export const applicationAPI = {
  getMyApplications: () => apiClient.get('/applications/my'),
  getByVacancy: (vacancyId) => apiClient.get('/applications/vacancy/${vacancyId}'),
  create: (vacancyId, data) => apiClient.post('/applications/vacancy/${vacancyId}', data),
  updateStatus: (applicationId, status) => apiClient.patch('/applications/${applicationId}/status', { status }),
  updateInterviewType: (applicationId, interviewType) => apiClient.patch('/applications/${applicationId}/interview-type', { interviewType }),
  cancel: (applicationId) => apiClient.delete('/applications/${applicationId}'),
};

export const resumeAPI = {
  getMyResume: () => apiClient.get('/resumes/my'),
  upload: (formData) => apiClient.post('/resumes/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
};

export const statsAPI = {
  getRecruiterStats: () => apiClient.get('/stats/recruiter'),
};