
package com.example.boardgames.controller;

import org.springframework.web.bind.annotation.*;
import com.example.boardgames.model.GameSession;
import com.example.boardgames.service.SessionService;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @PostMapping("/start/{gameId}")
    public GameSession start(@PathVariable Long gameId) {
        return service.start(gameId);
    }
}
