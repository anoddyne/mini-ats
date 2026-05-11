import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { applicationAPI, resumeAPI } from '../../api/endpoints';

export default function CandidateDashboard() {
  const [applications, setApplications] = useState([]);
  const [resume, setResume] = useState(null);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [stats, setStats] = useState({ total: 0, byStatus: {} });

  // Цвета и названия для статусов (включая статусы собеседований)
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

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const user = JSON.parse(localStorage.getItem('user'));
      const userId = user?.userId;
      const [applicationsRes, resumeRes] = await Promise.all([
        applicationAPI.getMyApplications(userId).catch(() => ({ data: [] })),
        resumeAPI.getMyResume().catch(() => ({ data: null })), // если 204, то data = null
      ]);
      setApplications(applicationsRes.data);
      setResume(resumeRes.data); // может быть null
      // ... статистика
    } catch (error) {
      console.error('Ошибка загрузки:', error);
    } finally {
      setLoading(false);
    }
  };

// Загрузка файла
  const handleResumeUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    if (file.type !== 'application/pdf') {
      alert('Только PDF');
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      alert('Максимум 5 МБ');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    setUploading(true);
    try {
      const response = await resumeAPI.upload(formData);
      setResume(response.data); // обновляем state
      alert('Резюме загружено');
    } catch (err) {
      console.error(err);
      alert('Ошибка загрузки');
    } finally {
      setUploading(false);
    }
  };

// Скачивание
  const handleDownloadResume = async () => {
    if (!resume) {
      alert('Резюме не найдено');
      return;
    }
    try {
      const response = await resumeAPI.download();
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', resume.fileName || 'resume.pdf');
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Ошибка скачивания:', error);
      alert('Не удалось скачать резюме');
    }
  };

// Удаление
  const handleDeleteResume = async () => {
    if (!confirm('Удалить резюме?')) return;
    setDeleting(true);
    try {
      await resumeAPI.delete();
      setResume(null);
      alert('Резюме удалено');
    } catch (error) {
      console.error(error);
      alert('Ошибка удаления');
    } finally {
      setDeleting(false);
    }
  };

  const cancelApplication = async (applicationId) => {
    if (confirm('Вы уверены, что хотите отменить отклик?')) {
      try {
        await applicationAPI.cancel(applicationId);
        await loadData();
        alert('Отклик отменён');
      } catch (error) {
        alert('Ошибка отмены');
      }
    }
  };

  if (loading) return <div className="text-center py-12">Загрузка...</div>;

  return (
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Личный кабинет кандидата</h1>
          <Link to="/profile" className="text-blue-600 hover:text-blue-700">
            Редактировать профиль →
          </Link>
        </div>

        {/* Статистика */}
        {stats.total > 0 && (
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-4 mb-8">
              <div className="bg-white p-4 rounded-lg shadow text-center">
                <div className="text-2xl font-bold text-blue-600">{stats.total}</div>
                <div className="text-sm text-gray-500">Всего откликов</div>
              </div>
              {Object.entries(stats.byStatus).map(([status, count]) => (
                  <div key={status} className="bg-white p-4 rounded-lg shadow text-center">
                    <div className="text-2xl font-bold text-blue-600">{count}</div>
                    <div className="text-sm text-gray-500">{statusNames[status] || status}</div>
                  </div>
              ))}
            </div>
        )}

        {/* Резюме */}
        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <h2 className="text-xl font-semibold mb-4">📄 Моё резюме</h2>
          {resume ? (
              <div className="flex justify-between items-center flex-wrap gap-3">
                <div className="flex gap-2">
                  <button
                      onClick={handleDownloadResume}
                      className="text-blue-600 hover:underline text-sm flex items-center"
                  >
                    📥 Скачать
                  </button>
                  <button
                      onClick={handleDeleteResume}
                      disabled={deleting}
                      className="text-red-600 hover:underline text-sm flex items-center"
                  >
                    {deleting ? 'Удаление...' : '🗑️ Удалить'}
                  </button>
                </div>
                <label className="cursor-pointer bg-gray-100 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-200">
                  Заменить
                  <input
                      type="file"
                      accept=".pdf"
                      onChange={handleResumeUpload}
                      disabled={uploading}
                      className="hidden"
                  />
                </label>
              </div>
          ) : (
              <div className="text-center py-4">
                <p className="text-gray-500 mb-3">Резюме не загружено</p>
                <label className="cursor-pointer bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700">
                  {uploading ? 'Загрузка...' : 'Загрузить резюме (PDF)'}
                  <input
                      type="file"
                      accept=".pdf"
                      onChange={handleResumeUpload}
                      disabled={uploading}
                      className="hidden"
                  />
                </label>
              </div>
          )}
        </div>

        {/* Мои отклики */}
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold">📋 Мои отклики</h2>
            <Link to="/" className="text-blue-600 hover:text-blue-700 text-sm">
              + Найти вакансии
            </Link>
          </div>

          {applications.length === 0 ? (
              <div className="text-center py-8 text-gray-500">
                <p>У вас пока нет откликов</p>
                <Link to="/" className="text-blue-600 hover:underline mt-2 inline-block">
                  Перейти к вакансиям
                </Link>
              </div>
          ) : (
              <div className="space-y-4">
                {applications.map((app) => (
                    <div key={app.id} className="border rounded-lg p-4 hover:shadow transition">
                      <div className="flex justify-between items-start flex-wrap gap-2">
                        <div className="flex-1">
                          <Link
                              to={`/vacancies/${app.vacancyId}`}
                              className="font-semibold text-lg text-blue-600 hover:underline"
                          >
                            {app.vacancyTitle}
                          </Link>
                          <p className="text-sm text-gray-500 mt-1">
                            {app.companyName} • {new Date(app.appliedAt).toLocaleDateString('ru-RU')}
                          </p>
                          {app.coverLetter && (
                              <p className="text-sm text-gray-600 mt-2 italic">
                                "{app.coverLetter}"
                              </p>
                          )}
                          {app.status === 'SCHEDULED' && app.interviewType && (
                              <p className="text-sm text-indigo-600 mt-2">
                                📅 Тип собеседования: {
                                app.interviewType === 'TECHNICAL' ? 'Техническое' :
                                    app.interviewType === 'HR' ? 'HR' :
                                        app.interviewType === 'FINAL' ? 'Финальное' : 'Проверка задания'
                              }
                              </p>
                          )}
                        </div>
                        <div className="text-right">
                    <span className={`inline-block px-3 py-1 text-xs rounded-full ${statusColors[app.status] || 'bg-gray-100'}`}>
                      {statusNames[app.status] || app.status}
                    </span>
                          {app.status === 'NEW' && (
                              <button
                                  onClick={() => cancelApplication(app.id)}
                                  className="block mt-2 text-red-600 text-sm hover:underline"
                              >
                                Отменить
                              </button>
                          )}
                        </div>
                      </div>
                      {app.feedback && (
                          <div className="mt-3 text-sm bg-gray-50 p-2 rounded">
                            <span className="font-medium">Фидбек от рекрутера:</span> {app.feedback}
                          </div>
                      )}
                    </div>
                ))}
              </div>
          )}
        </div>
      </div>
  );
}