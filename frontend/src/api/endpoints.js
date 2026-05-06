import apiClient from './client';

export const authAPI = {
  register: (data) => apiClient.post('/users/register', data),
  login: (data) => apiClient.post('/auth/login', data),
  updateProfile:(userId,updateData) => apiClient.put(`/users/${userId}`,updateData),
};

export const vacancyAPI = {
  getAll: (params) => apiClient.get('/vacancies', { params }),
  getById: (id) => apiClient.get(`/vacancies/${id}`),
  create: (data) => apiClient.post('/vacancies', data),
};

export const applicationAPI = {
  getMyApplications: () => apiClient.get('reactions/'), // TODO
  create: (vacancyId, data) => apiClient.post(`/applications/vacancies/${vacancyId}`, data),
};

export const resumeAPI = {
  upload: (formData) => apiClient.post('/resume', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
  getMyResume: (userId) => apiClient.get(`/resume/${userId}`),
};