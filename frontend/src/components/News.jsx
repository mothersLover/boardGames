
import { useEffect, useState } from "react";

export default function News() {
  const [news, setNews] = useState([]);

  useEffect(() => {
    fetch("/api/news")
      .then(r => r.json())
      .then(setNews);
  }, []);

  return (
    <section className="news" id="news">
      <div className="news-item">
          <h3>ASFAS asfasfasf asf asfasf </h3>
          <p>ASFAS asfasfasf asf asfasf  ASFAS asfasfasf asf asfasf </p>
        </div>
      {news.map((n, i) => (
        <div className="news-item" key={i}>
          <h3>{n.title}</h3>
          <p>{n.text}</p>
        </div>
      ))}
    </section>
  );
}
