import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import "./SessionResults.css";

function formatDate(iso) {
  if (!iso) return "";
  return new Date(iso).toLocaleDateString("ru-RU", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

export default function SessionResults() {
  const { sessionId } = useParams();
  const [session, setSession] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [values, setValues] = useState({});
  const [comment, setComment] = useState("");
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);

  const loadSession = () => {
    setLoading(true);
    setError("");
    fetch(`http://localhost:8080/api/sessions/${sessionId}`)
      .then((r) => {
        if (!r.ok) throw new Error("Партия не найдена");
        return r.json();
      })
      .then((data) => {
        setSession(data);
        setComment(data.comment || "");
        const initialValues = {};
        (data.scores || []).forEach((s) => {
          initialValues[s.id] = s.value ?? 0;
        });
        setValues(initialValues);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadSession();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sessionId]);

  const scoreByPlayerAndType = useMemo(() => {
    const map = {};
    (session?.scores || []).forEach((s) => {
      map[`${s.playerId}:${s.scoreTypeId}`] = s;
    });
    return map;
  }, [session]);

  const totals = useMemo(() => {
    const result = {};
    (session?.players || []).forEach((p) => {
      result[p.id] = 0;
    });
    (session?.scoreTypes || []).forEach((st) => {
      (session?.players || []).forEach((p) => {
        const score = scoreByPlayerAndType[`${p.id}:${st.id}`];
        if (!score) return;
        const value = Number(values[score.id] ?? 0);
        const weight = st.weight ?? 1;
        result[p.id] += value * weight;
      });
    });
    return result;
  }, [session, values, scoreByPlayerAndType]);

  const leaderId = useMemo(() => {
    const entries = Object.entries(totals);
    if (entries.length === 0) return null;
    const max = Math.max(...entries.map(([, v]) => v));
    if (max <= 0) return null;
    const leader = entries.find(([, v]) => v === max);
    return leader ? Number(leader[0]) : null;
  }, [totals]);

  const handleValueChange = (scoreId, value) => {
    setValues((prev) => ({ ...prev, [scoreId]: value }));
    setSaved(false);
  };

  const handleSave = async () => {
    setSaving(true);
    setError("");
    try {
      const payload = {
        scores: Object.entries(values).map(([scoreId, value]) => ({
          scoreId: Number(scoreId),
          value: value === "" ? 0 : Number(value),
        })),
        comment,
      };

      const response = await fetch(
        `http://localhost:8080/api/sessions/${sessionId}/results`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        }
      );

      if (!response.ok) {
        const errData = await response.json().catch(() => null);
        throw new Error(errData?.message || "Не удалось сохранить результаты");
      }

      const updated = await response.json();
      setSession(updated);
      setSaved(true);
      setTimeout(() => setSaved(false), 2500);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="page">
        <div className="container results-page">
          <p>Загрузка...</p>
        </div>
      </div>
    );
  }

  if (error && !session) {
    return (
      <div className="page">
        <div className="container results-page">
          <h2>Ошибка</h2>
          <p className="results-error">{error}</p>
          <Link to="/" className="results-back-link">← На главную</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="container results-page">
        <div className="results-header">
          <div className="results-header-info">
            {session.gameLogoUrl && (
              <img src={session.gameLogoUrl} alt={session.gameName} className="results-header-logo" />
            )}
            <div>
              <h2>
                {session.gameName}
                {session.isCompleted && <span className="results-badge">Завершена</span>}
              </h2>
              <p className="results-header-meta">
                {formatDate(session.startedAt)} · {(session.players || []).length} игрока(ов)
              </p>
            </div>
          </div>
          <Link to={`/game/${session.gameId}`} className="results-back-link">← Назад к игре</Link>
        </div>

        {error && <p className="results-error">{error}</p>}

        <div className="results-table-wrapper">
          <table className="results-table">
            <thead>
              <tr>
                <th>Показатель</th>
                {(session.players || []).map((p) => (
                  <th key={p.id}>{p.displayName}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {(session.scoreTypes || []).map((st) => (
                <tr key={st.id}>
                  <td title={st.description || ""}>
                    <span
                      className="results-type-dot"
                      style={{ background: st.colorCode || "#b49696" }}
                      aria-hidden="true"
                    />
                    {st.name}
                  </td>
                  {(session.players || []).map((p) => {
                    const score = scoreByPlayerAndType[`${p.id}:${st.id}`];
                    if (!score) return <td key={p.id}>—</td>;
                    return (
                      <td key={p.id}>
                        <input
                          type="number"
                          value={values[score.id] ?? 0}
                          onChange={(e) => handleValueChange(score.id, e.target.value)}
                        />
                      </td>
                    );
                  })}
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr className="results-total-row">
                <td>Итого</td>
                {(session.players || []).map((p) => (
                  <td key={p.id}>
                    {Math.round(totals[p.id] ?? 0)}
                    {leaderId === p.id && (
                      <span className="results-crown" title="Лидер">🏆</span>
                    )}
                  </td>
                ))}
              </tr>
            </tfoot>
          </table>
        </div>

        <div className="results-comment">
          <label htmlFor="sessionComment">Комментарий к партии</label>
          <textarea
            id="sessionComment"
            rows={3}
            value={comment}
            onChange={(e) => {
              setComment(e.target.value);
              setSaved(false);
            }}
            placeholder="Например: играли с новыми правилами, было очень напряжённо"
          />
        </div>

        <div className="results-actions">
          <button className="results-save-btn" onClick={handleSave} disabled={saving}>
            {saving ? "Сохранение..." : "Сохранить результаты"}
          </button>
          {saved && <span className="results-saved-note">✓ Сохранено</span>}
        </div>
      </div>
    </div>
  );
}
