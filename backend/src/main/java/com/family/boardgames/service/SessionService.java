
package com.family.boardgames.service;

import com.family.boardgames.model.*;
import com.family.boardgames.model.dto.*;
import com.family.boardgames.repo.GameRepository;
import com.family.boardgames.repo.GameSessionRepository;
import com.family.boardgames.repo.PlayerRepository;
import com.family.boardgames.repo.ScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private final GameSessionRepository sessionRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final ScoreRepository scoreRepository;
    private final MinioService minioService;

    public GameSession startSession(StartSessionDto startSessionDto) {
        String gameId = startSessionDto.getGameId();
        Long id;
        try {
            id = Long.valueOf(gameId);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid game id: " + gameId);
        }
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));
        GameSession build = GameSession.builder().
                game(game).
                notes(startSessionDto.getComment()).
                startedAt(LocalDateTime.now()).build();

        GameSession gameSession = sessionRepository.save(build);
        List<Score> scores = new ArrayList<>();
        startSessionDto.getPlayers().forEach(player -> {
            Optional<Player> byId = playerRepository.findById(player.getPlayerId());
            if (byId.isPresent()) {
                Player player1 = byId.get();
                List<ScoreType> scoreTypes = game.getScoreTypes();
                for (ScoreType scoreType : scoreTypes) {
                    Score score = Score.builder().scoreType(scoreType).player(player1).session(gameSession).value(0D).build();
                    Score saved = scoreRepository.save(score);
                    scores.add(saved);
                }
            }
        });
        gameSession.setScores(scores);
        return sessionRepository.save(gameSession);
    }

    @Transactional(readOnly = true)
    public GameSession getSession(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public SessionDto getSessionDetail(Long id) {
        return toDto(getSession(id));
    }

    @Transactional(readOnly = true)
    public List<GameSession> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<SessionSummaryDto> getActiveSessions(Long gameId) {
        return sessionRepository.findByGame_IdAndIsCompletedFalseOrderByStartedAtDesc(gameId).stream()
                .map(this::toSummaryDto)
                .toList();
    }

    private SessionSummaryDto toSummaryDto(GameSession session) {
        List<String> playerNames = session.getScores().stream()
                .map(s -> s.getPlayer().getDisplayName())
                .distinct()
                .toList();

        Player winner = session.getWinner();

        return SessionSummaryDto.builder()
                .id(session.getId())
                .sessionName(session.getSessionName())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .location(session.getLocation())
                .isCompleted(session.getIsCompleted())
                .playerNames(playerNames)
                .winnerName(winner != null ? winner.getDisplayName() : null)
                .build();
    }

    public SessionDto saveResults(Long id, SaveResultsDto dto) {
        GameSession session = getSession(id);

        Map<Long, Score> scoresById = session.getScores().stream()
                .collect(Collectors.toMap(Score::getId, s -> s));

        for (ScoreUpdateDto update : dto.getScores()) {
            Score score = scoresById.get(update.getScoreId());
            if (score == null) {
                throw new RuntimeException(
                        "Score " + update.getScoreId() + " does not belong to session " + id);
            }
            double weight = score.getScoreType().getWeight() != null ? score.getScoreType().getWeight() : 1.0;
            score.setValue(update.getValue());
            // Считаем явно, не полагаясь на момент срабатывания @PreUpdate у Score,
            // иначе итоги победителя ниже могли бы использовать ещё не обновлённое значение.
            score.setCalculatedValue(update.getValue() * weight);
        }

        session.setNotes(dto.getComment());
        session.setIsCompleted(true);
        if (session.getEndedAt() == null) {
            session.setEndedAt(LocalDateTime.now());
        }
        session.setWinner(determineWinner(session, dto.getWinnerId()));

        GameSession saved = sessionRepository.save(session);
        return toDto(saved);
    }

    public GameSession endSession(Long id) {
        GameSession session = getSession(id);
        session.setEndedAt(LocalDateTime.now());
        session.setIsCompleted(true);
        return sessionRepository.save(session);
    }

    private Player determineWinner(GameSession session, Long explicitWinnerId) {
        if (explicitWinnerId != null) {
            return playerRepository.findById(explicitWinnerId)
                    .orElseThrow(() -> new RuntimeException("Player not found with id: " + explicitWinnerId));
        }

        Map<Long, Double> totals = new HashMap<>();
        Map<Long, Player> playersById = new HashMap<>();
        for (Score s : session.getScores()) {
            Player p = s.getPlayer();
            double val = s.getCalculatedValue() != null ? s.getCalculatedValue() : 0;
            totals.merge(p.getId(), val, Double::sum);
            playersById.put(p.getId(), p);
        }

        return totals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> playersById.get(e.getKey()))
                .orElse(null);
    }

    private SessionDto toDto(GameSession session) {
        Game game = session.getGame();

        List<ScoreTypeDto> scoreTypeDtos = game.getScoreTypes().stream()
                .sorted(Comparator.comparing(ScoreType::getDisplayOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(st -> ScoreTypeDto.builder()
                        .id(st.getId())
                        .name(st.getName())
                        .description(st.getDescription())
                        .displayOrder(st.getDisplayOrder())
                        .colorCode(st.getColorCode())
                        .weight(st.getWeight())
                        .build())
                .toList();

        List<Score> scores = session.getScores();

        Map<Long, SessionPlayerDto> playerMap = new LinkedHashMap<>();
        for (Score s : scores) {
            Player p = s.getPlayer();
            playerMap.putIfAbsent(p.getId(),
                    SessionPlayerDto.builder().id(p.getId()).displayName(p.getDisplayName()).build());
        }

        List<ScoreDto> scoreDtos = scores.stream().map(s -> ScoreDto.builder()
                        .id(s.getId())
                        .sessionId(session.getId())
                        .sessionName(session.getSessionName())
                        .playerId(s.getPlayer().getId())
                        .playerName(s.getPlayer().getDisplayName())
                        .scoreTypeId(s.getScoreType().getId())
                        .scoreTypeName(s.getScoreType().getName())
                        .value(s.getValue())
                        .calculatedValue(s.getCalculatedValue())
                        .weight(s.getScoreType().getWeight())
                        .notes(s.getNotes())
                        .build())
                .toList();

        Player winner = session.getWinner();

        return SessionDto.builder()
                .id(session.getId())
                .gameId(game.getId())
                .gameName(game.getName())
                .gameLogoUrl(game.getLogoObjectKey() != null && !game.getLogoObjectKey().isBlank()
                        ? minioService.getFileUrl(game.getLogoObjectKey()) : null)
                .sessionName(session.getSessionName())
                .location(session.getLocation())
                .comment(session.getNotes())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .isCompleted(session.getIsCompleted())
                .winnerId(winner != null ? winner.getId() : null)
                .winnerName(winner != null ? winner.getDisplayName() : null)
                .scoreTypes(scoreTypeDtos)
                .players(new ArrayList<>(playerMap.values()))
                .scores(scoreDtos)
                .build();
    }
}
