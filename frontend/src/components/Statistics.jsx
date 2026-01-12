import { useEffect, useState } from "react";

export default function Statistics() {
  const [stats, setStats] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/statistics")
      .then((res) => res.json())
      .then(setStats);
  }, []);

  return (
    <div className="page">
      <div className="container">
        <h2>📊 Статистика</h2>

        {stats.length === 0 && <p>Нет данных</p>}

        {stats.map((s) => (
          <div key={s.player}>
            {s.player}: {s.totalScore}
          </div>
        ))}
      </div>
    </div>
  );
}