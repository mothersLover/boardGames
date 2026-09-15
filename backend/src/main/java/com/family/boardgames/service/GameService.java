
package com.family.boardgames.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.family.boardgames.repo.GameRepository;
import com.family.boardgames.model.Game;

@Service
@Transactional
public class GameService {
    private final GameRepository repo;

    public GameService(GameRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Game> all() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Game getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));
    }

    public Game create(Game g) {
        return repo.save(g);
    }

    public Game update(Long id, Game incoming) {
        Game existing = getById(id);
        // Копируем только скалярные поля: sessions/scoreTypes/tags/media используют
        // cascade=ALL (+orphanRemoval для sessions/scoreTypes) и не должны затираться
        // значениями из формы админки, которая их вообще не редактирует.
        existing.setName(incoming.getName());
        existing.setDescription(incoming.getDescription());
        existing.setGenre(incoming.getGenre());
        existing.setMinPlayers(incoming.getMinPlayers());
        existing.setMaxPlayers(incoming.getMaxPlayers());
        existing.setAgeRating(incoming.getAgeRating());
        existing.setPrice(incoming.getPrice());
        existing.setIsActive(incoming.getIsActive());
        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Game not found with id: " + id);
        }
        repo.deleteById(id);
    }
}
