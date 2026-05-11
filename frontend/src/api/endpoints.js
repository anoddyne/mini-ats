import apiClient from './client';

export const authAPI = {
  register: (data) => apiClient.post('/users/register', data),
  login: (data) => apiClient.post('auth/login', data),
  updateProfile:(userId,updateData) => apiClient.put(`/users/${userId}`,updateData),
};

export const vacancyAPI = {
  getAll: (params) => apiClient.get('/vacancies', { params }),
  getById: (id) => apiClient.get(`/vacancies/${id}`),
  create: (data) => apiClient.post('/vacancies', data),
  update: (id, data) => apiClient.put(`/vacancies/${id}`, data),
  delete: (id) => apiClient.delete(`/vacancies/${id}`),
  close: (id) => apiClient.patch(`/vacancies/${id}/close`),
  publish: (id) => apiClient.patch(`/vacancies/${id}/publish`),
};

// НОВЫЙ API ДЛЯ КОМПАНИЙ
export const companyAPI = {
  getAll: (params) => apiClient.get('/companies', { params }),
  getById: (id) => apiClient.get(`/companies/${id}`),
  getMyCompanies: () => apiClient.get('/companies/my'), // Мои компании (для рекрутера)
  create: (data) => apiClient.post('/companies', data),
  update: (id, data) => apiClient.put(`/companies/${id}`, data),
  delete: (id) => apiClient.delete(`/companies/${id}`),
};

export const applicationAPI = {
  getMyApplications: () => apiClient.get('/applications/my'),
  getByVacancy: (vacancyId) => apiClient.get(`/applications/vacancy/${vacancyId}`),
  create: (vacancyId, data) => apiClient.post(`/applications/vacancy/${vacancyId}`, data),
  updateStatus: (applicationId, status) => apiClient.patch(`/applications/${applicationId}/status`, { status }),
  updateInterviewType: (applicationId, interviewType) => apiClient.patch(`/applications/${applicationId}/interview-type`, { interviewType }),
  cancel: (applicationId) => apiClient.delete(`/applications/${applicationId}`),
};

export const resumeAPI = {
  getMyResume: () => apiClient.get('/resume'),
  upload: (formData) => apiClient.post('/resume', formData),
  download: () => apiClient.get('/resume/download', { responseType: 'blob' }),
  delete: () => apiClient.delete('/resume'),
};

export class statsAPI {
}
