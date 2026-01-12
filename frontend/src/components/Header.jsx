import { NavLink, useLocation, useNavigate } from "react-router-dom";
import "./Header.css";

export default function Header({ logo = "🎲 BoardGames", logoSrc, logoAlt = "Logo" }) {
  const location = useLocation();
  const navigate = useNavigate();
  const currentPath = location.pathname; // получаем текущий путь
  const handleLogoClick = () => {
    // Можно добавить дополнительную логику
    console.log("Переход на главную");
    navigate("/");
  };
  return (
    <header className="header">
        <div className="logo" onClick={handleLogoClick} style={{ cursor: 'pointer' }}>
        {logoSrc ? (
          <img 
            src={logoSrc} 
            alt={logoAlt}
            className="logo-image"
          />
        ) : (
          <span className="logo-text">{logo}</span>
        )}
      </div>

      <nav className="nav">
        <NavLink to="." end>Новая игра</NavLink>
        <NavLink to="multimedia">Мультимедиа</NavLink>
        <NavLink to="statistics">Статистика</NavLink>
        <NavLink to="rules">Правила</NavLink>
      </nav>
    </header>
  );
}