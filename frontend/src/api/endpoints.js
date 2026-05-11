import apiClient from './client';

export const authAPI = {
  register: (data) => apiClient.post('/auth/register', data),
  login: (data) => apiClient.post('/auth/login', data),
  updateProfile: (userId, updateData) => apiClient.put(`/users/${userId}`, updateData),
};

export const companyAPI = {
  getAll: (params) => apiClient.get('/companies', { params }),
  getById: (id) => apiClient.get(`/companies/${id}`),
  getMyCompanies: () => apiClient.get('/companies/my'),
  create: (data) => apiClient.post('/companies', data),
  update: (id, data) => apiClient.put(`/companies/${id}`, data),
  delete: (id) => apiClient.delete(`/companies/${id}`),
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

export const applicationAPI = {
  getMyApplications: () => apiClient.get('/applications/my'),
  getByVacancy: (vacancyId) => apiClient.get(`/applications/vacancy/${vacancyId}`),
  create: (vacancyId, data) => apiClient.post(`/applications/vacancy/${vacancyId}`, data),
  updateStatus: (applicationId, status) => apiClient.patch(`/applications/${applicationId}/status`, { status }),
  updateInterviewType: (applicationId, interviewType) => apiClient.patch(`/applications/${applicationId}/interview-type`, { interviewType }),
  cancel: (applicationId) => apiClient.delete(`/applications/${applicationId}`),
};

export const resumeAPI = {
  getMyResume: (userId) => apiClient.get(`/resume/${userId}`),
  upload: (formData) => apiClient.post('/resumes/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
};

export const statsAPI = {
  getRecruiterStats: () => apiClient.get('/stats/recruiter'),
};