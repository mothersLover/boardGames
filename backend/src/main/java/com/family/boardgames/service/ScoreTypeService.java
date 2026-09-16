package com.family.boardgames.service;

import com.family.boardgames.model.Game;
import com.family.boardgames.model.ScoreType;
import com.family.boardgames.repo.GameRepository;
import com.family.boardgames.repo.ScoreTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScoreTypeService {

    private final ScoreTypeRepository scoreTypeRepository;
    private final GameRepository gameRepository;

    @Transactional(readOnly = true)
    public List<ScoreType> getByGame(Long gameId) {
        Game game = getGame(gameId);
        return game.getScoreTypes().stream()
                .sorted(Comparator.comparing(ScoreType::getDisplayOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    public ScoreType create(Long gameId, ScoreType incoming) {
        Game game = getGame(gameId);
        incoming.setId(null);
        incoming.setGame(game);
        return scoreTypeRepository.save(incoming);
    }

    public ScoreType update(Long gameId, Long scoreTypeId, ScoreType incoming) {
        ScoreType existing = getOwned(gameId, scoreTypeId);
        existing.setName(incoming.getName());
        existing.setDescription(incoming.getDescription());
        existing.setWeight(incoming.getWeight());
        existing.setDisplayOrder(incoming.getDisplayOrder());
        existing.setColorCode(incoming.getColorCode());
        return scoreTypeRepository.save(existing);
    }

    public void delete(Long gameId, Long scoreTypeId) {
        ScoreType existing = getOwned(gameId, scoreTypeId);
        scoreTypeRepository.delete(existing);
    }

    private Game getGame(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));
    }

    private ScoreType getOwned(Long gameId, Long scoreTypeId) {
        ScoreType scoreType = scoreTypeRepository.findById(scoreTypeId)
                .orElseThrow(() -> new RuntimeException("Score type not found with id: " + scoreTypeId));
        if (scoreType.getGame() == null || !scoreType.getGame().getId().equals(gameId)) {
            throw new RuntimeException("Score type " + scoreTypeId + " does not belong to game " + gameId);
        }
        return scoreType;
    }
}
