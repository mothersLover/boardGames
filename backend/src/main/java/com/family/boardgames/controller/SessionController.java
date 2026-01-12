
package com.family.boardgames.controller;

import com.family.boardgames.mapper.SessionMapper;
import com.family.boardgames.model.dto.SessionDto;
import com.family.boardgames.model.dto.StartSessionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.family.boardgames.model.GameSession;
import com.family.boardgames.service.SessionService;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000") // Для React dev сервера
public class SessionController {

    private final SessionService sessionService;
    private final SessionMapper sessionMapper;

    @PostMapping("/start")
    public ResponseEntity<SessionDto> startSession(
            @Valid @RequestBody StartSessionDto startSessionDto) {

        log.info("Starting new session for game: {}", startSessionDto.getGameId());

        GameSession savedSession = sessionService.startSession(startSessionDto);
        SessionDto responseDto = sessionMapper.toDto(savedSession);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSession(@PathVariable Long id) {
        GameSession session = sessionService.getSession(id);
        SessionDto dto = sessionMapper.toDto(session);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<SessionDto>> getAllSessions() {
        List<GameSession> sessions = sessionService.getAllSessions();
        List<SessionDto> dtos = sessions.stream()
                .map(sessionMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<SessionDto> endSession(@PathVariable Long id) {
        GameSession session = sessionService.endSession(id);
        SessionDto dto = sessionMapper.toDto(session);
        return ResponseEntity.ok(dto);
    }
}