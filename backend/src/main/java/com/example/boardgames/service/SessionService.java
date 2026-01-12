
package com.example.boardgames.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

import com.example.boardgames.model.*;
import com.example.boardgames.repo.*;

@Service
public class SessionService {
    private final GameSessionRepository repo;
    private final GameRepository gameRepo;

    public SessionService(GameSessionRepository repo, GameRepository gameRepo) {
        this.repo = repo;
        this.gameRepo = gameRepo;
    }

    public GameSession start(Long gameId) {
        GameSession s = new GameSession();
        Game game = gameRepo.findById(gameId).orElseThrow();
        s.setGame(game);
//        s.status = "ACTIVE";
        s.setStartedAt(LocalDateTime.now());
        return repo.save(s);
    }
}
