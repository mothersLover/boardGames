
package com.example.boardgames.service;

import org.springframework.stereotype.Service;

import java.util.List;

import com.example.boardgames.repo.GameRepository;
import com.example.boardgames.model.Game;

@Service
public class GameService {
    private final GameRepository repo;

    public GameService(GameRepository repo) {
        this.repo = repo;
    }

    public List<Game> all() {
        return repo.findAll();
    }

    public Game create(Game g) {
        return repo.save(g);
    }
}
