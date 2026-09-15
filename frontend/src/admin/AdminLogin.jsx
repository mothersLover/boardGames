import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "./adminApi";
import "./Admin.css";

export default function AdminLogin() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await login(username, password);
      navigate("/admin/games");
    } catch (err) {
      setError(err.message || "Не удалось войти");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="admin-login-page">
      <form className="admin-login-card" onSubmit={handleSubmit}>
        <h2>Вход в админ-панель</h2>

        {error && <div className="admin-error">{error}</div>}

        <div className="admin-field">
          <label htmlFor="username">Логин</label>
          <input
            id="username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoFocus
            required
          />
        </div>

        <div className="admin-field">
          <label htmlFor="password">Пароль</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <button className="admin-btn" type="submit" disabled={loading}>
          {loading ? "Проверка..." : "Войти"}
        </button>
      </form>
    </div>
  );
}
