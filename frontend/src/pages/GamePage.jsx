
import { useParams, Link } from "react-router-dom";
import React, { useState } from 'react'; 
import './GamePage.css';
import logo7Wonders from '../resources/7.png';
import logoChampions from '../resources/champions.png';
import logoCythe from '../resources/scythe.png';
import Header from "../components/Header";

const games = {
  "scythe" : { title: "Scythe", description: "Альтернативные 1920-е", logo: logoCythe },
  "7wonders" : { title: "7 Wonders", description: "Строй цивилизацию", logo: logo7Wonders },
  "champions" : { title: "Champions of Midgard", description: "Викинги!", logo: logoChampions }
};

export default function GamePage() {
  const { gameId } = useParams();
  const game = games[gameId];
  const [players, setPlayers] = useState([""]);
  const [comment, setComment] = useState("");

  const addPlayer = () => {
    setPlayers([...players, ""]);
  };

  const updatePlayer = (index, value) => {
    const updated = [...players];
    updated[index] = value;
    setPlayers(updated);
  };

   const startGame = async () => {
    const payload = {
      gameId,
      players: players
        .filter((p) => p.trim() !== "")
        .map((p) => ({ name: p })),
      startedAt: new Date().toISOString(),
      comment
    };

    const response = await fetch("http://localhost:8080/api/sessions/start", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        // Authorization: Bearer ${token} ← когда подключишь Keycloak
      },
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      alert("Ошибка при старте игры");
      return;
    }

    const session = await response.json();
    console.log("Session started:", session);

    // TODO: переход на экран партии
  };

  if (!game) return <h2>Игра не найдена</h2>;

  return (
    <>
      <Header 
        logoSrc={game.logo}
        logoAlt="Логотип сайта"
      />
<div className="page">
      <div className="container">
        <div className="players">
          {players.map((player, index) => (
            <input
              key={index}
              className="player-input"
              type="text"
              placeholder={`Имя игрока ${index + 1}`} 
              value={player}
              onChange={(e) => updatePlayer(index, e.target.value)}
            />
          ))}
        </div>

        <textarea
          className="player-input"
          placeholder="Комментарий к партии"
          value={comment}
          onChange={(e) => setComment(e.target.value)}
          style={{ marginTop: 16, resize: "none" }}
        />

        <div className="add-player" onClick={addPlayer}>
          <div className="plus">+</div>
          <div>Добавить ещё игрока</div>
        </div>

        <button
          onClick={startGame}
          style={{
            marginTop: 24,
            width: "100%",
            padding: "14px",
            borderRadius: "12px",
            border: "none",
            fontSize: "16px",
            cursor: "pointer"
          }}
        >
          ▶️ Начать игру
        </button>
      </div>
    </div>
    </>
  );
}
