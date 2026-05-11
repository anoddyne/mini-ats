import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { companyAPI } from '../../api/endpoints';

export default function CreateCompanyPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    logoUrl: '',
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (errors[e.target.name]) {
      setErrors({ ...errors, [e.target.name]: '' });
    }
  };

  const isValidUrl = (url) => {
    if (!url) return true;
    try {
      new URL(url);
      return true;
    } catch {
      return false;
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.name.trim()) {
      newErrors.name = 'Название компании обязательно';
    } else if (formData.name.length < 2) {
      newErrors.name = 'Название должно быть от 2 символов';
    }
    
    if (formData.description && formData.description.length > 3000) {
      newErrors.description = 'Описание не должно превышать 3000 символов';
    }
    
    if (formData.logoUrl && !isValidUrl(formData.logoUrl)) {
      newErrors.logoUrl = 'Введите корректный URL';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    
    setLoading(true);
    try {
      await companyAPI.create(formData);
      alert('Компания успешно создана!');
      navigate('/companies');
    } catch (error) {
      const message = error.response?.data?.message || 'Ошибка создания компании';
      alert(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto px-4 py-8">
      <div className="bg-white rounded-lg shadow p-6">
        <h1 className="text-2xl font-bold mb-6">Создание компании</h1>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">
              Название компании <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              name="name"
              required
              value={formData.name}
              onChange={handleChange}
              className={`w-full border ${errors.name ? 'border-red-500' : 'border-gray-300'} rounded-md p-2`}
              placeholder="ООО Рога и Копыта"
            />
            {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Описание компании</label>
            <textarea
              name="description"
              rows={5}
              value={formData.description}
              onChange={handleChange}
              className={`w-full border ${errors.description ? 'border-red-500' : 'border-gray-300'} rounded-md p-2`}
              placeholder="Расскажите о компании..."
            />
            <div className="flex justify-between mt-1">
              {errors.description && <p className="text-red-500 text-xs">{errors.description}</p>}
              <p className="text-gray-400 text-xs ml-auto">
                {formData.description.length}/3000
              </p>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Ссылка на логотип</label>
            <input
              type="url"
              name="logoUrl"
              value={formData.logoUrl}
              onChange={handleChange}
              className={`w-full border ${errors.logoUrl ? 'border-red-500' : 'border-gray-300'} rounded-md p-2`}
              placeholder="https://example.com/logo.png"
            />
            {errors.logoUrl && <p className="text-red-500 text-xs mt-1">{errors.logoUrl}</p>}
            {formData.logoUrl && !errors.logoUrl && (
              <div className="mt-2">
                <p className="text-gray-500 text-xs mb-1">Предпросмотр:</p>
                <img
                  src={formData.logoUrl}
                  alt="Предпросмотр логотипа"
                  className="w-16 h-16 object-cover rounded border"
                  onError={(e) => e.target.style.display = 'none'}
                />
              </div>
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