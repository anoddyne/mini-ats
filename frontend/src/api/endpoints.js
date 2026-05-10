import apiClient from './client';

export const authAPI = {
  register: (data) => apiClient.post('/users/register', data),
  login: (data) => apiClient.post('auth/login', data),
  updateProfile:(userId,updateData) => apiClient.put(`/users/${userId}`,updateData),
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
  getMyApplications: () => apiClient.get('reactions/'), // TODO
  create: (vacancyId, data) => apiClient.post(`/applications/vacancies/${vacancyId}`, data),
};

export const resumeAPI = {
  upload: (formData) => apiClient.post('/resume', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
  getMyResume: (userId) => apiClient.get(`/resume/${userId}`),
};

export class statsAPI {
}