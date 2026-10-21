package com.family.boardgames.controller.admin;

import com.family.boardgames.model.dto.SessionHistoryPageDto;
import com.family.boardgames.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/games/{gameId}/sessions")
@RequiredArgsConstructor
public class AdminSessionController {

    private final SessionService sessionService;

    @GetMapping
    public SessionHistoryPageDto list(
            @PathVariable Long gameId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return sessionService.getAdminSessionHistory(gameId, page, size);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> delete(@PathVariable Long gameId, @PathVariable Long sessionId) {
        sessionService.deleteSession(gameId, sessionId);
        return ResponseEntity.noContent().build();
    }
}
