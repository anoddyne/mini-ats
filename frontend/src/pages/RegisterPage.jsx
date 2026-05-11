import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authAPI } from '../api/endpoints';

export default function RegisterPage() {
  const navigate = useNavigate();
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    surname: '',
    patronymic: '',
    age: '',
    phoneNumber: '',
    email: '',
    login: '',
    password: '',
    role: 'CANDIDATE',
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (errors[e.target.name]) {
      setErrors({ ...errors, [e.target.name]: '' });
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.name.trim()) newErrors.name = 'Имя обязательно';
    if (!formData.surname.trim()) newErrors.surname = 'Фамилия обязательна';
    
    if (formData.age && (formData.age < 18 || formData.age > 120)) {
      newErrors.age = 'Возраст должен быть от 18 до 120 лет';
    }
    
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!formData.email.trim()) {
      newErrors.email = 'Email обязателен';
    } else if (!emailRegex.test(formData.email)) {
      newErrors.email = 'Неверный формат email';
    }
    
    if (!formData.login.trim()) {
      newErrors.login = 'Логин обязателен';
    } else if (formData.login.length < 3 || formData.login.length > 50) {
      newErrors.login = 'Логин должен быть от 3 до 50 символов';
    }
    
    if (!formData.password) {
      newErrors.password = 'Пароль обязателен';
    } else if (formData.password.length < 8 || formData.password.length > 100) {
      newErrors.password = 'Пароль должен быть от 8 до 100 символов';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    
    setLoading(true);
    try {
      await authAPI.register(formData);
      alert('Регистрация успешна! Теперь вы можете войти.');
      navigate('/login');
    } catch (err) {
      const message = err.response?.data?.message || 'Ошибка регистрации';
      alert(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100 py-8">
      <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
        <h1 className="text-2xl font-bold text-center mb-6">Регистрация</h1>
        
        <form onSubmit={handleSubmit} className="space-y-3">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <input
                type="text"
                name="name"
                placeholder="Имя *"
                className={`w-full p-2 border ${errors.name ? 'border-red-500' : 'border-gray-300'} rounded`}
                value={formData.name}
                onChange={handleChange}
              />
              {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name}</p>}
            </div>
            <div>
              <input
                type="text"
                name="surname"
                placeholder="Фамилия *"
                className={`w-full p-2 border ${errors.surname ? 'border-red-500' : 'border-gray-300'} rounded`}
                value={formData.surname}
                onChange={handleChange}
              />
              {errors.surname && <p className="text-red-500 text-xs mt-1">{errors.surname}</p>}
            </div>
          </div>

          <div>
            <input
              type="text"
              name="patronymic"
              placeholder="Отчество"
              className="w-full p-2 border border-gray-300 rounded"
              value={formData.patronymic}
              onChange={handleChange}
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <input
                type="number"
                name="age"
                placeholder="Возраст (18+)"
                className={`w-full p-2 border ${errors.age ? 'border-red-500' : 'border-gray-300'} rounded`}
                value={formData.age}
                onChange={handleChange}
              />
              {errors.age && <p className="text-red-500 text-xs mt-1">{errors.age}</p>}
            </div>
            <div>
              <input
                type="tel"
                name="phoneNumber"
                placeholder="Телефон"
                className="w-full p-2 border border-gray-300 rounded"
                value={formData.phoneNumber}
                onChange={handleChange}
              />
            </div>
          </div>

          <div>
            <input
              type="email"
              name="email"
              placeholder="Email *"
              className={`w-full p-2 border ${errors.email ? 'border-red-500' : 'border-gray-300'} rounded`}
              value={formData.email}
              onChange={handleChange}
            />
            {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email}</p>}
          </div>

          <div>
            <input
              type="text"
              name="login"
              placeholder="Логин (3-50 символов) *"
              className={`w-full p-2 border ${errors.login ? 'border-red-500' : 'border-gray-300'} rounded`}
              value={formData.login}
              onChange={handleChange}
            />
            {errors.login && <p className="text-red-500 text-xs mt-1">{errors.login}</p>}
          </div>

          <div>
            <input
              type="password"
              name="password"
              placeholder="Пароль (8-100 символов) *"
              className={`w-full p-2 border ${errors.password ? 'border-red-500' : 'border-gray-300'} rounded`}
              value={formData.password}
              onChange={handleChange}
            />
            {errors.password && <p className="text-red-500 text-xs mt-1">{errors.password}</p>}
          </div>

          <div>
            <select
              name="role"
              className="w-full p-2 border border-gray-300 rounded"
              value={formData.role}
              onChange={handleChange}
            >
              <option value="CANDIDATE">Кандидат</option>
              <option value="RECRUITER">Рекрутер</option>
              <option value="ADMIN">Администратор</option>
            </select>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white p-2 rounded hover:bg-blue-700 disabled:opacity-50"
          >
            {loading ? 'Регистрация...' : 'Зарегистрироваться'}
          </button>
        </form>
        
        <p className="text-center mt-4">
          Уже есть аккаунт? <Link to="/login" className="text-blue-600">Войти</Link>
        </p>
      </div>
    </div>
  );
}