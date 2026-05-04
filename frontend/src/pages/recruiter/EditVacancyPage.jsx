import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { vacancyAPI } from '../../api/endpoints';

export default function EditVacancyPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    salaryFrom: '',
    salaryTo: '',
    location: '',
    employmentType: 'FULL_TIME',
    requiredSkills: '',
    experienceLevel: 'NO_EXPERIENCE',
  });

  useEffect(() => {
    loadVacancy();
  }, [id]);

  const loadVacancy = async () => {
    try {
      const response = await vacancyAPI.getById(id);
      const v = response.data;
      setFormData({
        title: v.title || '',
        description: v.description || '',
        salaryFrom: v.salaryFrom || '',
        salaryTo: v.salaryTo || '',
        location: v.location || '',
        employmentType: v.employmentType || 'FULL_TIME',
        requiredSkills: v.requiredSkills || '',
        experienceLevel: v.experienceLevel || 'NO_EXPERIENCE',
      });
    } catch (error) {
      console.error('Ошибка загрузки:', error);
      navigate('/recruiter/dashboard');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await vacancyAPI.update(id, formData);
      alert('Вакансия обновлена!');
      navigate('/recruiter/dashboard');
    } catch (error) {
      alert(error.response?.data?.message || 'Ошибка обновления');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="text-center py-12">Загрузка...</div>;

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <div className="bg-white rounded-lg shadow p-6">
        <h1 className="text-2xl font-bold mb-6">Редактирование вакансии</h1>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Название вакансии *</label>
            <input
              type="text"
              name="title"
              required
              value={formData.title}
              onChange={handleChange}
              className="w-full border border-gray-300 rounded-md p-2"
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
              className="w-full border border-gray-300 rounded-md p-2"
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
              <option value="FULL_TIME">Полная занятость</option>
              <option value="PART_TIME">Частичная занятость</option>
              <option value="REMOTE">Удалённая работа</option>
              <option value="INTERNSHIP">Стажировка</option>
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
              disabled={saving}
              className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 disabled:opacity-50"
            >
              {saving ? 'Сохранение...' : 'Сохранить'}
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