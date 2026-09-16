package com.family.boardgames.controller.admin;

import com.family.boardgames.model.ScoreType;
import com.family.boardgames.service.ScoreTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/games/{gameId}/score-types")
@RequiredArgsConstructor
public class AdminScoreTypeController {

    private final ScoreTypeService scoreTypeService;

    @GetMapping
    public List<ScoreType> all(@PathVariable Long gameId) {
        return scoreTypeService.getByGame(gameId);
    }

    @PostMapping
    public ResponseEntity<ScoreType> create(@PathVariable Long gameId, @RequestBody ScoreType scoreType) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scoreTypeService.create(gameId, scoreType));
    }

    @PutMapping("/{id}")
    public ScoreType update(@PathVariable Long gameId, @PathVariable Long id, @RequestBody ScoreType scoreType) {
        return scoreTypeService.update(gameId, id, scoreType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long gameId, @PathVariable Long id) {
        scoreTypeService.delete(gameId, id);
        return ResponseEntity.noContent().build();
    }
}
