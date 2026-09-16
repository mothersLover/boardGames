import { NavLink, useNavigate, useParams } from "react-router-dom";
import "./Header.css";

export default function Header({ logo = "🎲 BoardGames", logoSrc, logoAlt = "Logo" }) {
  const navigate = useNavigate();
  const { gameId } = useParams();
  const handleLogoClick = () => {
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
        <NavLink to={`/game/${gameId}`} end>Новая игра</NavLink>
        <NavLink to={`/game/${gameId}/sessions`}>Активные сессии</NavLink>
        <NavLink to={`/game/${gameId}/multimedia`}>Мультимедиа</NavLink>
        <NavLink to={`/game/${gameId}/statistics`}>Статистика</NavLink>
        <NavLink to={`/game/${gameId}/rules`}>Правила</NavLink>
      </nav>
    </header>
  );
}
