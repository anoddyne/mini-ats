import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authAPI } from '../api/endpoints';

export default function RegisterPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '', surname: '', email: '', login: '', password: '', role: 'CANDIDATE'
  });
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await authAPI.register(formData);
      navigate('/login');
    } catch (err) {
      setError('Ошибка регистрации');
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-96">
        <h1 className="text-2xl font-bold text-center mb-6">Регистрация</h1>
        {error && <div className="bg-red-100 text-red-700 p-2 rounded mb-4">{error}</div>}
        <form onSubmit={handleSubmit}>
          <input type="text" placeholder="Имя" className="w-full p-2 border rounded mb-2"
            onChange={(e) => setFormData({...formData, name: e.target.value})} />
          <input type="text" placeholder="Фамилия" className="w-full p-2 border rounded mb-2"
            onChange={(e) => setFormData({...formData, surname: e.target.value})} />
          <input type="email" placeholder="Email" className="w-full p-2 border rounded mb-2"
            onChange={(e) => setFormData({...formData, email: e.target.value})} />
          <input type="text" placeholder="Логин" className="w-full p-2 border rounded mb-2"
            onChange={(e) => setFormData({...formData, login: e.target.value})} />
          <input type="password" placeholder="Пароль" className="w-full p-2 border rounded mb-2"
            onChange={(e) => setFormData({...formData, password: e.target.value})} />
          <select className="w-full p-2 border rounded mb-4"
            onChange={(e) => setFormData({...formData, role: e.target.value})}>
            <option value="CANDIDATE">Кандидат</option>
            <option value="RECRUITER">Рекрутер</option>
          </select>
          <button type="submit" className="w-full bg-blue-600 text-white p-2 rounded hover:bg-blue-700">
            Зарегистрироваться
          </button>
        </form>
        <p className="text-center mt-4">
          Уже есть аккаунт? <Link to="/login" className="text-blue-600">Войти</Link>
        </p>
      </div>
    </div>
  );
}