import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { companyAPI } from '../api/endpoints';
import { useAuth } from '../contexts/AuthContext';

export default function CompaniesPage() {
  const { user } = useAuth();
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    loadCompanies();
  }, []);

  const loadCompanies = async () => {
    setLoading(true);
    try {
      const response = await companyAPI.getAll();
      setCompanies(Array.isArray(response.data) ? response.data : response.data.content || []);
    } catch (error) {
      console.error('Ошибка загрузки компаний:', error);
    } finally {
      setLoading(false);
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

        {/* Поиск */}
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
              {filteredCompanies.map((company) => (
                  <div key={company.companyId} className="border rounded-lg p-4 hover:shadow transition bg-white">
                    {/* Логотип */}
                    <div className="flex justify-center mb-4">
                      {company.logoUrl ? (
                          <img
                              src={company.logoUrl}
                              alt={company.name}
                              className="h-24 w-24 object-contain rounded-full"
                              onError={(e) => e.target.style.display = 'none'} // Скрыть, если картинка не загрузилась
                          />
                      ) : (
                          <div className="h-24 w-24 bg-gray-200 rounded-full flex items-center justify-center text-gray-500">
                            📄
                          </div>
                      )}
                    </div>

                    {/* Информация */}
                    <Link to={`/companies/${company.companyId}`} className="block">
                      <h2 className="text-xl font-semibold text-center mb-2 hover:text-blue-600">
                        {company.name}
                      </h2>
                    </Link>

                    <p className="text-gray-600 text-sm mb-4 line-clamp-3">
                      {company.description || 'Описание отсутствует'}
                    </p>

                    {/* Кнопка для перехода к вакансиям */}
                    <div className="flex justify-center">
                      <Link
                          to={`/vacancies?companyId=${company.companyId}`}
                          className="border border-blue-600 text-blue-600 px-4 py-1 rounded-md hover:bg-blue-50 transition"
                      >
                        Вакансии компании
                      </Link>
                    </div>
                  </div>
              ))}
            </div>
        )}
      </div>
  );
}