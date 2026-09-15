import { useEffect, useState } from "react";
import "./News.css";

const PAGE_SIZE = 5;
const TRUNCATE_AT = 220;

function formatDate(iso) {
  if (!iso) return "";
  const date = new Date(iso);
  return date.toLocaleDateString("ru-RU", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
}

export default function News() {
  const [news, setNews] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [expanded, setExpanded] = useState({});

  useEffect(() => {
    setLoading(true);
    setError("");
    fetch(`http://localhost:8080/api/news?page=${page}&size=${PAGE_SIZE}`)
      .then((r) => {
        if (!r.ok) throw new Error("Не удалось загрузить новости");
        return r.json();
      })
      .then((data) => {
        setNews(data.content || []);
        setTotalPages(data.totalPages || 0);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [page]);

  const toggleExpanded = (id) => {
    setExpanded((prev) => ({ ...prev, [id]: !prev[id] }));
  };

  return (
    <section className="news" id="news">
      <h2 className="news-title">Новости</h2>

      {error && <p className="news-empty">{error}</p>}
      {!error && loading && <p className="news-empty">Загрузка...</p>}
      {!error && !loading && news.length === 0 && (
        <p className="news-empty">Пока новостей нет</p>
      )}

      {news.map((item) => {
        const isLong = item.content && item.content.length > TRUNCATE_AT;
        const isExpanded = !!expanded[item.id];
        const displayText =
          isLong && !isExpanded
            ? `${item.content.slice(0, TRUNCATE_AT).trim()}…`
            : item.content;

        return (
          <div className="news-item" key={item.id}>
            <span className="news-date">{formatDate(item.publishedAt)}</span>
            <h3>{item.title}</h3>
            <p>{displayText}</p>
            {isLong && (
              <button className="news-toggle" onClick={() => toggleExpanded(item.id)}>
                {isExpanded ? "Свернуть" : "Читать далее"}
              </button>
            )}
          </div>
        );
      })}

      {totalPages > 1 && (
        <div className="news-pagination">
          <button
            className="news-page-btn"
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
          >
            ←
          </button>

          {Array.from({ length: totalPages }, (_, i) => i).map((i) => (
            <button
              key={i}
              className={`news-page-btn ${i === page ? "active" : ""}`}
              onClick={() => setPage(i)}
            >
              {i + 1}
            </button>
          ))}

          <button
            className="news-page-btn"
            onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
            disabled={page >= totalPages - 1}
          >
            →
          </button>
        </div>
      )}
    </section>
  );
}
