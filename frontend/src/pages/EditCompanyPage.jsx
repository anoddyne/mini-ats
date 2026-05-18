import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { companyAPI } from '../api/endpoints';

export default function EditCompanyPage() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        logo: null,
    });
    const [currentLogoUrl, setCurrentLogoUrl] = useState('');

    useEffect(() => {
        loadCompany();
    }, [id]);

    const loadCompany = async () => {
        setLoading(true);
        try {
            const response = await companyAPI.getById(id);
            const company = response.data;
            setFormData({
                name: company.name,
                description: company.description || '',
                logo: null,
            });
            setCurrentLogoUrl(company.logoUrl);
        } catch (error) {
            console.error('Ошибка загрузки компании:', error);
            alert('Не удалось загрузить данные компании');
            navigate('/companies');
        } finally {
            setLoading(false);
        }
    };

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
        setSubmitting(true);
        try {
            // Создаём DTO объект
            const dto = {
                name: formData.name,
                description: formData.description,
            };
            const data = new FormData();
            // Добавляем DTO как JSON-строку
            data.append('dto', new Blob([JSON.stringify(dto)], { type: 'application/json' }));
            if (formData.logo) {
                data.append('logo', formData.logo);
            }
            await companyAPI.update(id, data);
            alert('Компания обновлена');
            navigate('/companies');
        } catch (error) {
            console.error(error);
            alert('Ошибка обновления');
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <div className="text-center py-12">Загрузка...</div>;

    return (
        <div className="max-w-3xl mx-auto px-4 py-8">
            <div className="bg-white rounded-lg shadow p-6">
                <h1 className="text-2xl font-bold mb-6">Редактирование компании</h1>

                {currentLogoUrl && (
                    <div className="mb-4 text-center">
                        <img src={currentLogoUrl} alt="Текущий логотип" className="h-24 w-24 object-contain mx-auto rounded-full" />
                        <p className="text-sm text-gray-500 mt-1">Текущий логотип</p>
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium mb-1">Название компании *</label>
                        <input
                            type="text"
                            name="name"
                            required
                            value={formData.name}
                            onChange={handleChange}
                            className="w-full border border-gray-300 rounded-md p-2 focus:ring-blue-600"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">Описание</label>
                        <textarea
                            name="description"
                            rows={4}
                            value={formData.description}
                            onChange={handleChange}
                            className="w-full border border-gray-300 rounded-md p-2 focus:ring-blue-600"
                            placeholder="Расскажите о компании..."
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">Новый логотип (необязательно)</label>
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
                            disabled={submitting}
                            className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 disabled:opacity-50"
                        >
                            {submitting ? 'Сохранение...' : 'Сохранить'}
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