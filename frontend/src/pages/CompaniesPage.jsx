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
      setCompanies(response.data.content || []);
    } catch (error) {
      console.error('Ошибка загрузки компаний:', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredCompanies = companies.filter(company =>
    company.name?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">🏢 Компании</h1>
        {user?.role === 'RECRUITER' && (
          <Link
            to="/companies/create"
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
          >
            + Создать компанию
          </Link>
        )}
      </div>

      <div className="mb-6">
        <input
          type="text"
          placeholder="Поиск компаний по названию..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
        />
      </div>

      {loading ? (
        <div className="text-center py-12">Загрузка...</div>
      ) : filteredCompanies.length === 0 ? (
        <div className="bg-white rounded-lg shadow p-12 text-center">
          <p className="text-gray-500 text-lg">😕 Компании не найдены</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredCompanies.map((company) => (
            <div key={company.id} className="bg-white rounded-lg shadow hover:shadow-lg transition p-6">
              <div className="flex items-start gap-3">
                {company.logoUrl && (
                  <img
                    src={company.logoUrl}
                    alt={company.name}
                    className="w-12 h-12 rounded-lg object-cover flex-shrink-0"
                    onError={(e) => e.target.style.display = 'none'}
                  />
                )}
                <div className="flex-1">
                  <div className="flex justify-between items-start">
                    <h2 className="text-xl font-semibold text-gray-900">{company.name}</h2>
                    {user?.role === 'RECRUITER' && user.id === company.ownerId && (
                      <Link
                        to={`/companies/${company.id}/edit`}
                        className="text-yellow-600 hover:text-yellow-700"
                      >
                        ✏️
                      </Link>
                    )}
                  </div>
                  {company.description && (
                    <p className="text-gray-600 text-sm mt-3 line-clamp-2">
                      {company.description}
                    </p>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}