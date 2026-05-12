import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { companyAPI } from '../../api/endpoints';

export default function CreateCompanyPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    logo: null,
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleFileChange = (e) => {
    setFormData({ ...formData, logo: e.target.files[0] });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.name.trim()) {
      alert('Название компании обязательно');
      return;
    }
    setLoading(true);
    try {
      const dto = {
        name: formData.name,
        description: formData.description,
      };
      const data = new FormData();
      data.append('dto', new Blob([JSON.stringify(dto)], { type: 'application/json' }));
      if (formData.logo) {
        data.append('logo', formData.logo);
      }
      await companyAPI.create(data);
      alert('Компания создана!');
      navigate('/companies');
    } catch (error) {
      alert(error.response?.data?.message || 'Ошибка создания');
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="max-w-3xl mx-auto px-4 py-8">
        <div className="bg-white rounded-lg shadow p-6">
          <h1 className="text-2xl font-bold mb-6">Создание компании</h1>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium mb-1">Название компании *</label>
              <input
                  type="text"
                  name="name"
                  required
                  value={formData.name}
                  onChange={handleChange}
                  className="w-full border border-gray-300 rounded-md p-2 focus:ring-blue-600 focus:border-blue-600"
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Описание</label>
              <textarea
                  name="description"
                  rows={4}
                  value={formData.description}
                  onChange={handleChange}
                  className="w-full border border-gray-300 rounded-md p-2 focus:ring-blue-600 focus:border-blue-600"
                  placeholder="Расскажите о компании..."
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Логотип компании</label>
              <input
                  type="file"
                  accept="image/*"
                  onChange={handleFileChange}
                  className="w-full border border-gray-300 rounded-md p-2"
              />
              {formData.logo && (
                  <p className="text-sm text-green-600 mt-1">Выбран файл: {formData.logo.name}</p>
              )}
            </div>

            <div className="flex gap-3 pt-4">
              <button
                  type="submit"
                  disabled={loading}
                  className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 disabled:opacity-50"
              >
                {loading ? 'Создание...' : 'Создать компанию'}
              </button>
              <button
                  type="button"
                  onClick={() => navigate('/companies')}
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