import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { vacancyAPI, applicationAPI, statsAPI } from '../../api/endpoints';

export default function RecruiterDashboard() {
  const [myVacancies, setMyVacancies] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedVacancy, setSelectedVacancy] = useState(null);
  const [applications, setApplications] = useState([]);
  const [applicationsLoading, setApplicationsLoading] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [vacanciesRes, statsRes] = await Promise.all([
        vacancyAPI.getAll({ my: true }),
        statsAPI.getRecruiterStats().catch(() => ({ data: null })),
      ]);
      setMyVacancies(vacanciesRes.data.content || []);
      setStats(statsRes.data);
    } catch (error) {
      console.error('Ошибка загрузки:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadApplications = async (vacancyId) => {
    setApplicationsLoading(true);
    try {
      const response = await applicationAPI.getByVacancy(vacancyId);
      setApplications(response.data);
      setSelectedVacancy(vacancyId);
    } catch (error) {
      console.error('Ошибка загрузки откликов:', error);
    } finally {
      setApplicationsLoading(false);
    }
  };

  const updateStatus = async (applicationId, newStatus) => {
    try {
      await applicationAPI.updateStatus(applicationId, newStatus);
      await loadApplications(selectedVacancy);
      await loadData(); // Обновляем статистику
    } catch (error) {
      console.error('Ошибка обновления статуса:', error);
      alert('Ошибка обновления статуса');
    }
  };

  const closeVacancy = async (vacancyId) => {
    if (confirm('Закрыть вакансию? Отклики больше не будут приниматься.')) {
      try {
        await vacancyAPI.close(vacancyId);
        await loadData();
        if (selectedVacancy === vacancyId) {
          setSelectedVacancy(null);
          setApplications([]);
        }
      } catch (error) {
        alert('Ошибка закрытия вакансии');
      }
    }
  };

  const statusColors = {
    NEW: 'bg-yellow-100 text-yellow-800',
    REVIEW: 'bg-blue-100 text-blue-800',
    INTERVIEW: 'bg-purple-100 text-purple-800',
    OFFER: 'bg-green-100 text-green-800',
    REJECT: 'bg-red-100 text-red-800',
  };

  const statusNames = {
    NEW: 'Новый',
    REVIEW: 'На рассмотрении',
    INTERVIEW: 'Собеседование',
    OFFER: 'Оффер',
    REJECT: 'Отказ',
  };

  // Статистические карточки
  const statsCards = stats ? [
    { label: 'Всего откликов', value: stats.total, color: 'bg-blue-500', icon: '📊' },
    { label: 'Новых', value: stats.byStatus?.NEW || 0, color: 'bg-yellow-500', icon: '🆕' },
    { label: 'На рассмотрении', value: stats.byStatus?.REVIEW || 0, color: 'bg-blue-500', icon: '👀' },
    { label: 'Собеседование', value: stats.byStatus?.INTERVIEW || 0, color: 'bg-purple-500', icon: '🎯' },
    { label: 'Оффер', value: stats.byStatus?.OFFER || 0, color: 'bg-green-500', icon: '✅' },
    { label: 'Отказ', value: stats.byStatus?.REJECT || 0, color: 'bg-red-500', icon: '❌' },
  ] : [];

  if (loading) return <div className="text-center py-12">Загрузка...</div>;

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6 flex-wrap gap-3">
        <h1 className="text-3xl font-bold text-gray-900">Панель рекрутера</h1>
        <div className="flex gap-3">
          <Link
            to="/recruiter/vacancies/create"
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
          >
            + Создать вакансию
          </Link>
          <Link to="/profile" className="text-gray-600 hover:text-gray-700 pt-2">
            Профиль →
          </Link>
        </div>
      </div>

      {/* ========== СТАТИСТИКА (НОВЫЙ БЛОК) ========== */}
      {stats && stats.total > 0 && (
        <div className="mb-8">
          <h2 className="text-xl font-semibold mb-4">📈 Статистика откликов</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {statsCards.map((card, index) => (
              <div
                key={index}
                className={`${card.color} rounded-lg shadow p-4 text-white transform hover:scale-105 transition-transform duration-200`}
              >
                <div className="text-3xl mb-2">{card.icon}</div>
                <div className="text-2xl font-bold">{card.value}</div>
                <div className="text-sm opacity-90">{card.label}</div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Если статистика пустая, показываем заглушку */}
      {stats && stats.total === 0 && (
        <div className="bg-gray-50 rounded-lg p-6 text-center mb-8">
          <p className="text-gray-500">📊 Пока нет откликов на ваши вакансии</p>
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Мои вакансии */}
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-semibold mb-4">📌 Мои вакансии</h2>
          {myVacancies.length === 0 ? (
            <div className="text-center py-8 text-gray-500">
              <p>У вас пока нет вакансий</p>
              <Link to="/recruiter/vacancies/create" className="text-blue-600 hover:underline text-sm mt-2 inline-block">
                Создать первую вакансию →
              </Link>
            </div>
          ) : (
            <div className="space-y-3 max-h-96 overflow-y-auto">
              {myVacancies.map((vacancy) => (
                <div
                  key={vacancy.id}
                  className={`p-4 border rounded-lg cursor-pointer transition ${
                    selectedVacancy === vacancy.id ? 'border-blue-600 bg-blue-50' : 'hover:border-blue-300'
                  }`}
                  onClick={() => loadApplications(vacancy.id)}
                >
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <h3 className="font-semibold text-lg">{vacancy.title}</h3>
                      <p className="text-sm text-gray-500">
                        {vacancy.location || 'Локация не указана'} • {vacancy.employmentType === 'FULL_TIME' ? 'Полная занятость' :
                          vacancy.employmentType === 'PART_TIME' ? 'Частичная занятость' :
                          vacancy.employmentType === 'REMOTE' ? 'Удалённо' : 'Стажировка'}
                      </p>
                      <div className="flex gap-2 mt-2">
                        <span className={`text-xs px-2 py-1 rounded ${
                          vacancy.status === 'OPEN' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                        }`}>
                          {vacancy.status === 'OPEN' ? 'Открыта' : 'Закрыта'}
                        </span>
                        <span className="text-xs text-gray-500">
                          📊 {vacancy.applicationsCount || 0} откликов
                        </span>
                      </div>
                    </div>
                    <div className="flex gap-2">
                      <Link
                        to={`/recruiter/vacancies/${vacancy.id}/edit`}
                        onClick={(e) => e.stopPropagation()}
                        className="text-yellow-600 hover:text-yellow-700 text-sm"
                        title="Редактировать"
                      >
                        ✏️
                      </Link>
                      {vacancy.status === 'OPEN' && (
                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            closeVacancy(vacancy.id);
                          }}
                          className="text-red-600 hover:text-red-700 text-sm"
                          title="Закрыть вакансию"
                        >
                          🔒
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Отклики на выбранную вакансию */}
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-semibold mb-4">
            {selectedVacancy ? '👥 Кандидаты' : '👈 Выберите вакансию'}
          </h2>
          {selectedVacancy && (
            <>
              {applicationsLoading ? (
                <div className="text-center py-8">Загрузка...</div>
              ) : applications.length === 0 ? (
                <div className="text-center py-8 text-gray-500">
                  Нет откликов на эту вакансию
                </div>
              ) : (
                <div className="space-y-4 max-h-96 overflow-y-auto">
                  {applications.map((app) => (
                    <div key={app.id} className="border rounded-lg p-4">
                      <div className="flex justify-between items-start flex-wrap gap-2">
                        <div className="flex-1">
                          <p className="font-semibold">{app.candidateName || 'Кандидат'}</p>
                          <p className="text-sm text-gray-500">{app.candidateEmail || 'email не указан'}</p>
                          {app.candidatePhone && (
                            <p className="text-sm text-gray-500">📞 {app.candidatePhone}</p>
                          )}
                          {app.coverLetter && (
                            <p className="text-sm text-gray-600 mt-2 bg-gray-50 p-2 rounded">
                              💬 {app.coverLetter}
                            </p>
                          )}
                          {app.resumeUrl && (
                            <a
                              href={app.resumeUrl}
                              target="_blank"
                              rel="noopener noreferrer"
                              className="text-blue-600 text-sm hover:underline mt-2 inline-block"
                            >
                              📄 Скачать резюме
                            </a>
                          )}
                        </div>
                        <div>
                          <select
                            value={app.status}
                            onChange={(e) => updateStatus(app.id, e.target.value)}
                            className={`text-sm rounded-md border-gray-300 p-1 focus:ring-blue-600 focus:border-blue-600 ${statusColors[app.status] || ''}`}
                          >
                            <option value="NEW">🟡 Новый</option>
                            <option value="REVIEW">🔵 На рассмотрении</option>
                            <option value="INTERVIEW">🟣 Собеседование</option>
                            <option value="OFFER">🟢 Оффер</option>
                            <option value="REJECT">🔴 Отказ</option>
                          </select>
                        </div>
                      </div>
                      {app.feedback && (
                        <div className="mt-2 text-sm text-gray-600 border-t pt-2">
                          📝 Фидбек: {app.feedback}
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}