import apiClient from './client';

export const authAPI = {
  register: (data) => apiClient.post('/auth/register', data),
  login: (data) => apiClient.post('/auth/login', data),
};

export const vacancyAPI = {
  getAll: (params) => apiClient.get('/vacancies', { params }),
  getById: (id) => apiClient.get(`/vacancies/${id}`),
  create: (data) => apiClient.post('/vacancies', data),
};

export const applicationAPI = {
  getMyApplications: () => apiClient.get('/applications/my'),
  create: (vacancyId, data) => apiClient.post(`/applications/vacancy/${vacancyId}`, data),
};

export const resumeAPI = {
  upload: (formData) => apiClient.post('/resumes/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
};