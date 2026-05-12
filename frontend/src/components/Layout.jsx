import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function Layout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getDashboardLink = () => {
    if (!user) return '/';
    return user.role === 'CANDIDATE' ? '/candidate/dashboard' : '/recruiter/dashboard';
  };

  return (
      <div className="min-h-screen bg-gray-50">
        <nav className="bg-white shadow-sm border-b sticky top-0 z-10">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between h-16">
              <div className="flex items-center">
                <Link to="/" className="text-xl font-bold text-blue-600 hover:text-blue-700">
                  JobBoard
                </Link>
                <div className="hidden sm:ml-6 sm:flex sm:space-x-4">
                  <Link
                      to="/companies"
                      className="px-3 py-2 text-gray-700 hover:text-blue-600"
                  >
                    Компании
                  </Link>
                  <Link
                      to="/"
                      className="px-3 py-2 text-gray-700 hover:text-blue-600"
                  >
                    Вакансии
                  </Link>
                  {user && (
                      <Link
                          to={getDashboardLink()}
                          className="px-3 py-2 text-gray-700 hover:text-blue-600"
                      >
                        Личный кабинет
                      </Link>
                  )}
                </div>
              </div>
              <div className="flex items-center space-x-4">
                {user ? (
                    <>
                      <Link to="/profile" className="text-gray-700 hover:text-blue-600">
                        👤 {user.name || user.login}
                      </Link>
                      <span className="text-xs bg-gray-100 px-2 py-1 rounded">
                    {user.role === 'CANDIDATE' ? 'Кандидат' : 'Рекрутер'}
                  </span>
                      <button
                          onClick={handleLogout}
                          className="text-gray-700 hover:text-red-600"
                      >
                        Выйти
                      </button>
                    </>
                ) : (
                    <div className="flex items-center space-x-3">
                      <Link to="/login" className="text-gray-700 hover:text-blue-600">
                        Вход
                      </Link>
                      <Link
                          to="/register"
                          className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
                      >
                        Регистрация
                      </Link>
                    </div>
                )}
              </div>
            </div>
          </div>
        </nav>
        <main>{children}</main>
      </div>
  );
}