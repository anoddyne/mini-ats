import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from '../contexts/AuthContext';
import Layout from '../components/Layout';
import HomePage from '../pages/HomePage';
import LoginPage from '../pages/LoginPage';
import RegisterPage from '../pages/RegisterPage';
import CandidateDashboard from '../pages/candidate/CandidateDashboard';
import RecruiterDashboard from '../pages/recruiter/RecruiterDashboard';
import ProfilePage from "../pages/ProfilePage.jsx";
import VacancyDetailPage from "../pages/VacancyDetailPage.jsx";
import CompaniesPage from "../pages/CompaniesPage.jsx";
import EditCompanyPage from '../pages/EditCompanyPage';


// Страницы для рекрутера
import CreateVacancyPage from "../pages/recruiter/CreateVacancyPage.jsx";
import EditVacancyPage from "../pages/recruiter/EditVacancyPage.jsx";
import CreateCompanyPage from "../pages/recruiter/CreateCompanyPage.jsx";

function ProtectedRoute({ children, allowedRoles = [] }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" />;
  if (allowedRoles.length && !allowedRoles.includes(user.role)) {
    return <Navigate to="/" />;
  }
  return children;
}

export default function AppRouter() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Layout>
          <Routes>
            {/* Публичные маршруты */}
            <Route path="/" element={<HomePage />} />
            <Route path="/vacancies/:id" element={<VacancyDetailPage />} />
            <Route path="/companies" element={<CompaniesPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/companies/edit/:id" element={<EditCompanyPage />} />
            {/* Общие для всех авторизованных */}
            <Route path="/profile" element={
              <ProtectedRoute>
                <ProfilePage />
              </ProtectedRoute>
            } />
            
            {/* Маршруты кандидата */}
            <Route path="/candidate/dashboard" element={
              <ProtectedRoute allowedRoles={['CANDIDATE']}>
                <CandidateDashboard />
              </ProtectedRoute>
            } />
            
            {/* Маршруты рекрутера */}
            <Route path="/recruiter/dashboard" element={
              <ProtectedRoute allowedRoles={['RECRUITER', 'ADMIN']}>
                <RecruiterDashboard />
              </ProtectedRoute>
            } />
            <Route path="/recruiter/vacancies/create" element={
              <ProtectedRoute allowedRoles={['RECRUITER', 'ADMIN']}>
                <CreateVacancyPage />
              </ProtectedRoute>
            } />
            <Route path="/recruiter/vacancies/:id/edit" element={
              <ProtectedRoute allowedRoles={['RECRUITER', 'ADMIN']}>
                <EditVacancyPage />
              </ProtectedRoute>
            } />
            
            {/* Маршруты для управления компаниями (только рекрутер/админ) */}
            <Route path="/companies/create" element={
              <ProtectedRoute allowedRoles={['RECRUITER', 'ADMIN']}>
                <CreateCompanyPage />
              </ProtectedRoute>
            } />
            <Route path="/companies/:id/edit" element={
              <ProtectedRoute allowedRoles={['RECRUITER', 'ADMIN']}>
                <EditCompanyPage />
              </ProtectedRoute>
            } />
            <Route path="/recruiter/dashboard" element={
              <ProtectedRoute><RecruiterDashboard /></ProtectedRoute>
            } />
          </Routes>
        </Layout>
      </AuthProvider>
    </BrowserRouter>
  );
}