import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { logout } from "./adminApi";
import "./Admin.css";

export default function AdminLayout() {
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/admin/login");
  };

  return (
    <div className="admin-layout">
      <header className="admin-topbar">
        <h1>Админ-панель</h1>
        <nav className="admin-nav">
          <NavLink to="/admin/games">Игры</NavLink>
          <NavLink to="/admin/players">Игроки</NavLink>
          <NavLink to="/admin/news">Новости</NavLink>
        </nav>
        <button className="admin-btn secondary" onClick={handleLogout}>
          Выйти
        </button>
      </header>

      <div className="admin-content">
        <Outlet />
      </div>
    </div>
  );
}
