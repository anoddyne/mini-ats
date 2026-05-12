import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { companyAPI } from '../api/endpoints';
import { useAuth } from '../contexts/AuthContext';

export default function CompaniesPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [companies, setCompanies] = useState([]);
  const [myCompaniesIds, setMyCompaniesIds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  // Загружаем данные после того, как user станет известен
  useEffect(() => {
    if (user) {
      loadData();
    } else {
      // Если пользователь не загружен, не показываем лоадер бесконечно, можно установить loading=false
      setLoading(false);
    }
  }, [user]); // <- ключевое изменение: зависимость от user

  const loadData = async () => {
    setLoading(true);
    try {
      // Загружаем все компании
      const response = await companyAPI.getAll();
      const allCompanies = Array.isArray(response.data) ? response.data : response.data.content || [];
      setCompanies(allCompanies);

      // Если пользователь рекрутер, загружаем список его компаний
      if (user?.role === 'RECRUITER') {
        const myCompaniesRes = await companyAPI.getMyCompanies();
        const myCompanies = Array.isArray(myCompaniesRes.data) ? myCompaniesRes.data : myCompaniesRes.data.content || [];
        setMyCompaniesIds(myCompanies.map(c => c.companyId));
      }
    } catch (error) {
      console.error('Ошибка загрузки компаний:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (companyId) => {
    if (!window.confirm('Вы уверены, что хотите удалить компанию? Это действие необратимо.')) return;
    try {
      await companyAPI.delete(companyId);
      await loadData(); // обновим список после удаления
    } catch (error) {
      console.error('Ошибка удаления:', error);
      alert('Не удалось удалить компанию');
    }
  };

  const filteredCompanies = companies.filter(company =>
      company.name?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  if (loading) {
    return <div className="text-center py-12">Загрузка...</div>;
  }

  return (
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Компании</h1>
          {user?.role === 'RECRUITER' && (
              <Link to="/companies/create" className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700">
                + Создать компанию
              </Link>
          )}
        </div>

        <div className="mb-6">
          <input
              type="text"
              placeholder="Поиск компаний..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {filteredCompanies.length === 0 ? (
            <div className="text-center py-12 text-gray-500">Компании не найдены</div>
        ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {filteredCompanies.map((company) => {
                const isOwner = user?.role === 'RECRUITER' && myCompaniesIds.includes(company.companyId);
                return (
                    <div key={company.companyId} className="border rounded-lg p-4 hover:shadow transition bg-white relative">
                      {/* Логотип */}
                      <div className="flex justify-center mb-4">
                        {company.logoUrl ? (
                            <img
                                src={company.logoUrl}
                                alt={company.name}
                                className="h-24 w-24 object-contain rounded-full"
                                onError={(e) => e.target.style.display = 'none'}
                            />
                        ) : (
                            <div className="h-24 w-24 bg-gray-200 rounded-full flex items-center justify-center text-gray-500">
                              📄
                            </div>
                        )}
                      </div>

                      <Link to={`/companies/${company.companyId}`} className="block">
                        <h2 className="text-xl font-semibold text-center mb-2 hover:text-blue-600">
                          {company.name}
                        </h2>
                      </Link>

                      <p className="text-gray-600 text-sm mb-4 line-clamp-3">
                        {company.description || 'Описание отсутствует'}
                      </p>

                      <div className="flex justify-center">
                        <Link
                            to={`/vacancies?companyId=${company.companyId}`}
                            className="border border-blue-600 text-blue-600 px-4 py-1 rounded-md hover:bg-blue-50 transition"
                        >
                          Вакансии компании
                        </Link>
                      </div>

                      {isOwner && (
                          <div className="absolute top-2 right-2 flex space-x-1">
                            <button
                                onClick={() => navigate(`/companies/edit/${company.companyId}`)}
                                className="text-gray-500 hover:text-blue-600"
                                title="Редактировать"
                            >
                              ✏️
                            </button>
                            <button
                                onClick={() => handleDelete(company.companyId)}
                                className="text-gray-500 hover:text-red-600"
                                title="Удалить"
                            >
                              🗑️
                            </button>
                          </div>
                      )}
                    </div>
                );
              })}
            </div>
        )}
      </div>
  );
}