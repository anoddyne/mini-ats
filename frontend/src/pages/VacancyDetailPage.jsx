import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { vacancyAPI, applicationAPI } from '../api/endpoints';

export default function VacancyDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [vacancy, setVacancy] = useState(null);
  const [loading, setLoading] = useState(true);
  const [applying, setApplying] = useState(false);
  const [coverLetter, setCoverLetter] = useState('');
  const [showApplyForm, setShowApplyForm] = useState(false);
  const [alreadyApplied, setAlreadyApplied] = useState(false);

  const getEmploymentTypeLabel = (type) => {
    switch (type) {
      case 'OFFICE': return 'Офис';
      case 'HYBRID': return 'Гибрид';
      case 'REMOTE': return 'Удалённая работа';
      case 'ON_SITE': return 'На объекте';
      default: return type || 'Не указан';
    }
  };

  const getVacancyStatusLabel = (status) => {
    switch (status) {
      case 'OPEN': return { text: 'Открыта', color: 'text-green-600' };
      case 'CLOSED': return { text: 'Закрыта', color: 'text-red-600' };
      case 'DRAFT': return { text: 'Черновик', color: 'text-gray-500' };
      case 'ARCHIVED': return { text: 'В архиве', color: 'text-purple-600' };
      default: return null;
    }
  };

  useEffect(() => {
    loadVacancy();
  }, [id]);

  const loadVacancy = async () => {
    setLoading(true);
    try {
      const response = await vacancyAPI.getById(id);
      setVacancy(response.data);
    } catch (error) {
      console.error('Ошибка загрузки:', error);
      navigate('/');
    } finally {
      setLoading(false);
    }
  };

  const handleApply = async () => {
    if (!coverLetter.trim()) {
      alert('Напишите сопроводительное письмо');
      return;
    }
    setApplying(true);
    try {
      await applicationAPI.create({
        coverLetter: coverLetter,
        vacancyId: id
      });
      alert('Отклик успешно отправлен!');
      setShowApplyForm(false);
      setCoverLetter('');
      setAlreadyApplied(true);
    } catch (error) {
      alert(error.response?.data?.message || 'Ошибка при отправке отклика');
    } finally {
      setApplying(false);
    }
  };

  if (loading) return <div className="flex justify-center py-12">Загрузка...</div>;
  if (!vacancy) return <div className="text-center py-12">Вакансия не найдена</div>;

  const statusInfo = getVacancyStatusLabel(vacancy.status);

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="p-6">
          <div className="flex justify-between items-start">
            <h1 className="text-2xl font-bold text-gray-900">{vacancy.title}</h1>
            {statusInfo && (
              <span className={`text-sm font-medium ${statusInfo.color}`}>
                {statusInfo.text}
              </span>
            )}
          </div>
          
          <div className="flex flex-wrap gap-2 mt-2 text-sm text-gray-500">
            <span>{vacancy.location || 'Локация не указана'}</span>
            <span>•</span>
            <span>{getEmploymentTypeLabel(vacancy.employmentType)}</span>
            {vacancy.salaryFrom && (
              <>
                <span>•</span>
                <span>{vacancy.salaryFrom.toLocaleString()} - {vacancy.salaryTo?.toLocaleString()} ₽</span>
              </>
            )}
          </div>

          <div className="mt-4">
            <h3 className="font-semibold text-lg">Описание</h3>
            <p className="text-gray-700 whitespace-pre-wrap mt-1">{vacancy.description}</p>
          </div>

          <div className="mt-4">
            <h3 className="font-semibold text-lg">Требуемые навыки</h3>
            <div className="flex flex-wrap gap-2 mt-1">
              {vacancy.requiredSkills?.split(',').map((skill, idx) => (
                <span key={idx} className="bg-gray-100 text-gray-700 px-2 py-1 text-sm rounded">
                  {skill.trim()}
                </span>
              ))}
            </div>
          </div>

          <div className="mt-4">
            <h3 className="font-semibold text-lg">Опыт работы</h3>
            <p className="text-gray-700">
              {vacancy.experienceLevel === 'NO_EXPERIENCE' ? 'Нет опыта' :
               vacancy.experienceLevel === 'JUNIOR' ? 'Junior (до 1 года)' :
               vacancy.experienceLevel === 'MIDDLE' ? 'Middle (1-3 года)' :
               vacancy.experienceLevel === 'SENIOR' ? 'Senior (3-6 лет)' :
               vacancy.experienceLevel === 'LEAD' ? 'Lead (6+ лет)' : 'Не указан'}
            </p>
          </div>

          {/* Кнопка отклика - только для OPEN */}
          {user?.role === 'CANDIDATE' && vacancy.status === 'OPEN' && !alreadyApplied && (
            <div className="mt-6">
              {!showApplyForm ? (
                <button
                  onClick={() => setShowApplyForm(true)}
                  className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700"
                >
                  Откликнуться
                </button>
              ) : (
                <div className="border rounded-lg p-4 mt-2">
                  <label className="block text-sm font-medium mb-1">Сопроводительное письмо</label>
                  <textarea
                    value={coverLetter}
                    onChange={(e) => setCoverLetter(e.target.value)}
                    placeholder="Расскажите, почему вы подходите на эту позицию..."
                    rows={4}
                    className="w-full border-gray-300 rounded-md focus:ring-blue-600 focus:border-blue-600"
                  />
                  <div className="flex gap-2 mt-3">
                    <button
                      onClick={handleApply}
                      disabled={applying}
                      className="bg-blue-600 text-white px-4 py-1 rounded hover:bg-blue-700 disabled:opacity-50"
                    >
                      {applying ? 'Отправка...' : 'Отправить'}
                    </button>
                    <button
                      onClick={() => setShowApplyForm(false)}
                      className="border border-gray-300 px-4 py-1 rounded hover:bg-gray-50"
                    >
                      Отмена
                    </button>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* Кнопки рекрутера - для DRAFT и OPEN */}
          {user?.role === 'RECRUITER' && (vacancy.status === 'OPEN' || vacancy.status === 'DRAFT') && (
            <div className="mt-6 flex gap-3">
              <button
                onClick={() => navigate(`/recruiter/vacancies/${vacancy.id}/edit`)}
                className="bg-yellow-500 text-white px-4 py-2 rounded-md hover:bg-yellow-600"
              >
                Редактировать
              </button>
              {vacancy.status === 'OPEN' && (
                <button
                  onClick={async () => {
                    if (confirm('Закрыть вакансию?')) {
                      await vacancyAPI.close(vacancy.id);
                      loadVacancy();
                    }
                  }}
                  className="bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600"
                >
                  Закрыть вакансию
                </button>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}