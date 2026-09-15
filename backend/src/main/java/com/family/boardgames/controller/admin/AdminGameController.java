package com.family.boardgames.controller.admin;

import com.family.boardgames.model.Game;
import com.family.boardgames.model.dto.FileUploadDto;
import com.family.boardgames.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/{id}/logo")
    public Game uploadLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return gameService.uploadLogo(id, file);
    }

    @PostMapping("/{id}/media")
    public Game uploadMedia(@PathVariable Long id,
                             @RequestParam("file") MultipartFile file,
                             @RequestParam("type") FileUploadDto.FileType type) {
        return gameService.uploadMedia(id, file, type);
    }

    @DeleteMapping("/{id}/media")
    public Game deleteMedia(@PathVariable Long id,
                             @RequestParam("type") FileUploadDto.FileType type,
                             @RequestParam("path") String path) {
        return gameService.deleteMediaItem(id, type, path);
    }
}
