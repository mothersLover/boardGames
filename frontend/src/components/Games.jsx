
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import logoPlaceholder from '../resources/logo.jpg';

export default function Games() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/api/games")
      .then((r) => {
        if (!r.ok) throw new Error("Не удалось загрузить список игр");
        return r.json();
      })
      .then(setGames)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <section className="games"><p>Загрузка...</p></section>;
  if (error) return <section className="games"><p>{error}</p></section>;
  if (games.length === 0) return <section className="games"><p>Игры пока не добавлены</p></section>;

  return (
    <section className="games">
      {games.map((game, index) => (
        <Link
          to={`/game/${game.id}`}
          className={`game ${index === games.length - 1 && games.length % 2 !== 0 ? "large" : "small"}`}
          key={game.id}
        >
          <img src={game.logoUrl || logoPlaceholder} alt={game.name} />
          <span className="game-title">{game.name}</span>
        </Link>
      ))}
    </section>
  );
}
