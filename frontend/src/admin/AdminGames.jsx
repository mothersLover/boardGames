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

const MEDIA_TYPES = [
  { type: "AUDIO", label: "Аудио", pathsKey: "audioPaths", urlsKey: "audioUrls" },
  { type: "VIDEO", label: "Видео", pathsKey: "videoPaths", urlsKey: "videoUrls" },
  { type: "INSTRUCTION", label: "Инструкции", pathsKey: "instructionPaths", urlsKey: "instructionUrls" },
  { type: "OTHER", label: "Другое", pathsKey: "otherPaths", urlsKey: "otherUrls" },
];

function fileNameOf(path) {
  return path.split("/").pop();
}

function MediaTypeSection({
  label,
  paths,
  urls,
  disabled,
  uploading,
  pendingFile,
  onSelectFile,
  onClearPending,
  onDelete,
}) {
  return (
    <div className="admin-media-section">
      <h4>{label}</h4>
      <ul className="admin-media-list">
        {paths.length === 0 && <li className="admin-media-empty">Файлов нет</li>}
        {paths.map((path, i) => (
          <li key={path}>
            <a href={urls[i]} target="_blank" rel="noreferrer">{fileNameOf(path)}</a>
            <button
              type="button"
              className="admin-btn danger"
              disabled={disabled}
              onClick={() => onDelete(path)}
            >
              Удалить
            </button>
          </li>
        ))}
      </ul>

      {uploading ? (
        <div className="admin-media-pending uploading">
          Загрузка «{pendingFile?.name}»...
        </div>
      ) : pendingFile ? (
        <div className="admin-media-pending">
          <span>Выбран «{pendingFile.name}» — загрузится при сохранении</span>
          <button type="button" className="admin-btn secondary" onClick={onClearPending}>
            ✕
          </button>
        </div>
      ) : (
        <input
          type="file"
          disabled={disabled}
          onChange={(e) => {
            const file = e.target.files && e.target.files[0];
            if (file) onSelectFile(file);
            e.target.value = "";
          }}
        />
      )}
    </div>
  );
}

const emptyScoreTypeForm = {
  name: "",
  description: "",
  weight: "1",
  displayOrder: "",
  colorCode: "#4a90d9",
};

function scoreTypeToForm(st) {
  return {
    name: st.name || "",
    description: st.description || "",
    weight: st.weight ?? "1",
    displayOrder: st.displayOrder ?? "",
    colorCode: st.colorCode || "#4a90d9",
  };
}

function scoreTypeToPayload(form) {
  return {
    name: form.name,
    description: form.description,
    weight: form.weight === "" ? 1 : Number(form.weight),
    displayOrder: form.displayOrder === "" ? null : Number(form.displayOrder),
    colorCode: form.colorCode,
  };
}

function ScoreTypesManager({ gameId }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(emptyScoreTypeForm);
  const [saving, setSaving] = useState(false);

  const load = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await adminApi.get(`/games/${gameId}/score-types`);
      setItems(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [gameId]);

  const startCreate = () => {
    setEditingId(null);
    setForm(emptyScoreTypeForm);
    setShowForm(true);
  };

  const startEdit = (st) => {
    setEditingId(st.id);
    setForm(scoreTypeToForm(st));
    setShowForm(true);
  };

  const cancelForm = () => {
    setShowForm(false);
    setEditingId(null);
    setForm(emptyScoreTypeForm);
  };

  const handleChange = (field) => (e) => {
    const value = e.target.value;
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      const payload = scoreTypeToPayload(form);
      if (editingId) {
        await adminApi.put(`/games/${gameId}/score-types/${editingId}`, payload);
      } else {
        await adminApi.post(`/games/${gameId}/score-types`, payload);
      }
      cancelForm();
      await load();
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (st) => {
    if (!window.confirm(`Удалить тип очков "${st.name}"?`)) return;
    setError("");
    try {
      await adminApi.del(`/games/${gameId}/score-types/${st.id}`);
      await load();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      {error && <div className="admin-error">{error}</div>}

      {loading ? (
        <p>Загрузка...</p>
      ) : (
        <ul className="admin-media-list">
          {items.length === 0 && <li className="admin-media-empty">Типов очков ещё нет</li>}
          {items.map((st) => (
            <li key={st.id}>
              <span style={{ display: "flex", alignItems: "center", gap: 8 }}>
                <span
                  style={{
                    display: "inline-block",
                    width: 12,
                    height: 12,
                    borderRadius: "50%",
                    background: st.colorCode || "#ccc",
                  }}
                  aria-hidden="true"
                />
                {st.name}
                <small style={{ color: "#999" }}>(вес {st.weight})</small>
              </span>
              <span className="admin-form-actions">
                <button type="button" className="admin-btn" onClick={() => startEdit(st)}>
                  Изменить
                </button>
                <button type="button" className="admin-btn danger" onClick={() => handleDelete(st)}>
                  Удалить
                </button>
              </span>
            </li>
          ))}
        </ul>
      )}

      {!showForm && (
        <button type="button" className="admin-btn" onClick={startCreate}>
          Добавить тип очков
        </button>
      )}

      {showForm && (
        <div className="admin-form-card" style={{ marginTop: 10 }}>
          <h4>{editingId ? "Редактировать тип очков" : "Новый тип очков"}</h4>

          <div className="admin-field">
            <label>Название</label>
            <input value={form.name} onChange={handleChange("name")} required />
          </div>

          <div className="admin-field">
            <label>Описание</label>
            <textarea value={form.description} onChange={handleChange("description")} rows={2} />
          </div>

          <div className="admin-field">
            <label>Вес (множитель очков)</label>
            <input type="number" step="0.1" value={form.weight} onChange={handleChange("weight")} />
          </div>

          <div className="admin-field">
            <label>Порядок отображения</label>
            <input type="number" min="1" value={form.displayOrder} onChange={handleChange("displayOrder")} />
          </div>

          <div className="admin-field">
            <label>Цвет</label>
            <input type="color" value={form.colorCode} onChange={handleChange("colorCode")} style={{ width: 60, height: 36, padding: 2 }} />
          </div>

          <div className="admin-form-actions">
            <button type="button" className="admin-btn" onClick={handleSubmit} disabled={saving}>
              {saving ? "Сохранение..." : "Сохранить"}
            </button>
            <button type="button" className="admin-btn secondary" onClick={cancelForm}>
              Отмена
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default function AdminGames() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [logoFile, setLogoFile] = useState(null);
  const [logoPreview, setLogoPreview] = useState(null);
  const [editingGame, setEditingGame] = useState(null);
  const [mediaBusy, setMediaBusy] = useState(false);
  const [pendingMedia, setPendingMedia] = useState({});
  const [uploadingType, setUploadingType] = useState(null);

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
    setLogoFile(null);
    setLogoPreview(null);
    setPendingMedia({});
    setShowForm(true);
  };

  const startEdit = (game) => {
    setEditingId(game.id);
    setEditingGame(game);
    setForm(toFormValues(game));
    setLogoFile(null);
    setLogoPreview(game.logoUrl || null);
    setPendingMedia({});
    setShowForm(true);
  };

  const cancelForm = () => {
    setShowForm(false);
    setEditingId(null);
    setEditingGame(null);
    setForm(emptyForm);
    setLogoFile(null);
    setLogoPreview(null);
    setPendingMedia({});
    setUploadingType(null);
  };

  const handleSelectPendingMedia = (type, file) => {
    setPendingMedia((prev) => ({ ...prev, [type]: file }));
  };

  const handleClearPendingMedia = (type) => {
    setPendingMedia((prev) => ({ ...prev, [type]: null }));
  };

  const handleMediaDelete = async (type, path) => {
    if (!window.confirm("Удалить файл?")) return;
    setMediaBusy(true);
    setError("");
    try {
      const updated = await adminApi.del(
        `/games/${editingId}/media?type=${type}&path=${encodeURIComponent(path)}`
      );
      setEditingGame(updated);
    } catch (err) {
      setError(err.message);
    } finally {
      setMediaBusy(false);
    }
  };

  const handleChange = (field) => (e) => {
    const value = e.target.type === "checkbox" ? e.target.checked : e.target.value;
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleLogoChange = (e) => {
    const file = e.target.files && e.target.files[0];
    setLogoFile(file || null);
    setLogoPreview(file ? URL.createObjectURL(file) : null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      const payload = toPayload(form);
      let gameId = editingId;

      if (editingId) {
        await adminApi.put(`/games/${editingId}`, payload);
      } else {
        const created = await adminApi.post("/games", payload);
        gameId = created.id;
      }

      if (logoFile && gameId) {
        const formData = new FormData();
        formData.append("file", logoFile);
        await adminApi.upload(`/games/${gameId}/logo`, formData);
      }

      if (gameId) {
        for (const { type } of MEDIA_TYPES) {
          const file = pendingMedia[type];
          if (!file) continue;
          setUploadingType(type);
          const formData = new FormData();
          formData.append("file", file);
          formData.append("type", type);
          await adminApi.upload(`/games/${gameId}/media`, formData);
        }
        setUploadingType(null);
      }

      cancelForm();
      await loadGames();
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
      setUploadingType(null);
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

          <div className="admin-field">
            <label>Логотип</label>
            {logoPreview && (
              <img src={logoPreview} alt="Логотип" className="admin-logo-preview" />
            )}
            <input type="file" accept="image/*" onChange={handleLogoChange} />
          </div>

          {editingId && (
            <div className="admin-field">
              <label>Типы очков</label>
              <small>Определяют строки таблицы результатов и их цвет на странице партии</small>
              <ScoreTypesManager gameId={editingId} />
            </div>
          )}

          {editingId && (
            <div className="admin-field">
              <label>Медиа-файлы</label>
              <small>Выберите файл — он загрузится в MinIO при нажатии «Сохранить»</small>
              {MEDIA_TYPES.map(({ type, label, pathsKey, urlsKey }) => (
                <MediaTypeSection
                  key={type}
                  label={label}
                  paths={editingGame?.media?.[pathsKey] || []}
                  urls={editingGame?.media?.[urlsKey] || []}
                  disabled={mediaBusy || saving}
                  uploading={uploadingType === type}
                  pendingFile={pendingMedia[type]}
                  onSelectFile={(file) => handleSelectPendingMedia(type, file)}
                  onClearPending={() => handleClearPendingMedia(type)}
                  onDelete={(path) => handleMediaDelete(type, path)}
                />
              ))}
            </div>
          )}

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
              {uploadingType ? "Загрузка файлов..." : saving ? "Сохранение..." : "Сохранить"}
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
              <th>Лого</th>
              <th>Название</th>
              <th>Жанр</th>
              <th>Игроков</th>
              <th>Возраст</th>
              <th>Цена</th>
              <th>Активна</th>
              <th>Сыграно</th>
              <th>Незавершено</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {games.map((game) => (
              <tr key={game.id}>
                <td>{game.id}</td>
                <td>
                  {game.logoUrl ? (
                    <img src={game.logoUrl} alt={game.name} className="admin-logo-thumb" />
                  ) : (
                    "-"
                  )}
                </td>
                <td>{game.name}</td>
                <td>{game.genre}</td>
                <td>
                  {game.minPlayers ?? "?"}–{game.maxPlayers ?? "?"}
                </td>
                <td>{game.ageRating ?? "-"}</td>
                <td>{game.price ?? "-"}</td>
                <td>{game.isActive ? "Да" : "Нет"}</td>
                <td>{game.completedSessionsCount ?? 0}</td>
                <td>{game.activeSessionsCount ?? 0}</td>
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
