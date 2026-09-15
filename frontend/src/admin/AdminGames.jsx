import { useEffect, useState } from "react";
import { adminApi } from "./adminApi";
import "./Admin.css";

const emptyForm = {
  name: "",
  description: "",
  genre: "",
  minPlayers: "",
  maxPlayers: "",
  ageRating: "",
  price: "",
  isActive: true,
};

function toFormValues(game) {
  return {
    name: game.name || "",
    description: game.description || "",
    genre: game.genre || "",
    minPlayers: game.minPlayers ?? "",
    maxPlayers: game.maxPlayers ?? "",
    ageRating: game.ageRating ?? "",
    price: game.price ?? "",
    isActive: game.isActive ?? true,
  };
}

function toPayload(form) {
  return {
    name: form.name,
    description: form.description,
    genre: form.genre,
    minPlayers: form.minPlayers === "" ? null : Number(form.minPlayers),
    maxPlayers: form.maxPlayers === "" ? null : Number(form.maxPlayers),
    ageRating: form.ageRating === "" ? null : Number(form.ageRating),
    price: form.price === "" ? null : Number(form.price),
    isActive: form.isActive,
  };
}

export default function AdminGames() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);

  const loadGames = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await adminApi.get("/games");
      setGames(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadGames();
  }, []);

  const startCreate = () => {
    setEditingId(null);
    setForm(emptyForm);
    setShowForm(true);
  };

  const startEdit = (game) => {
    setEditingId(game.id);
    setForm(toFormValues(game));
    setShowForm(true);
  };

  const cancelForm = () => {
    setShowForm(false);
    setEditingId(null);
    setForm(emptyForm);
  };

  const handleChange = (field) => (e) => {
    const value = e.target.type === "checkbox" ? e.target.checked : e.target.value;
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      const payload = toPayload(form);
      if (editingId) {
        await adminApi.put(`/games/${editingId}`, payload);
      } else {
        await adminApi.post("/games", payload);
      }
      cancelForm();
      await loadGames();
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (game) => {
    if (!window.confirm(`Удалить игру "${game.name}"?`)) return;
    setError("");
    try {
      await adminApi.del(`/games/${game.id}`);
      await loadGames();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h2>Игры</h2>

      {error && <div className="admin-error">{error}</div>}

      <div className="admin-toolbar">
        {!showForm && (
          <button className="admin-btn" onClick={startCreate}>
            Добавить игру
          </button>
        )}
      </div>

      {showForm && (
        <form className="admin-form-card" onSubmit={handleSubmit}>
          <h3>{editingId ? "Редактировать игру" : "Новая игра"}</h3>

          <div className="admin-field">
            <label>Название</label>
            <input value={form.name} onChange={handleChange("name")} required minLength={3} maxLength={100} />
          </div>

          <div className="admin-field">
            <label>Описание</label>
            <textarea value={form.description} onChange={handleChange("description")} rows={3} maxLength={500} />
          </div>

          <div className="admin-field">
            <label>Жанр</label>
            <input value={form.genre} onChange={handleChange("genre")} />
          </div>

          <div className="admin-field">
            <label>Мин. игроков</label>
            <input type="number" min="1" value={form.minPlayers} onChange={handleChange("minPlayers")} />
          </div>

          <div className="admin-field">
            <label>Макс. игроков</label>
            <input type="number" min="1" value={form.maxPlayers} onChange={handleChange("maxPlayers")} />
          </div>

          <div className="admin-field">
            <label>Возрастной рейтинг</label>
            <input type="number" min="0" value={form.ageRating} onChange={handleChange("ageRating")} />
          </div>

          <div className="admin-field">
            <label>Цена</label>
            <input type="number" min="0" step="0.01" value={form.price} onChange={handleChange("price")} />
          </div>

          <div className="admin-checkbox-field">
            <input
              id="isActive"
              type="checkbox"
              checked={form.isActive}
              onChange={handleChange("isActive")}
            />
            <label htmlFor="isActive">Активна</label>
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
              <th>Название</th>
              <th>Жанр</th>
              <th>Игроков</th>
              <th>Возраст</th>
              <th>Цена</th>
              <th>Активна</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {games.map((game) => (
              <tr key={game.id}>
                <td>{game.id}</td>
                <td>{game.name}</td>
                <td>{game.genre}</td>
                <td>
                  {game.minPlayers ?? "?"}–{game.maxPlayers ?? "?"}
                </td>
                <td>{game.ageRating ?? "-"}</td>
                <td>{game.price ?? "-"}</td>
                <td>{game.isActive ? "Да" : "Нет"}</td>
                <td className="actions">
                  <button className="admin-btn" onClick={() => startEdit(game)}>
                    Изменить
                  </button>
                  <button className="admin-btn danger" onClick={() => handleDelete(game)}>
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
