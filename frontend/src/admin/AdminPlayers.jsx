import { useEffect, useState } from "react";
import { adminApi } from "./adminApi";
import "./Admin.css";

const emptyForm = {
  userName: "",
  displayName: "",
  email: "",
  rating: "",
};

function toFormValues(player) {
  return {
    userName: player.userName || "",
    displayName: player.displayName || "",
    email: player.email || "",
    rating: player.rating ?? "",
  };
}

function toPayload(form) {
  return {
    userName: form.userName,
    displayName: form.displayName,
    email: form.email,
    rating: form.rating === "" ? null : Number(form.rating),
  };
}

export default function AdminPlayers() {
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);

  const loadPlayers = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await adminApi.get("/players");
      setPlayers(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPlayers();
  }, []);

  const startCreate = () => {
    setEditingId(null);
    setForm(emptyForm);
    setShowForm(true);
  };

  const startEdit = (player) => {
    setEditingId(player.id);
    setForm(toFormValues(player));
    setShowForm(true);
  };

  const cancelForm = () => {
    setShowForm(false);
    setEditingId(null);
    setForm(emptyForm);
  };

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      const payload = toPayload(form);
      if (editingId) {
        await adminApi.put(`/players/${editingId}`, payload);
      } else {
        await adminApi.post("/players", payload);
      }
      cancelForm();
      await loadPlayers();
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (player) => {
    if (!window.confirm(`Удалить игрока "${player.displayName}"?`)) return;
    setError("");
    try {
      await adminApi.del(`/players/${player.id}`);
      await loadPlayers();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h2>Игроки</h2>

      {error && <div className="admin-error">{error}</div>}

      <div className="admin-toolbar">
        {!showForm && (
          <button className="admin-btn" onClick={startCreate}>
            Добавить игрока
          </button>
        )}
      </div>

      {showForm && (
        <form className="admin-form-card" onSubmit={handleSubmit}>
          <h3>{editingId ? "Редактировать игрока" : "Новый игрок"}</h3>

          <div className="admin-field">
            <label>Логин (username)</label>
            <input value={form.userName} onChange={handleChange("userName")} required />
          </div>

          <div className="admin-field">
            <label>Отображаемое имя</label>
            <input value={form.displayName} onChange={handleChange("displayName")} required />
          </div>

          <div className="admin-field">
            <label>Email</label>
            <input type="email" value={form.email} onChange={handleChange("email")} />
          </div>

          <div className="admin-field">
            <label>Рейтинг</label>
            <input type="number" step="0.1" value={form.rating} onChange={handleChange("rating")} />
          </div>

          <div className="admin-form-actions">
            <button className="admin-btn" type="submit" disabled={saving}>
              {saving ? "Сохранение..." : "Сохранить"}
            </button>
            <button className="admin-btn secondary" type="button" onClick={cancelForm}>
              Отмена
            </button>
          </div>
        </form>
      )}

      {loading ? (
        <p>Загрузка...</p>
      ) : (
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Логин</th>
              <th>Имя</th>
              <th>Email</th>
              <th>Рейтинг</th>
              <th>Партий</th>
              <th>Побед</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {players.map((player) => (
              <tr key={player.id}>
                <td>{player.id}</td>
                <td>{player.userName}</td>
                <td>{player.displayName}</td>
                <td>{player.email}</td>
                <td>{player.rating ?? "-"}</td>
                <td>{player.totalGames ?? 0}</td>
                <td>{player.wins ?? 0}</td>
                <td className="actions">
                  <button className="admin-btn" onClick={() => startEdit(player)}>
                    Изменить
                  </button>
                  <button className="admin-btn danger" onClick={() => handleDelete(player)}>
                    Удалить
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
