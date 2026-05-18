import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { authAPI } from '../api/endpoints';

export default function ProfilePage() {
  const { user, updateUser } = useAuth();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    surname: '',
    patronymic: '',
    age: '',
    phoneNumber: '',
    email: '',
    login: '',
  });
  const [passwordData, setPasswordData] = useState({
    newPassword: '',
    confirmPassword: '',
  });

  useEffect(() => {
    if (user) {
      setFormData({
        name: user.name || '',
        surname: user.surname || '',
        patronymic: user.patronymic || '',
        age: user.age || '',
        phoneNumber: user.phoneNumber || '',
        email: user.email || '',
        login: user.login || '',
      });
    }
  }, [user]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handlePasswordChange = (e) => {
    setPasswordData({ ...passwordData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setLoading(true);
    try {
      const updateData = {
        name: formData.name,
        surname: formData.surname,
        patronymic: formData.patronymic,
        age: parseInt(formData.age),
        phoneNumber: formData.phoneNumber,
        email: formData.email,
        login: formData.login,
        password: passwordData.newPassword || null
      };

      const response = await authAPI.updateProfile(updateData);
      updateUser(response.data);
      alert('Профиль обновлён!');
      // Очищаем поля пароля
      setPasswordData({ newPassword: '', confirmPassword: '' });
    } catch (error) {
      alert(error.response?.data?.message || 'Ошибка обновления');
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="max-w-2xl mx-auto px-4 py-8">
        <div className="bg-white rounded-lg shadow p-6">
          <h1 className="text-2xl font-bold mb-6">Мой профиль</h1>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium mb-1">Имя</label>
                <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-md p-2"
                />
              </div>
              <div>
                <label className="block text-sm font-medium mb-1">Фамилия</label>
                <input
                    type="text"
                    name="surname"
                    value={formData.surname}
                    onChange={handleChange}
                    className="w-full border border-gray-300 rounded-md p-2"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Отчество</label>
              <input
                  type="text"
                  name="patronymic"
                  value={formData.patronymic}
                  onChange={handleChange}
                  className="w-full border border-gray-300 rounded-md p-2"
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Возраст</label>
              <input
                  type="number"
                  name="age"
                  value={formData.age}
                  onChange={handleChange}
                  min="0"
                  max="120"
                  className="w-full border border-gray-300 rounded-md p-2"
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Телефон</label>
              <input
                  type="tel"
                  name="phoneNumber"
                  value={formData.phoneNumber}
                  onChange={handleChange}
                  placeholder="+7 (999) 123-45-67"
                  className="w-full border border-gray-300 rounded-md p-2"
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Email</label>
              <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  className="w-full border border-gray-300 rounded-md p-2"
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Логин</label>
              <input
                  type="text"
                  name="login"
                  value={formData.login}
                  disabled
                  className="w-full border border-gray-300 rounded-md p-2 bg-gray-100"
              />
              <p className="text-xs text-gray-500 mt-1">Логин нельзя изменить</p>
            </div>

            {/* Поля для смены пароля */}
            <div className="border-t pt-4 mt-2">
              <h2 className="text-lg font-semibold mb-3">Смена пароля</h2>
              <div>
                <label className="block text-sm font-medium mb-1">Новый пароль</label>
                <input
                    type="password"
                    name="newPassword"
                    value={passwordData.newPassword}
                    onChange={handlePasswordChange}
                    className="w-full border border-gray-300 rounded-md p-2"
                />
              </div>
              <div className="mt-2">
                <label className="block text-sm font-medium mb-1">Подтверждение пароля</label>
                <input
                    type="password"
                    name="confirmPassword"
                    value={passwordData.confirmPassword}
                    onChange={handlePasswordChange}
                    className="w-full border border-gray-300 rounded-md p-2"
                />
              </div>
            </div>

            <div className="pt-4">
              <button
                  type="submit"
                  disabled={loading}
                  className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 disabled:opacity-50"
              >
                {loading ? 'Сохранение...' : 'Сохранить изменения'}
              </button>
            </div>
          </form>
        </div>
      </div>
  );
}