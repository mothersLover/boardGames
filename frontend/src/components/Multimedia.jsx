import { useState } from "react";

export default function Multimedia() {
  const [playing, setPlaying] = useState(false);

  return (
    <div className="page">
      <div className="container">
        <h2>🎵 Музыкальное сопровождение</h2>

        <audio id="player" src="/music/demo.mp3" />

        <button
          onClick={() => {
            const audio = document.getElementById("player");
            playing ? audio.pause() : audio.play();
            setPlaying(!playing);
          }}
          style={{ marginTop: 20 }}
        >
          {playing ? "⏸ Пауза" : "▶ Воспроизвести"}
        </button>
      </div>
    </div>
  );
}
