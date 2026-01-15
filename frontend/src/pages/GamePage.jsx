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
  const [players, setPlayers] = useState([{ name: "", playerId: null, isValid: false }]);
  const [playerErrors, setPlayerErrors] = useState({});
  const [comment, setComment] = useState("");
  const [suggestions, setSuggestions] = useState({});
  const [loadingPlayers, setLoadingPlayers] = useState({});
  const abortControllers = useRef({});

  // Проверка, можно ли активировать кнопку "Начать игру"
  const canStartGame = players.filter(p => p.isValid).length >= 2;

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

      if (playersData.length === 0) {
        // Если игрок не найден, не очищаем поле, но показываем сообщение
        setSuggestions(prev => ({
          ...prev,
          [index]: []
        }));
      } else {
        setSuggestions(prev => ({
          ...prev,
          [index]: playersData.slice(0, 5)
        }));
      }
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
    }, 300),
    [searchPlayers]
  );

  const addPlayer = () => {
    setPlayers([...players, { name: "", playerId: null, isValid: false }]);
  };

  const removePlayer = (index) => {
    if (players.length <= 1) return;

    // Отменяем запрос если есть
    if (abortControllers.current[index]) {
      abortControllers.current[index].abort();
      delete abortControllers.current[index];
    }

    const updated = players.filter((_, i) => i !== index);
    setPlayers(updated);

    // Очищаем подсказки для этого индекса
    setSuggestions(prev => {
      const newSuggestions = { ...prev };
      delete newSuggestions[index];
      return newSuggestions;
    });
  };

  const updatePlayer = (index, value) => {
    const updated = [...players];
    updated[index] = {
      name: value,
      playerId: null,
      isValid: false
    };
    setPlayers(updated);

    // Сбрасываем ошибку при вводе
    setPlayerErrors(prev => {
      const newErrors = { ...prev };
      delete newErrors[index];
      return newErrors;
    });

    // Если поле очистили, удаляем подсказки
    if (!value.trim()) {
      setSuggestions(prev => {
        const newSuggestions = { ...prev };
        delete newSuggestions[index];
        return newSuggestions;
      });
      return;
    }

    // Запускаем поиск с debounce
    debouncedSearch(index, value);
  };

  const selectSuggestion = (index, player) => {
    const updated = [...players];
    updated[index] = {
      name: player.displayName,
      playerId: player.id,
      isValid: true
    };
    setPlayers(updated);

    // Сбрасываем ошибку
    setPlayerErrors(prev => {
      const newErrors = { ...prev };
      delete newErrors[index];
      return newErrors;
    });

    // Убираем подсказки
    setSuggestions(prev => {
      const newSuggestions = { ...prev };
      delete newSuggestions[index];
      return newSuggestions;
    });
  };

  const handleInputBlur = (index) => {
    const player = players[index];

    // Если пользователь ввел что-то, но не выбрал из подсказок
    if (player.name.trim() && !player.playerId) {
      // Даем немного времени для обработки клика по подсказке
      setTimeout(() => {
        // Проверяем, есть ли подсказки
        if (suggestions[index] && suggestions[index].length === 0) {
          // Игрок не найден - очищаем поле
          const updated = [...players];
          updated[index] = { name: "", playerId: null, isValid: false };
          setPlayers(updated);

          // Показываем сообщение об ошибке
          setPlayerErrors(prev => ({
            ...prev,
            [index]: "Игрок не найден. Введите другое имя или выберите из списка."
          }));
        } else if (suggestions[index] && suggestions[index].length > 0) {
          // Есть подсказки, но не выбрали - показываем ошибку
          setPlayerErrors(prev => ({
            ...prev,
            [index]: "Выберите игрока из списка"
          }));
        }

        // Убираем подсказки
        setSuggestions(prev => {
          const newSuggestions = { ...prev };
          delete newSuggestions[index];
          return newSuggestions;
        });
      }, 300);
    } else {
      // Убираем подсказки через небольшой таймаут
      setTimeout(() => {
        setSuggestions(prev => {
          const newSuggestions = { ...prev };
          delete newSuggestions[index];
          return newSuggestions;
        });
      }, 200);
    }
  };

  const handleInputFocus = (index) => {
    const player = players[index];
    if (player.name.trim()) {
      searchPlayers(index, player.name);
    }
  };

  const validatePlayers = () => {
    const errors = {};
    let hasErrors = false;

    players.forEach((player, index) => {
      if (player.name.trim() && !player.playerId) {
        errors[index] = "Игрок не найден. Пожалуйста, выберите игрока из списка.";
        hasErrors = true;
      }
    });

    setPlayerErrors(errors);
    return !hasErrors;
  };

  const startGame = async () => {
    // Валидация игроков
    if (!validatePlayers()) {
      alert("Пожалуйста, исправьте ошибки в полях игроков");
      return;
    }

    // Проверка минимального количества игроков
    const validPlayers = players.filter(p => p.isValid);
    if (validPlayers.length < 2) {
      alert("Для начала игры необходимо минимум 2 игрока");
      return;
    }

    // Собираем данные валидных игроков
    const playerData = players
      .filter(p => p.isValid)
      .map(p => ({
        name: p.name,
        playerId: p.playerId
      }));

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
        mode: 'cors',
        credentials: 'include',
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
    };
  }, []);

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
                <div className="player-input-container">
                  <input
                    className={`player-input ${playerErrors[index] ? 'error' : ''} ${player.isValid ? 'valid' : ''}`}
                    type="text"
                    placeholder={`Имя игрока ${index + 1}`}
                    value={player.name}
                    onChange={(e) => updatePlayer(index, e.target.value)}
                    onFocus={() => handleInputFocus(index)}
                    onBlur={() => handleInputBlur(index)}
                  />
                  {players.length > 1 && (
                    <button
                      className="remove-player-btn"
                      onClick={() => removePlayer(index)}
                      type="button"
                    >
                      ×
                    </button>
                  )}
                </div>

                {playerErrors[index] && (
                  <div className="error-message">{playerErrors[index]}</div>
                )}

                {loadingPlayers[index] && (
                  <div className="suggestions-dropdown">
                    <div className="suggestion-item loading">
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
                          e.preventDefault();
                          selectSuggestion(index, suggestion);
                        }}
                      >
                        <div className="suggestion-content">
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

                {suggestions[index] && suggestions[index].length === 0 && !loadingPlayers[index] && player.name.trim() && (
                  <div className="suggestions-dropdown">
                    <div className="suggestion-item no-results">
                      <span>Игрок не найден. Введите другое имя.</span>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>

          <div className="add-player-container">
            <button
              className="add-player-button"
              onClick={addPlayer}
              type="button"
            >
              <span className="plus">+</span>
              <span>Добавить ещё игрока</span>
            </button>
          </div>

          <div className="comment-section">
            <label htmlFor="comment" className="comment-label">
              Комментарий к партии (необязательно)
            </label>
            <textarea
              id="comment"
              className="comment-input"
              placeholder="Например: 'Играли с новыми правилами' и хохотали каждые 30 секунд"
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              rows={3}
            />
          </div>

          <div className="start-button-container">
            <button
              onClick={startGame}
              disabled={!canStartGame}
              className={`start-game-button ${canStartGame ? 'enabled' : 'disabled'}`}
            >
              Начать игру
              {!canStartGame && (
                <span className="button-hint">
                  (минимум 2 игрока)
                </span>
              )}
            </button>
          </div>
        </div>
      </div>
    </>
  );
}