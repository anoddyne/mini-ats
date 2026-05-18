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

  // Функция для преобразования типа занятости
  const getEmploymentTypeLabel = (type) => {
    switch (type) {
      case 'OFFICE': return 'Офис';
      case 'HYBRID': return 'Гибрид';
      case 'REMOTE': return 'Удалённо';
      case 'ON_SITE': return 'На объекте';
      default: return type || 'Не указан';
    }
  };

  // Функция для статуса вакансии
  const getVacancyStatusLabel = (status) => {
    switch (status) {
      case 'DRAFT': return { text: 'Черновик', color: 'bg-gray-100 text-gray-800' };
      case 'OPEN': return { text: 'Открыта', color: 'bg-green-100 text-green-800' };
      case 'CLOSED': return { text: 'Закрыта', color: 'bg-red-100 text-red-800' };
      case 'ARCHIVED': return { text: 'В архиве', color: 'bg-purple-100 text-purple-800' };
      default: return { text: status || 'Неизвестно', color: 'bg-gray-100 text-gray-800' };
    }
  };

  // Цвета и названия для статусов откликов
  const statusColors = {
    NEW: 'bg-yellow-100 text-yellow-800',
    REVIEW: 'bg-blue-100 text-blue-800',
    INTERVIEW: 'bg-purple-100 text-purple-800',
    SCHEDULED: 'bg-indigo-100 text-indigo-800',
    COMPLETED: 'bg-green-100 text-green-800',
    CANCELLED: 'bg-red-100 text-red-800',
    POSTPONED: 'bg-orange-100 text-orange-800',
    OFFER: 'bg-emerald-100 text-emerald-800',
    REJECT: 'bg-gray-100 text-gray-800',
  };

  const statusNames = {
    NEW: 'Новый',
    REVIEW: 'На рассмотрении',
    INTERVIEW: 'Собеседование',
    SCHEDULED: 'Запланировано',
    COMPLETED: 'Завершено',
    CANCELLED: 'Отменено',
    POSTPONED: 'Отложено',
    OFFER: 'Оффер',
    REJECT: 'Отказ',
  };

  const interviewTypes = [
    { value: 'TECHNICAL', label: '🛠 Техническое' },
    { value: 'HR', label: '💬 HR собеседование' },
    { value: 'FINAL', label: '🎯 Финальное' },
    { value: 'TASK_REVIEW', label: '📋 Проверка задания' },
  ];

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    console.log("--- DASHBOARD START LOADING ---");
    try {
      const vacanciesRes = await vacancyAPI.getAll({ my: true });

      console.log("1. Что пришло с сервера (весь response):", vacanciesRes);
      console.log("2. Данные (data):", vacanciesRes.data);

      // Проверяем структуру (Spring Data Page или обычный List)
      const data = vacanciesRes.data.content || vacanciesRes.data;
      console.log("3. Итоговый массив для стейта:", data);

      if (Array.isArray(data)) {
        setMyVacancies(data);
      } else {
        console.error("ОШИБКА: Сервер вернул не массив! Проверь структуру ответа.");
      }

    } catch (error) {
      console.error('КРИТИЧЕСКАЯ ОШИБКА ЗАГРУЗКИ:', error);
    } finally {
      setLoading(false);
      console.log("--- DASHBOARD END LOADING ---");
    }
  };

  // const loadData = async () => {
  //   setLoading(true);
  //   try {
  //     const [vacanciesRes, statsRes] = await Promise.all([
  //       vacancyAPI.getAll(),
  //       statsAPI.getRecruiterStats().catch(() => ({ data: null })),
  //     ]);
  //     console.log("Полный ответ от API:", vacanciesRes);
  //     setMyVacancies(vacanciesRes.data.content || []);
  //     setStats(statsRes.data);
  //   } catch (error) {
  //     console.error('Ошибка загрузки:', error);
  //   } finally {
  //     setLoading(false);
  //   }
  // };

  const loadApplications = async (vacancyId) => {
    setApplicationsLoading(true);
    try {
      const response = await applicationAPI.getByVacancy(vacancyId);
      let apps = response.data;
      // Если ответ — объект страницы (с content), берем content
      if (apps && !Array.isArray(apps) && apps.content) {
        apps = apps.content;
      }
      if (Array.isArray(apps)) {
        setApplications(apps);
      } else {
        console.error("Отклики не массив:", apps);
        setApplications([]);
      }
      setSelectedVacancy(vacancyId);
    } catch (error) {
      console.error('Ошибка загрузки откликов:', error);
      setApplications([]);
    } finally {
      setApplicationsLoading(false);
    }
  };

  const updateStatus = async (applicationId, newStatus) => {
    try {
      await applicationAPI.updateStatus(applicationId, newStatus);
      await loadApplications(selectedVacancy);
      await loadData();
    } catch (error) {
      console.error('Ошибка обновления статуса:', error);
      alert('Ошибка обновления статуса');
    }
  };

  const updateInterviewType = async (applicationId, interviewType) => {
    try {
      await applicationAPI.updateInterviewType(applicationId, interviewType);
      await loadApplications(selectedVacancy);
    } catch (error) {
      console.error('Ошибка обновления типа собеседования:', error);
      alert('Ошибка обновления типа собеседования');
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

  const statsCards = stats ? [
    { label: 'Всего откликов', value: stats.total, color: 'bg-blue-500', icon: '📊' },
    { label: 'Новых', value: stats.byStatus?.NEW || 0, color: 'bg-yellow-500', icon: '🆕' },
    { label: 'На рассмотрении', value: stats.byStatus?.REVIEW || 0, color: 'bg-blue-500', icon: '👀' },
    { label: 'Собеседование', value: stats.byStatus?.INTERVIEW || 0, color: 'bg-purple-500', icon: '🎯' },
    { label: 'Запланировано', value: stats.byStatus?.SCHEDULED || 0, color: 'bg-indigo-500', icon: '📅' },
    { label: 'Завершено', value: stats.byStatus?.COMPLETED || 0, color: 'bg-green-500', icon: '✅' },
    { label: 'Оффер', value: stats.byStatus?.OFFER || 0, color: 'bg-emerald-500', icon: '🎉' },
    { label: 'Отказ', value: stats.byStatus?.REJECT || 0, color: 'bg-gray-500', icon: '❌' },
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

      {stats && stats.total > 0 && (
        <div className="mb-8">
          <h2 className="text-xl font-semibold mb-4">📈 Статистика откликов</h2>
          <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-8 gap-3">
            {statsCards.map((card, index) => (
              <div
                key={index}
                className={`${card.color} rounded-lg shadow p-3 text-white transform hover:scale-105 transition-transform duration-200`}
              >
                <div className="text-2xl mb-1">{card.icon}</div>
                <div className="text-xl font-bold">{card.value}</div>
                <div className="text-xs opacity-90">{card.label}</div>
              </div>
            ))}
          </div>
        </div>
      )}

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
              {myVacancies.map((vacancy) => {
                const statusInfo = getVacancyStatusLabel(vacancy.status);
                console.log("Вакансия из списка:", vacancy);
                return (
                  <div
                    key={vacancy.vacancyId}
                    className={`p-4 border rounded-lg cursor-pointer transition ${
                      selectedVacancy === vacancy.vacancyId ? 'border-blue-600 bg-blue-50' : 'hover:border-blue-300'
                    }`}
                    onClick={() => loadApplications(vacancy.vacancyId)}
                  >
                    <div className="flex justify-between items-start">
                      <div className="flex-1">
                        <h3 className="font-semibold text-lg">{vacancy.title}</h3>
                        <div className="flex gap-2 mt-2">
                          <span className={`text-xs px-2 py-1 rounded ${statusInfo.color}`}>
                            {statusInfo.text}
                          </span>
                          <span className="text-xs text-gray-500">
                            📊 {vacancy.applicationsCount || 0} откликов
                          </span>
                        </div>
                      </div>
                      <div className="flex gap-2">
                        <Link
                          to={`/recruiter/vacancies/${vacancy.vacancyId}/edit`}
                          onClick={(e) => e.stopPropagation()}
                          className="text-yellow-600 hover:text-yellow-700 text-sm"
                          title="Редактировать"
                        >
                          ✏️
                        </Link>
                        {(vacancy.status === 'OPEN' || vacancy.status === 'DRAFT') && (
                          <button
                            onClick={(e) => {
                              e.stopPropagation();
                              closeVacancy(vacancy.vacancyId);
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
                );
              })}
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

                          {app.status === 'SCHEDULED' && (
                            <div className="mt-3">
                              <label className="text-xs text-gray-500 mr-2">Тип собеседования:</label>
                              <select
                                value={app.interviewType || 'TECHNICAL'}
                                onChange={(e) => updateInterviewType(app.id, e.target.value)}
                                className="text-sm border border-gray-300 rounded-md p-1"
                              >
                                {interviewTypes.map(type => (
                                  <option key={type.value} value={type.value}>{type.label}</option>
                                ))}
                              </select>
                            </div>
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
                            <option value="SCHEDULED">📅 Запланировано</option>
                            <option value="COMPLETED">✅ Завершено</option>
                            <option value="CANCELLED">❌ Отменено</option>
                            <option value="POSTPONED">⏰ Отложено</option>
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