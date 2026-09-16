import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import Header from "./Header";
import "./ActiveSessions.css";

function formatDate(iso) {
  if (!iso) return "";
  return new Date(iso).toLocaleString("ru-RU", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export default function ActiveSessions() {
  const { gameId } = useParams();
  const [game, setGame] = useState(null);
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    setError("");
    Promise.all([
      fetch(`http://localhost:8080/api/games/${gameId}`).then((r) => {
        if (!r.ok) throw new Error("Игра не найдена");
        return r.json();
      }),
      fetch(`http://localhost:8080/api/sessions/active?gameId=${gameId}`).then((r) => {
        if (!r.ok) throw new Error("Не удалось загрузить сессии");
        return r.json();
      }),
    ])
      .then(([gameData, sessionsData]) => {
        setGame(gameData);
        setSessions(sessionsData);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [gameId]);

  if (loading) {
    return (
      <div className="page">
        <div className="container sessions-page">
          <p>Загрузка...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page">
        <div className="container sessions-page">
          <h2>Ошибка</h2>
          <p className="sessions-empty">{error}</p>
        </div>
      </div>
    );
  }

  return (
    <>
      <Header logoSrc={game?.logoUrl} logoAlt={game?.name} />
      <div className="page">
        <div className="container sessions-page">
          <h2>Активные сессии: {game?.name}</h2>

          {sessions.length === 0 ? (
            <p className="sessions-empty">Незавершённых партий нет — самое время начать новую!</p>
          ) : (
            <div className="sessions-list">
              {sessions.map((s) => (
                <Link to={`/session/${s.id}`} className="session-card" key={s.id}>
                  <div className="session-card-top">
                    <span className="session-card-date">{formatDate(s.startedAt)}</span>
                    {s.location && <span className="session-card-location">{s.location}</span>}
                    <span className="session-card-badge">Не завершена</span>
                  </div>
                  <div className="session-card-players">
                    {(s.playerNames || []).join(", ") || "Без игроков"}
                  </div>
                </Link>
              ))}
            </div>
          )}
        </div>
      </div>
    </>
  );
}
