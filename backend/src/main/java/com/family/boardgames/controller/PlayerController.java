package com.family.boardgames.controller;

import com.family.boardgames.model.dto.PlayerDto;
import com.family.boardgames.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class PlayerController {
    
    private final PlayerService playerService;
    
    @GetMapping("/search")
    public ResponseEntity<List<PlayerDto>> searchPlayers(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "10") int limit) {
        
        log.debug("Searching players with query: '{}', limit: {}", query, limit);
        
        List<PlayerDto> players;
        if (query == null || query.trim().isEmpty()) {
            players = playerService.getRecentPlayers(limit);
        } else {
            players = playerService.searchPlayers(query.trim(), limit);
        }
        
        return ResponseEntity.ok(players);
    }
    
    @GetMapping
    public ResponseEntity<List<PlayerDto>> getAllPlayers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        List<PlayerDto> players = playerService.getAllPlayers(page, size);
        return ResponseEntity.ok(players);
    }
}