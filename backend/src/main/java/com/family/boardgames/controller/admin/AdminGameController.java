package com.family.boardgames.controller.admin;

import com.family.boardgames.model.Game;
import com.family.boardgames.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/games")
@RequiredArgsConstructor
public class AdminGameController {

    private final GameService gameService;

    @GetMapping
    public List<Game> all() {
        return gameService.all();
    }

    @GetMapping("/{id}")
    public Game getById(@PathVariable Long id) {
        return gameService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Game> create(@RequestBody Game game) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.create(game));
    }

    @PutMapping("/{id}")
    public Game update(@PathVariable Long id, @RequestBody Game game) {
        return gameService.update(id, game);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gameService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
