import { API_URL } from "../config";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
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

function formatDateTime(iso) {
  if (!iso) return "";
  return new Date(iso).toLocaleString("ru-RU", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function round(n) {
  if (n === null || n === undefined) return "-";
  return Math.round(n * 10) / 10;
}

const HISTORY_PAGE_SIZE = 10;

export default function Statistics() {
  const { gameId } = useParams();
  const navigate = useNavigate();
  const [game, setGame] = useState(null);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [history, setHistory] = useState({ content: [], page: 0, totalPages: 0 });
  const [historyPage, setHistoryPage] = useState(0);
  const [historyLoading, setHistoryLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    setError("");
    Promise.all([
      fetch(`${API_URL}/api/games/${gameId}`).then((r) => {
        if (!r.ok) throw new Error("Игра не найдена");
        return r.json();
      }),
      fetch(`${API_URL}/api/games/${gameId}/statistics`).then((r) => {
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

  useEffect(() => {
    setHistoryLoading(true);
    fetch(
      `${API_URL}/api/sessions/history?gameId=${gameId}&page=${historyPage}&size=${HISTORY_PAGE_SIZE}`
    )
      .then((r) => {
        if (!r.ok) throw new Error("Не удалось загрузить историю партий");
        return r.json();
      })
      .then(setHistory)
      .catch(() => {})
      .finally(() => setHistoryLoading(false));
  }, [gameId, historyPage]);

  if (loading) {
    return (
      <>
        <Header />
        <div className="page">
          <div className="container stats-page">
            <p>Загрузка...</p>
          </div>
        </div>
      </>
    );
  }

  if (error) {
    return (
      <>
        <Header />
        <div className="page">
          <div className="container stats-page">
            <h2>Ошибка</h2>
            <p className="stats-empty">{error}</p>
          </div>
        </div>
      </>
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

          <div className="stats-section">
            <h3>История партий</h3>
            {historyLoading ? (
              <p>Загрузка...</p>
            ) : history.content.length === 0 ? (
              <p className="stats-empty">Завершённых партий пока нет</p>
            ) : (
              <>
                <table className="stats-history-table">
                  <thead>
                    <tr>
                      <th>Дата</th>
                      <th>Игроки</th>
                      <th>Победитель</th>
                    </tr>
                  </thead>
                  <tbody>
                    {history.content.map((s) => (
                      <tr
                        key={s.id}
                        onClick={() => navigate(`/session/${s.id}`)}
                        className="stats-history-row"
                      >
                        <td>{formatDateTime(s.startedAt)}</td>
                        <td>{(s.playerNames || []).join(", ")}</td>
                        <td>{s.winnerName ? `🏆 ${s.winnerName}` : "-"}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>

                {history.totalPages > 1 && (
                  <div className="stats-history-pagination">
                    <button
                      className="stats-page-btn"
                      onClick={() => setHistoryPage((p) => Math.max(0, p - 1))}
                      disabled={historyPage === 0}
                    >
                      ←
                    </button>
                    {Array.from({ length: history.totalPages }, (_, i) => i).map((i) => (
                      <button
                        key={i}
                        className={`stats-page-btn ${i === historyPage ? "active" : ""}`}
                        onClick={() => setHistoryPage(i)}
                      >
                        {i + 1}
                      </button>
                    ))}
                    <button
                      className="stats-page-btn"
                      onClick={() => setHistoryPage((p) => Math.min(history.totalPages - 1, p + 1))}
                      disabled={historyPage >= history.totalPages - 1}
                    >
                      →
                    </button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
