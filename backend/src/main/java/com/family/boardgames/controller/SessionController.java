
package com.family.boardgames.controller;

import com.family.boardgames.model.GameSession;
import com.family.boardgames.model.dto.SaveResultsDto;
import com.family.boardgames.model.dto.SessionDto;
import com.family.boardgames.model.dto.SessionSummaryDto;
import com.family.boardgames.model.dto.StartSessionDto;
import com.family.boardgames.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/start")
    public ResponseEntity<SessionDto> startSession(
            @Valid @RequestBody StartSessionDto startSessionDto) {

        log.info("Starting new session for game: {}", startSessionDto.getGameId());

        GameSession savedSession = sessionService.startSession(startSessionDto);
        SessionDto responseDto = sessionService.getSessionDetail(savedSession.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSession(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSessionDetail(id));
    }

    @GetMapping("/active")
    public List<SessionSummaryDto> getActiveSessions(@RequestParam Long gameId) {
        return sessionService.getActiveSessions(gameId);
    }

    @PutMapping("/{id}/results")
    public ResponseEntity<SessionDto> saveResults(
            @PathVariable Long id,
            @Valid @RequestBody SaveResultsDto dto) {
        return ResponseEntity.ok(sessionService.saveResults(id, dto));
    }
}
