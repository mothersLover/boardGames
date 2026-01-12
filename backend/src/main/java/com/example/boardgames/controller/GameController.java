
package com.example.boardgames.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.boardgames.model.Game;
import com.example.boardgames.service.GameService;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @GetMapping
    public List<Game> all() {
        return service.all();
    }

    @PostMapping
    public Game create(@RequestBody Game g) {
        return service.create(g);
    }
}
