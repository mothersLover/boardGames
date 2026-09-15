import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import Header from "./Header";
import "./Multimedia.css";

function fileNameOf(path) {
  if (!path) return "";
  return path.split("/").pop();
}

export default function Multimedia() {
  const { gameId } = useParams();
  const [game, setGame] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    setLoading(true);
    setError(false);
    fetch(`http://localhost:8080/api/games/${gameId}`)
      .then((r) => {
        if (!r.ok) throw new Error("Игра не найдена");
        return r.json();
      })
      .then(setGame)
      .catch(() => setError(true))
      .finally(() => setLoading(false));
  }, [gameId]);

  if (loading) {
    return (
      <div className="page">
        <div className="container multimedia-page">
          <p>Загрузка...</p>
        </div>
      </div>
    );
  }

  if (error || !game) {
    return (
      <div className="page">
        <div className="container multimedia-page">
          <h2>Игра не найдена</h2>
        </div>
      </div>
    );
  }

  const media = game.media || {};
  const audioUrls = media.audioUrls || [];
  const audioPaths = media.audioPaths || [];
  const videoUrls = media.videoUrls || [];
  const videoPaths = media.videoPaths || [];
  const instructionUrls = media.instructionUrls || [];
  const instructionPaths = media.instructionPaths || [];
  const otherUrls = media.otherUrls || [];
  const otherPaths = media.otherPaths || [];

  const hasAnyMedia =
    audioUrls.length > 0 || videoUrls.length > 0 || instructionUrls.length > 0 || otherUrls.length > 0;

  return (
    <>
      <Header logoSrc={game.logoUrl} logoAlt={game.name} />
      <div className="page">
        <div className="container multimedia-page">
          <h2>Мультимедиа: {game.name}</h2>

          {!hasAnyMedia && <p className="multimedia-empty">Пока нет загруженных файлов</p>}

          {audioUrls.length > 0 && (
            <section className="media-section">
              <h3>🎵 Музыка</h3>
              <div className="media-grid">
                {audioUrls.map((url, i) => (
                  <div className="media-card" key={url}>
                    <div className="media-name">{fileNameOf(audioPaths[i])}</div>
                    <audio controls src={url} />
                  </div>
                ))}
              </div>
            </section>
          )}

          {videoUrls.length > 0 && (
            <section className="media-section">
              <h3>🎬 Видео</h3>
              <div className="media-grid">
                {videoUrls.map((url, i) => (
                  <div className="media-card" key={url}>
                    <div className="media-name">{fileNameOf(videoPaths[i])}</div>
                    <video controls src={url} />
                  </div>
                ))}
              </div>
            </section>
          )}

          {instructionUrls.length > 0 && (
            <section className="media-section">
              <h3>📖 Инструкции</h3>
              <ul className="media-list">
                {instructionUrls.map((url, i) => (
                  <li key={url}>
                    <a href={url} target="_blank" rel="noreferrer">
                      📄 {fileNameOf(instructionPaths[i])}
                    </a>
                  </li>
                ))}
              </ul>
            </section>
          )}

          {otherUrls.length > 0 && (
            <section className="media-section">
              <h3>📁 Другое</h3>
              <ul className="media-list">
                {otherUrls.map((url, i) => (
                  <li key={url}>
                    <a href={url} target="_blank" rel="noreferrer">
                      📎 {fileNameOf(otherPaths[i])}
                    </a>
                  </li>
                ))}
              </ul>
            </section>
          )}
        </div>
      </div>
    </>
  );
}
