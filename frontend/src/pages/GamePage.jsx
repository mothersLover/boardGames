import React, { useState, useEffect, useRef, useCallback } from 'react';
import debounce from 'lodash.debounce'; // или напишем свою debounce
import { useParams, Link } from "react-router-dom";
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
  const [suggestions, setSuggestions] = useState({});
  const [loadingPlayers, setLoadingPlayers] = useState({});
  const [searchTimeout, setSearchTimeout] = useState(null);
  const abortControllers = useRef({});

  // Функция для поиска игроков на сервере
  const searchPlayers = useCallback(async (index, query) => {
    if (!query.trim()) {
      setSuggestions(prev => {
        const newSuggestions = { ...prev };
        delete newSuggestions[index];
        return newSuggestions;
      });
      setLoadingPlayers(prev => ({ ...prev, [index]: false }));
      return;
    }

    // Отменяем предыдущий запрос для этого инпута
    if (abortControllers.current[index]) {
      abortControllers.current[index].abort();
    }

    // Создаем новый AbortController
    abortControllers.current[index] = new AbortController();

    setLoadingPlayers(prev => ({ ...prev, [index]: true }));

    try {
      const response = await fetch(
        `http://localhost:8080/api/players/search?query=${encodeURIComponent(query)}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
          mode: 'cors',
          credentials: 'include',
          signal: abortControllers.current[index].signal
        }
      );

      if (!response.ok) {
        throw new Error('Failed to search players');
      }

      const playersData = await response.json();
      
      setSuggestions(prev => ({
        ...prev,
        [index]: playersData.slice(0, 5) // Ограничиваем 5 подсказками
      }));
    } catch (error) {
      if (error.name !== 'AbortError') {
        console.error("Error searching players:", error);
        setSuggestions(prev => {
          const newSuggestions = { ...prev };
          delete newSuggestions[index];
          return newSuggestions;
        });
      }
    } finally {
      setLoadingPlayers(prev => ({ ...prev, [index]: false }));
    }
  }, []);

  // Оптимизированная функция с debounce
  const debouncedSearch = useCallback(
    debounce((index, value) => {
      searchPlayers(index, value);
    }, 300), // Задержка 300ms
    []
  );

  const addPlayer = () => {
    setPlayers([...players, ""]);
  };

  const updatePlayer = (index, value) => {
    const updated = [...players];
    updated[index] = value;
    setPlayers(updated);

    // Запускаем поиск с debounce
    debouncedSearch(index, value);
  };

  const selectSuggestion = (index, player) => {
    const updated = [...players];
    updated[index] = player.displayName;
    setPlayers(updated);
    
    // Сохраняем playerId в data-атрибут или отдельное состояние
    const input = document.querySelector(`input[data-index="${index}"]`);
    if (input) {
      input.dataset.playerId = player.id;
    }
    
    // Убираем подсказки
    setSuggestions(prev => {
      const newSuggestions = { ...prev };
      delete newSuggestions[index];
      return newSuggestions;
    });
  };

  const handleInputBlur = (index) => {
    // Даем время на клик по подсказке
    setTimeout(() => {
      setSuggestions(prev => {
        const newSuggestions = { ...prev };
        delete newSuggestions[index];
        return newSuggestions;
      });
    }, 200);
  };

  const handleInputFocus = (index, value) => {
    if (value.trim()) {
      searchPlayers(index, value);
    }
  };

  const startGame = async () => {
    // Собираем данные игроков с ID
    const playerInputs = document.querySelectorAll('.player-input[data-index]');
    const playerData = [];
    
    playerInputs.forEach((input, index) => {
      const value = input.value.trim();
      if (value) {
        playerData.push({
          name: value,
          playerId: input.dataset.playerId || null
        });
      }
    });

    const payload = {
      gameId,
      players: playerData,
      startedAt: new Date().toISOString(),
      comment
    };

    try {
      const response = await fetch("http://localhost:8080/api/sessions/start", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Ошибка при старте игры");
      }

      const session = await response.json();
      console.log("Session started:", session);
      
      // TODO: переход на экран партии
      // navigate(`/session/${session.id}`);
      
    } catch (error) {
      alert(error.message || "Ошибка при старте игры");
    }
  };

  // Очистка при размонтировании
  useEffect(() => {
    return () => {
      // Отменяем все pending запросы
      Object.values(abortControllers.current).forEach(controller => {
        controller.abort();
      });
      // Очищаем таймеры
      if (searchTimeout) clearTimeout(searchTimeout);
    };
  }, [searchTimeout]);

  if (!game) return <h2>Игра не найдена</h2>;

  return (
    <>
      <Header 
        logoSrc={game.logo}
        logoAlt="Логотип сайта"
      />
      <div className="page">
        <div className="container">
          <h2 style={{ marginBottom: 24 }}>Начать новую партию в {game.name}</h2>
          
          <div className="players">
            {players.map((player, index) => (
              <div key={index} className="player-input-wrapper">
                <input
                  className="player-input"
                  type="text"
                  placeholder={`Имя игрока ${index + 1}`} 
                  value={player}
                  data-index={index}
                  onChange={(e) => updatePlayer(index, e.target.value)}
                  onFocus={() => handleInputFocus(index, player)}
                  onBlur={() => handleInputBlur(index)}
                />
                
                {loadingPlayers[index] && (
                  <div className="suggestions-dropdown">
                    <div className="suggestion-item">
                      <div className="loading-indicator">Поиск...</div>
                    </div>
                  </div>
                )}
                
                {suggestions[index] && suggestions[index].length > 0 && (
                  <div className="suggestions-dropdown">
                    {suggestions[index].map((suggestion, idx) => (
                      <div
                        key={idx}
                        className="suggestion-item"
                        onMouseDown={(e) => {
                          e.preventDefault(); // Предотвращаем потерю фокуса
                          selectSuggestion(index, suggestion);
                        }}
                      >
                        <div style={{ display: 'flex', flexDirection: 'column' }}>
                          <span className="suggestion-display">
                            {suggestion.displayName}
                          </span>
                          <span className="suggestion-username">
                            {suggestion.userName}
                            {suggestion.rating && ` • Рейтинг: ${suggestion.rating}`}
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
                
                {suggestions[index] && suggestions[index].length === 0 && !loadingPlayers[index] && player.trim() && (
                  <div className="suggestions-dropdown">
                    <div className="suggestion-item">
                      <span style={{ color: '#666' }}>
                        Игрок не найден. Будет создан новый.
                      </span>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>

          <textarea
            className="comment-input"
            placeholder="Комментарий к партии (необязательно)"
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            rows={3}
          />

          <div className="add-player" onClick={addPlayer}>
            <div className="plus">+</div>
            <div>Добавить ещё игрока</div>
          </div>

          <button
            onClick={startGame}
            disabled={players.every(p => !p.trim())}
            className="start-game-button"
          >
            Начать игру
          </button>
        </div>
      </div>
    </>
  );
}

// Стили
const styles = `
  .player-input-wrapper {
    position: relative;
    margin-bottom: 12px;
  }

  .player-input, .comment-input {
    width: 100%;
    padding: 12px 16px;
    border: 2px solid #e1e5e9;
    border-radius: 10px;
    font-size: 16px;
    box-sizing: border-box;
    transition: border-color 0.3s;
    font-family: inherit;
  }

  .player-input:focus, .comment-input:focus {
    outline: none;
    border-color: #4a90e2;
    box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.1);
  }

  .suggestions-dropdown {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: white;
    border: 2px solid #e1e5e9;
    border-top: none;
    border-radius: 0 0 10px 10px;
    box-shadow: 0 6px 20px rgba(0,0,0,0.1);
    z-index: 1000;
    max-height: 250px;
    overflow-y: auto;
  }

  .suggestion-item {
    padding: 12px 16px;
    cursor: pointer;
    border-bottom: 1px solid #f0f0f0;
    transition: background-color 0.2s;
  }

  .suggestion-item:hover {
    background-color: #f8fafc;
  }

  .suggestion-item:last-child {
    border-bottom: none;
  }

  .suggestion-display {
    font-weight: 600;
    color: #2d3748;
    margin-bottom: 4px;
  }

  .suggestion-username {
    color: #718096;
    font-size: 14px;
  }

  .add-player {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 16px;
    background-color: #f8fafc;
    border: 2px dashed #cbd5e0;
    border-radius: 10px;
    cursor: pointer;
    margin-top: 20px;
    transition: all 0.3s;
    color: #4a5568;
  }

  .add-player:hover {
    background-color: #edf2f7;
    border-color: #a0aec0;
  }

  .plus {
    width: 28px;
    height: 28px;
    background-color: #4a90e2;
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
    font-weight: 300;
  }

  .start-game-button {
    margin-top: 28px;
    width: 100%;
    padding: 16px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 12px;
    font-size: 18px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s;
    letter-spacing: 0.5px;
  }

  .start-game-button:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  }

  .start-game-button:disabled {
    background: #cbd5e0;
    cursor: not-allowed;
    transform: none;
  }

  .loading-indicator {
    padding: 8px;
    text-align: center;
    color: #718096;
    font-size: 14px;
  }

  .comment-input {
    margin-top: 20px;
    resize: vertical;
    min-height: 80px;
  }
`;

// Добавляем стили
if (!document.getElementById('game-page-styles')) {
  const styleSheet = document.createElement('style');
  styleSheet.id = 'game-page-styles';
  styleSheet.textContent = styles;
  document.head.appendChild(styleSheet);
}