import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from '../contexts/AuthContext';
import Layout from '../components/Layout';
import HomePage from '../pages/HomePage';
import LoginPage from '../pages/LoginPage';
import RegisterPage from '../pages/RegisterPage';
import CandidateDashboard from '../pages/candidate/CandidateDashboard';
import RecruiterDashboard from '../pages/recruiter/RecruiterDashboard';
import ProfilePage from "../pages/ProfilePage.jsx";
import CreateVacancyPage from "../pages/recruiter/CreateVacancyPage.jsx";

function ProtectedRoute({ children }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" />;
  return children;
}

export default function AppRouter() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Layout>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path ="/profile" element={< ProfilePage />}/>
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/recruiter/vacancies/create" element={<CreateVacancyPage />} />
            <Route path="/candidate/dashboard" element={
              <ProtectedRoute><CandidateDashboard /></ProtectedRoute>
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