import { useEffect, useState } from "react";
import { adminApi } from "./adminApi";
import "./Admin.css";

function toDatetimeLocal(iso) {
  if (!iso) return "";
  // iso приходит как "2026-09-15T12:34:56", input[type=datetime-local] ждёт "2026-09-15T12:34"
  return iso.slice(0, 16);
}

const emptyForm = {
  title: "",
  content: "",
  publishedAt: "",
};

function toFormValues(item) {
  return {
    title: item.title || "",
    content: item.content || "",
    publishedAt: toDatetimeLocal(item.publishedAt),
  };
}

function toPayload(form) {
  return {
    title: form.title,
    content: form.content,
    publishedAt: form.publishedAt ? form.publishedAt : null,
  };
}

export default function AdminNews() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);

  const loadNews = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await adminApi.get("/news");
      setItems(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadNews();
  }, []);

  const startCreate = () => {
    setEditingId(null);
    setForm(emptyForm);
    setShowForm(true);
  };

  const startEdit = (item) => {
    setEditingId(item.id);
    setForm(toFormValues(item));
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
        await adminApi.put(`/news/${editingId}`, payload);
      } else {
        await adminApi.post("/news", payload);
      }
      cancelForm();
      await loadNews();
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (item) => {
    if (!window.confirm(`Удалить новость "${item.title}"?`)) return;
    setError("");
    try {
      await adminApi.del(`/news/${item.id}`);
      await loadNews();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h2>Новости</h2>

      {error && <div className="admin-error">{error}</div>}

      <div className="admin-toolbar">
        {!showForm && (
          <button className="admin-btn" onClick={startCreate}>
            Добавить новость
          </button>
        )}
      </div>

      {showForm && (
        <form className="admin-form-card" onSubmit={handleSubmit}>
          <h3>{editingId ? "Редактировать новость" : "Новая новость"}</h3>

          <div className="admin-field">
            <label>Заголовок</label>
            <input value={form.title} onChange={handleChange("title")} required maxLength={200} />
          </div>

          <div className="admin-field">
            <label>Текст</label>
            <textarea value={form.content} onChange={handleChange("content")} rows={6} required maxLength={5000} />
          </div>

          <div className="admin-field">
            <label>Дата публикации</label>
            <input
              type="datetime-local"
              value={form.publishedAt}
              onChange={handleChange("publishedAt")}
            />
            <small>Оставьте пустым, чтобы использовать текущее время</small>
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
              <th>Дата</th>
              <th>Заголовок</th>
              <th>Текст</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id}>
                <td>{item.id}</td>
                <td>{item.publishedAt ? item.publishedAt.slice(0, 10) : "-"}</td>
                <td>{item.title}</td>
                <td>{(item.content || "").slice(0, 80)}{item.content && item.content.length > 80 ? "…" : ""}</td>
                <td className="actions">
                  <button className="admin-btn" onClick={() => startEdit(item)}>
                    Изменить
                  </button>
                  <button className="admin-btn danger" onClick={() => handleDelete(item)}>
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
