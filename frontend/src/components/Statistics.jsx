import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import Header from "./Header";
import "./Statistics.css";

function formatDate(iso) {
  if (!iso) return "";
  return new Date(iso).toLocaleDateString("ru-RU", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

function round(n) {
  if (n === null || n === undefined) return "-";
  return Math.round(n * 10) / 10;
}

export default function Statistics() {
  const { gameId } = useParams();
  const [game, setGame] = useState(null);
  const [stats, setStats] = useState(null);
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
      fetch(`http://localhost:8080/api/games/${gameId}/statistics`).then((r) => {
        if (!r.ok) throw new Error("Не удалось загрузить статистику");
        return r.json();
      }),
    ])
      .then(([gameData, statsData]) => {
        setGame(gameData);
        setStats(statsData);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [gameId]);

  if (loading) {
    return (
      <div className="page">
        <div className="container stats-page">
          <p>Загрузка...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page">
        <div className="container stats-page">
          <h2>Ошибка</h2>
          <p className="stats-empty">{error}</p>
        </div>
      </div>
    );
  }

  const noData = !stats || stats.totalSessions === 0;

  return (
    <>
      <Header logoSrc={game?.logoUrl} logoAlt={game?.name} />
      <div className="page">
        <div className="container stats-page">
          <h2>Статистика: {game?.name}</h2>

          {noData ? (
            <p className="stats-empty">
              Пока нет завершённых партий — сыграйте хотя бы одну и сохраните результаты!
            </p>
          ) : (
            <>
              <div className="stats-metrics">
                <div className="stats-metric-card">
                  <div className="stats-metric-value">{stats.totalSessions}</div>
                  <div className="stats-metric-label">Сыграно партий</div>
                </div>
                <div className="stats-metric-card">
                  <div className="stats-metric-value">{stats.totalPlayers}</div>
                  <div className="stats-metric-label">Игроков</div>
                </div>
                <div className="stats-metric-card">
                  <div className="stats-metric-value">{round(stats.averageScore)}</div>
                  <div className="stats-metric-label">Средний счёт за партию</div>
                </div>
              </div>

              <div className="stats-highlights">
                {stats.highestScore && (
                  <div className="stats-highlight-card best">
                    <div className="stats-highlight-title">🏆 Лучший результат</div>
                    <div className="stats-highlight-value">{round(stats.highestScore.value)}</div>
                    <div className="stats-highlight-meta">
                      {stats.highestScore.playerName} · {formatDate(stats.highestScore.date)}
                    </div>
                  </div>
                )}
                {stats.lowestScore && (
                  <div className="stats-highlight-card worst">
                    <div className="stats-highlight-title">📉 Наименьший результат</div>
                    <div className="stats-highlight-value">{round(stats.lowestScore.value)}</div>
                    <div className="stats-highlight-meta">
                      {stats.lowestScore.playerName} · {formatDate(stats.lowestScore.date)}
                    </div>
                  </div>
                )}
              </div>

              <div className="stats-section">
                <h3>Таблица лидеров</h3>
                <table className="stats-leaderboard">
                  <thead>
                    <tr>
                      <th>Игрок</th>
                      <th>Партий</th>
                      <th>Побед</th>
                      <th>% побед</th>
                      <th>Средний счёт</th>
                      <th>Лучший счёт</th>
                    </tr>
                  </thead>
                  <tbody>
                    {(stats.leaderboard || []).map((p, i) => (
                      <tr key={p.playerId}>
                        <td>
                          <span className="stats-rank">
                            {i === 0 ? "🥇" : i === 1 ? "🥈" : i === 2 ? "🥉" : ""}
                          </span>
                          {p.playerName}
                        </td>
                        <td>{p.sessionsPlayed}</td>
                        <td>{p.wins}</td>
                        <td>{round(p.winRate)}%</td>
                        <td>{round(p.averageScore)}</td>
                        <td>{round(p.bestScore)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {stats.scoreTypeHighlights && stats.scoreTypeHighlights.length > 0 && (
                <div className="stats-section">
                  <h3>Рекорды по показателям</h3>
                  <div className="stats-score-type-grid">
                    {stats.scoreTypeHighlights.map((h) => (
                      <div
                        className="stats-score-type-card"
                        key={h.scoreTypeName}
                        style={{ borderLeftColor: h.colorCode || "#b49696" }}
                      >
                        <div className="stats-score-type-name">{h.scoreTypeName}</div>
                        <div className="stats-score-type-value">{round(h.value)}</div>
                        <div className="stats-score-type-player">{h.playerName}</div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </>
  );
}
