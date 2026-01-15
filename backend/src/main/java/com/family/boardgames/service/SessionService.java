
package com.family.boardgames.service;

import com.family.boardgames.model.*;
import com.family.boardgames.model.dto.PlayerDto;
import com.family.boardgames.model.dto.StartSessionDto;
import com.family.boardgames.repo.GameRepository;
import com.family.boardgames.repo.GameSessionRepository;
import com.family.boardgames.repo.PlayerRepository;
import com.family.boardgames.repo.ScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private final GameSessionRepository sessionRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final ScoreRepository scoreRepository;

    public GameSession startSession(GameSession session) {
        // Можно добавить бизнес-логику:
        // - Проверка существующей активной сессии
        // - Валидация игры
        // - Логирование
        return sessionRepository.save(session);
    }

    public GameSession startSession(StartSessionDto startSessionDto) {
        String gameId = startSessionDto.getGameId();
        Optional<Game> byNameContainingIgnoreCase = gameRepository.findByNameContainingIgnoreCase(gameId);
        Game game = byNameContainingIgnoreCase.orElseThrow(() -> new RuntimeException("Game with name " + gameId + " not found"));
        GameSession build = GameSession.builder().
                game(game).
                notes(startSessionDto.getComment()).
                startedAt(LocalDateTime.now()).build();

        GameSession gameSession = sessionRepository.save(build);
        List<Score> scores = new ArrayList<>();
        startSessionDto.getPlayers().forEach(player -> {
            Optional<Player> byId = playerRepository.findById(player.getPlayerId());
            if (byId.isPresent()) {
                Player player1 = byId.get();
                List<ScoreType> scoreTypes = game.getScoreTypes();
                for (ScoreType scoreType : scoreTypes) {
                    Score score = Score.builder().scoreType(scoreType).player(player1).session(gameSession).value(0D).build();
                    Score saved = scoreRepository.save(score);
                    scores.add(saved);
                }
            }
        });
        gameSession.setScores(scores);
        return sessionRepository.save(gameSession);
    }

    @Transactional(readOnly = true)
    public GameSession getSession(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<GameSession> getAllSessions() {
        return sessionRepository.findAll();
    }

    public GameSession endSession(Long id) {
        GameSession session = getSession(id);
        session.setEndedAt(LocalDateTime.now());
        session.setIsCompleted(true);
        return sessionRepository.save(session);
    }
}
