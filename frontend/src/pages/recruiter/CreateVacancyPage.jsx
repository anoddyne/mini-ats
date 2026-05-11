import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { vacancyAPI, companyAPI } from '../../api/endpoints';

export default function CreateVacancyPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [companies, setCompanies] = useState([]);
  const [companiesLoading, setCompaniesLoading] = useState(true);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    salaryFrom: '',
    salaryTo: '',
    location: '',
    companyId: '',      // ← НОВОЕ ПОЛЕ: ID компании
    employmentType: 'OFFICE',
    status: 'DRAFT',
    requiredSkills: '',
    experienceLevel: 'NO_EXPERIENCE',
  });

  // Загружаем список компаний при загрузке страницы
  useEffect(() => {
    loadCompanies();
  }, []);

  const loadCompanies = async () => {
    setCompaniesLoading(true);
    try {
      const response = await companyAPI.getMyCompanies(); // Только мои компании
      setCompanies(response.data || []);
    } catch (error) {
      console.error('Ошибка загрузки компаний:', error);
    } finally {
      setCompaniesLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Проверяем, выбрана ли компания
    if (!formData.companyId) {
      alert('Пожалуйста, выберите компанию');
      return;
    }
    
    setLoading(true);
    try {
      await vacancyAPI.create(formData);
      alert('Вакансия создана!');
      navigate('/recruiter/dashboard');
    } catch (error) {
      alert(error.response?.data?.message || 'Ошибка создания');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <div className="bg-white rounded-lg shadow p-6">
        <h1 className="text-2xl font-bold mb-6">Создание вакансии</h1>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* ВЫБОР КОМПАНИИ */}
          <div>
            <label className="block text-sm font-medium mb-1">
              Компания <span className="text-red-500">*</span>
            </label>
            {companiesLoading ? (
              <div className="text-gray-500">Загрузка компаний...</div>
            ) : companies.length === 0 ? (
              <div className="border border-red-300 bg-red-50 rounded-md p-3">
                <p className="text-red-600 text-sm mb-2">
                  У вас нет компаний. Сначала создайте компанию.
                </p>
                <button
                  type="button"
                  onClick={() => navigate('/companies/create')}
                  className="bg-blue-600 text-white px-4 py-2 rounded-md text-sm hover:bg-blue-700"
                >
                  + Создать компанию
                </button>
              </div>
            ) : (
              <select
                name="companyId"
                required
                value={formData.companyId}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-md p-2 focus:ring-blue-600 focus:border-blue-600"
              >
                <option value="">-- Выберите компанию --</option>
                {companies.map(company => (
                  <option key={company.id} value={company.id}>
                    {company.name}
                  </option>
                ))}
              </select>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Название вакансии *</label>
            <input
              type="text"
              name="title"
              required
              value={formData.title}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-md p-2 focus:ring-blue-600 focus:border-blue-600"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Описание *</label>
            <textarea
              name="description"
              required
              rows={6}
              value={formData.description}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-md p-2 focus:ring-blue-600 focus:border-blue-600"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">Зарплата от (₽)</label>
              <input
                type="number"
                name="salaryFrom"
                value={formData.salaryFrom}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-md p-2"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Зарплата до (₽)</label>
              <input
                type="number"
                name="salaryTo"
                value={formData.salaryTo}
                onChange={handleChange}
                className="w-full border border-gray-300 rounded-md p-2"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Локация</label>
            <input
              type="text"
              name="location"
              value={formData.location}
              onChange={handleChange}
              placeholder="Москва, Санкт-Петербург, Удалённо..."
              className="w-full border border-gray-300 rounded-md p-2"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Тип занятости</label>
            <select
              name="employmentType"
              value={formData.employmentType}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-md p-2"
            >
              <option value="OFFICE">Офис</option>
              <option value="HYBRID">Гибрид</option>
              <option value="REMOTE">Удалённая работа</option>
              <option value="ON_SITE">На объекте</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Статус вакансии</label>
            <select
              name="status"
              value={formData.status}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-md p-2"
            >
              <option value="DRAFT">📝 Черновик</option>
              <option value="OPEN">🟢 Открыта</option>
              <option value="CLOSED">🔴 Закрыта</option>
              <option value="ARCHIVED">📦 В архиве</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Требуемые навыки</label>
            <input
              type="text"
              name="requiredSkills"
              value={formData.requiredSkills}
              onChange={handleChange}
              placeholder="JavaScript, React, Java, Spring (через запятую)"
              className="w-full border border-gray-300 rounded-md p-2"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Опыт работы</label>
            <select
              name="experienceLevel"
              value={formData.experienceLevel}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-md p-2"
            >
              <option value="NO_EXPERIENCE">Нет опыта</option>
              <option value="JUNIOR">Junior (до 1 года)</option>
              <option value="MIDDLE">Middle (1-3 года)</option>
              <option value="SENIOR">Senior (3-6 лет)</option>
              <option value="LEAD">Lead (6+ лет)</option>
            </select>
          </div>

          <div className="flex gap-3 pt-4">
            <button
              type="submit"
              disabled={loading || companies.length === 0}
              className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 disabled:opacity-50"
            >
              {loading ? 'Создание...' : 'Создать вакансию'}
            </button>
            <button
              type="button"
              onClick={() => navigate('/recruiter/dashboard')}
              className="border border-gray-300 px-6 py-2 rounded-md hover:bg-gray-50"
            >
              Отмена
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}