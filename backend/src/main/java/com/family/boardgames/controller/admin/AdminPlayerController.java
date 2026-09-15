package com.family.boardgames.controller.admin;

import com.family.boardgames.model.dto.PlayerDto;
import com.family.boardgames.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/players")
@RequiredArgsConstructor
public class AdminPlayerController {

    private final PlayerService playerService;

    @GetMapping
    public List<PlayerDto> all() {
        return playerService.getAllPlayers(0, 1000);
    }

    @GetMapping("/{id}")
    public PlayerDto getById(@PathVariable Long id) {
        return playerService.getPlayerById(id);
    }

    @PostMapping
    public ResponseEntity<PlayerDto> create(@RequestBody PlayerDto player) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.createPlayer(player));
    }

    @PutMapping("/{id}")
    public PlayerDto update(@PathVariable Long id, @RequestBody PlayerDto player) {
        return playerService.updatePlayer(id, player);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}
