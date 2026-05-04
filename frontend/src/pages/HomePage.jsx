import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { vacancyAPI } from '../api/endpoints';

export default function HomePage() {
  const [vacancies, setVacancies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  
  // Параметры пагинации и фильтрации
  const [filters, setFilters] = useState({
    page: 0,
    size: 10,
    sortBy: 'createdAt',
    direction: 'DESC',
  });
  
  // Текстовый поиск
  const [searchQuery, setSearchQuery] = useState('');
  
  // Фильтры
  const [employmentType, setEmploymentType] = useState('');
  const [location, setLocation] = useState('');
  const [experienceLevel, setExperienceLevel] = useState('');
  const [salaryMin, setSalaryMin] = useState('');
  const [salaryMax, setSalaryMax] = useState('');
  
  // Показывать ли панель фильтров (на мобильных)
  const [showFilters, setShowFilters] = useState(false);

  // Загрузка вакансий
  useEffect(() => {
    loadVacancies();
  }, [filters]);

  const loadVacancies = async () => {
    setLoading(true);
    try {
      // Собираем все параметры для запроса
      const params = { ...filters };
      
      if (searchQuery) params.title = searchQuery;
      if (employmentType) params.employmentType = employmentType;
      if (location) params.location = location;
      if (experienceLevel) params.experienceLevel = experienceLevel;
      if (salaryMin) params.salaryFrom = salaryMin;
      if (salaryMax) params.salaryTo = salaryMax;
      
      const response = await vacancyAPI.getAll(params);
      setVacancies(response.data.content || []);
      setTotalPages(response.data.totalPages || 0);
      setTotalElements(response.data.totalElements || 0);
    } catch (error) {
      console.error('Ошибка загрузки вакансий:', error);
      setVacancies([]);
      setTotalPages(0);
      setTotalElements(0);
    } finally {
      setLoading(false);
    }
  };

  // Поиск (сбрасываем на первую страницу)
  const handleSearch = (e) => {
    e.preventDefault();
    setFilters({ ...filters, page: 0 });
    setTimeout(() => loadVacancies(), 0);
  };

  // Сброс всех фильтров
  const resetFilters = () => {
    setSearchQuery('');
    setEmploymentType('');
    setLocation('');
    setExperienceLevel('');
    setSalaryMin('');
    setSalaryMax('');
    setFilters({ page: 0, size: 10, sortBy: 'createdAt', direction: 'DESC' });
    setTimeout(() => loadVacancies(), 0);
  };

  // Применить фильтры
  const applyFilters = () => {
    setFilters({ ...filters, page: 0 });
    setTimeout(() => loadVacancies(), 0);
  };

  // Смена страницы
  const goToPage = (page) => {
    setFilters({ ...filters, page });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  // Смена сортировки
  const handleSort = (sortBy) => {
    const newDirection = filters.sortBy === sortBy && filters.direction === 'DESC' ? 'ASC' : 'DESC';
    setFilters({ ...filters, sortBy, direction: newDirection, page: 0 });
  };

  // Типы занятости для фильтра
  const employmentTypes = [
    { value: 'FULL_TIME', label: 'Полная занятость' },
    { value: 'PART_TIME', label: 'Частичная занятость' },
    { value: 'REMOTE', label: 'Удалённая работа' },
    { value: 'INTERNSHIP', label: 'Стажировка' },
  ];

  // Уровни опыта для фильтра
  const experienceLevels = [
    { value: 'NO_EXPERIENCE', label: 'Нет опыта' },
    { value: 'JUNIOR', label: 'Junior (до 1 года)' },
    { value: 'MIDDLE', label: 'Middle (1-3 года)' },
    { value: 'SENIOR', label: 'Senior (3-6 лет)' },
    { value: 'LEAD', label: 'Lead (6+ лет)' },
  ];

  // Локации для фильтра
  const locations = [
    { value: 'Москва', label: 'Москва' },
    { value: 'Санкт-Петербург', label: 'Санкт-Петербург' },
    { value: 'Новосибирск', label: 'Новосибирск' },
    { value: 'Екатеринбург', label: 'Екатеринбург' },
    { value: 'Казань', label: 'Казань' },
    { value: 'Нижний Новгород', label: 'Нижний Новгород' },
    { value: 'Удалённо', label: 'Удалённо' },
  ];

  const getSortIcon = (field) => {
    if (filters.sortBy !== field) return '↕️';
    return filters.direction === 'DESC' ? '↓' : '↑';
  };

  const formatSalary = (from, to) => {
    if (from && to) return `${from.toLocaleString()} - ${to.toLocaleString()} ₽`;
    if (from) return `от ${from.toLocaleString()} ₽`;
    if (to) return `до ${to.toLocaleString()} ₽`;
    return 'з/п не указана';
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU');
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {/* Заголовок */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">
          Все вакансии
          {totalElements > 0 && (
            <span className="text-lg font-normal text-gray-500 ml-2">
              ({totalElements})
            </span>
          )}
        </h1>
      </div>

      {/* Поисковая строка */}
      <form onSubmit={handleSearch} className="mb-4">
        <div className="flex gap-2">
          <div className="flex-1">
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Поиск по названию вакансии..."
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <button
            type="submit"
            className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
          >
            🔍 Найти
          </button>
          <button
            type="button"
            onClick={() => setShowFilters(!showFilters)}
            className="px-4 py-3 border border-gray-300 rounded-lg hover:bg-gray-50 transition md:hidden"
          >
            {showFilters ? '📄 Скрыть' : '🔧 Фильтры'}
          </button>
        </div>
      </form>

      {/* Панель фильтров */}
      <div className={`${showFilters ? 'block' : 'hidden'} md:block mb-6`}>
        <div className="bg-gray-50 rounded-lg p-4 border border-gray-200">
          <div className="flex flex-wrap justify-between items-center mb-3">
            <h3 className="font-semibold text-gray-700">🔧 Фильтры</h3>
            <button
              onClick={resetFilters}
              className="text-sm text-blue-600 hover:text-blue-700"
            >
              Сбросить всё
            </button>
          </div>
          
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-3">
            {/* Тип занятости */}
            <select
              value={employmentType}
              onChange={(e) => setEmploymentType(e.target.value)}
              className="px-3 py-2 border border-gray-300 rounded-md text-sm"
            >
              <option value="">Все типы</option>
              {employmentTypes.map(type => (
                <option key={type.value} value={type.value}>{type.label}</option>
              ))}
            </select>

            {/* Локация */}
            <select
              value={location}
              onChange={(e) => setLocation(e.target.value)}
              className="px-3 py-2 border border-gray-300 rounded-md text-sm"
            >
              <option value="">Все города</option>
              {locations.map(loc => (
                <option key={loc.value} value={loc.value}>{loc.label}</option>
              ))}
            </select>

            {/* Опыт */}
            <select
              value={experienceLevel}
              onChange={(e) => setExperienceLevel(e.target.value)}
              className="px-3 py-2 border border-gray-300 rounded-md text-sm"
            >
              <option value="">Любой опыт</option>
              {experienceLevels.map(exp => (
                <option key={exp.value} value={exp.value}>{exp.label}</option>
              ))}
            </select>

            {/* Зарплата от */}
            <input
              type="number"
              placeholder="Зарплата от (₽)"
              value={salaryMin}
              onChange={(e) => setSalaryMin(e.target.value)}
              className="px-3 py-2 border border-gray-300 rounded-md text-sm"
            />

            {/* Зарплата до */}
            <input
              type="number"
              placeholder="Зарплата до (₽)"
              value={salaryMax}
              onChange={(e) => setSalaryMax(e.target.value)}
              className="px-3 py-2 border border-gray-300 rounded-md text-sm"
            />
          </div>
          
          <div className="flex justify-end mt-3">
            <button
              onClick={applyFilters}
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 text-sm"
            >
              Применить фильтры
            </button>
          </div>
        </div>
      </div>

      {/* Сортировка */}
      <div className="flex justify-between items-center mb-4 text-sm">
        <div className="text-gray-500">
          {loading ? 'Загрузка...' : `Найдено: ${totalElements}`}
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => handleSort('createdAt')}
            className="px-3 py-1 border border-gray-300 rounded-md hover:bg-gray-50"
          >
            📅 По дате {getSortIcon('createdAt')}
          </button>
          <button
            onClick={() => handleSort('salaryFrom')}
            className="px-3 py-1 border border-gray-300 rounded-md hover:bg-gray-50"
          >
            💰 По зарплате {getSortIcon('salaryFrom')}
          </button>
        </div>
      </div>

      {/* Список вакансий */}
      {loading ? (
        <div className="space-y-4">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="bg-white p-6 rounded-lg shadow animate-pulse">
              <div className="h-6 bg-gray-200 rounded w-1/3 mb-3"></div>
              <div className="h-4 bg-gray-200 rounded w-1/4 mb-2"></div>
              <div className="h-4 bg-gray-200 rounded w-1/2"></div>
            </div>
          ))}
        </div>
      ) : vacancies.length === 0 ? (
        <div className="bg-white rounded-lg shadow p-12 text-center">
          <p className="text-gray-500 text-lg">😕 Вакансии не найдены</p>
          <p className="text-gray-400 text-sm mt-2">Попробуйте изменить параметры поиска</p>
          <button
            onClick={resetFilters}
            className="mt-4 text-blue-600 hover:text-blue-700"
          >
            Сбросить фильтры
          </button>
        </div>
      ) : (
        <div className="space-y-4">
          {vacancies.map((vacancy) => (
            <Link
              key={vacancy.id}
              to={`/vacancies/${vacancy.id}`}
              className="block bg-white p-6 rounded-lg shadow hover:shadow-lg transition-shadow border border-gray-100"
            >
              <div className="flex justify-between items-start flex-wrap gap-2">
                <div className="flex-1">
                  <h2 className="text-xl font-semibold text-gray-900 hover:text-blue-600">
                    {vacancy.title}
                  </h2>
                  <div className="flex flex-wrap gap-3 mt-2 text-sm text-gray-500">
                    <span>📍 {vacancy.location || 'Не указана'}</span>
                    <span>💼 {vacancy.employmentType === 'FULL_TIME' ? 'Полная занятость' :
                          vacancy.employmentType === 'PART_TIME' ? 'Частичная занятость' :
                          vacancy.employmentType === 'REMOTE' ? 'Удалённо' : 'Стажировка'}</span>
                    <span>⭐ {vacancy.experienceLevel === 'NO_EXPERIENCE' ? 'Нет опыта' :
                          vacancy.experienceLevel === 'JUNIOR' ? 'Junior' :
                          vacancy.experienceLevel === 'MIDDLE' ? 'Middle' :
                          vacancy.experienceLevel === 'SENIOR' ? 'Senior' : 'Lead'}</span>
                    <span>📅 {formatDate(vacancy.createdAt)}</span>
                  </div>
                  <p className="text-gray-600 mt-2 line-clamp-2">
                    {vacancy.description}
                  </p>
                  {vacancy.requiredSkills && (
                    <div className="flex flex-wrap gap-1 mt-2">
                      {vacancy.requiredSkills.split(',').slice(0, 3).map((skill, idx) => (
                        <span key={idx} className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded">
                          {skill.trim()}
                        </span>
                      ))}
                      {vacancy.requiredSkills.split(',').length > 3 && (
                        <span className="text-xs text-gray-400">+ ещё</span>
                      )}
                    </div>
                  )}
                </div>
                <div className="text-right">
                  <p className="text-lg font-bold text-green-600">
                    {formatSalary(vacancy.salaryFrom, vacancy.salaryTo)}
                  </p>
                  {vacancy.status === 'OPEN' ? (
                    <span className="inline-block mt-1 text-xs bg-green-100 text-green-800 px-2 py-1 rounded">
                      Открыта
                    </span>
                  ) : (
                    <span className="inline-block mt-1 text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded">
                      Закрыта
                    </span>
                  )}
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}

      {/* Пагинация */}
      {totalPages > 1 && (
        <div className="flex justify-center items-center gap-2 mt-8 flex-wrap">
          <button
            onClick={() => goToPage(filters.page - 1)}
            disabled={filters.page === 0}
            className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            ← Назад
          </button>
          
          <div className="flex gap-1 flex-wrap">
            {[...Array(Math.min(5, totalPages))].map((_, i) => {
              let pageNum;
              if (totalPages <= 5) {
                pageNum = i;
              } else if (filters.page <= 2) {
                pageNum = i;
              } else if (filters.page >= totalPages - 3) {
                pageNum = totalPages - 5 + i;
              } else {
                pageNum = filters.page - 2 + i;
              }
              
              return (
                <button
                  key={pageNum}
                  onClick={() => goToPage(pageNum)}
                  className={`w-10 h-10 rounded-md ${
                    filters.page === pageNum
                      ? 'bg-blue-600 text-white'
                      : 'border border-gray-300 hover:bg-gray-50'
                  }`}
                >
                  {pageNum + 1}
                </button>
              );
            })}
          </div>
          
          <button
            onClick={() => goToPage(filters.page + 1)}
            disabled={filters.page >= totalPages - 1}
            className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Вперёд →
          </button>
        </div>
      )}
    </div>
  );
}