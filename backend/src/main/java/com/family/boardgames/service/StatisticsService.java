package com.family.boardgames.service;

import com.family.boardgames.model.Game;
import com.family.boardgames.model.GameSession;
import com.family.boardgames.model.Score;
import com.family.boardgames.model.ScoreType;
import com.family.boardgames.model.dto.GameStatsDto;
import com.family.boardgames.model.dto.LeaderboardEntryDto;
import com.family.boardgames.model.dto.ScoreHighlightDto;
import com.family.boardgames.model.dto.ScoreTypeHighlightDto;
import com.family.boardgames.repo.GameRepository;
import com.family.boardgames.repo.GameSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final GameRepository gameRepository;
    private final GameSessionRepository sessionRepository;

    private record PlayerSessionTotal(Long sessionId, java.time.LocalDateTime date,
                                       Long playerId, String playerName, double total) {
    }

    public GameStatsDto getGameStats(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));

        List<GameSession> sessions =
                sessionRepository.findByGame_IdAndIsCompletedTrueOrderByStartedAtDesc(gameId);

        if (sessions.isEmpty()) {
            return GameStatsDto.builder()
                    .gameId(game.getId())
                    .gameName(game.getName())
                    .totalSessions(0)
                    .totalPlayers(0)
                    .averageScore(null)
                    .highestScore(null)
                    .lowestScore(null)
                    .leaderboard(List.of())
                    .scoreTypeHighlights(List.of())
                    .build();
        }

        List<PlayerSessionTotal> totals = new ArrayList<>();
        for (GameSession s : sessions) {
            Map<Long, Double> perPlayer = new LinkedHashMap<>();
            Map<Long, String> names = new LinkedHashMap<>();
            for (Score sc : s.getScores()) {
                double v = sc.getCalculatedValue() != null ? sc.getCalculatedValue() : 0;
                Long pid = sc.getPlayer().getId();
                perPlayer.merge(pid, v, Double::sum);
                names.putIfAbsent(pid, sc.getPlayer().getDisplayName());
            }
            perPlayer.forEach((pid, total) ->
                    totals.add(new PlayerSessionTotal(s.getId(), s.getStartedAt(), pid, names.get(pid), total)));
        }

        PlayerSessionTotal highest = totals.stream()
                .max(Comparator.comparingDouble(PlayerSessionTotal::total))
                .orElse(null);
        PlayerSessionTotal lowest = totals.stream()
                .min(Comparator.comparingDouble(PlayerSessionTotal::total))
                .orElse(null);

        double averageScore = totals.stream().mapToDouble(PlayerSessionTotal::total).average().orElse(0);

        Map<Long, Long> winnerBySession = sessions.stream()
                .filter(s -> s.getWinner() != null)
                .collect(Collectors.toMap(GameSession::getId, s -> s.getWinner().getId()));

        Map<Long, List<PlayerSessionTotal>> byPlayer = totals.stream()
                .collect(Collectors.groupingBy(PlayerSessionTotal::playerId));

        List<LeaderboardEntryDto> leaderboard = byPlayer.entrySet().stream()
                .map(e -> {
                    Long playerId = e.getKey();
                    List<PlayerSessionTotal> list = e.getValue();
                    long sessionsPlayed = list.size();
                    long wins = list.stream()
                            .map(PlayerSessionTotal::sessionId)
                            .filter(sid -> playerId.equals(winnerBySession.get(sid)))
                            .distinct()
                            .count();
                    double avg = list.stream().mapToDouble(PlayerSessionTotal::total).average().orElse(0);
                    double best = list.stream().mapToDouble(PlayerSessionTotal::total).max().orElse(0);
                    return LeaderboardEntryDto.builder()
                            .playerId(playerId)
                            .playerName(list.get(0).playerName())
                            .sessionsPlayed(sessionsPlayed)
                            .wins(wins)
                            .winRate(sessionsPlayed > 0 ? (wins * 100.0 / sessionsPlayed) : 0)
                            .averageScore(avg)
                            .bestScore(best)
                            .build();
                })
                .sorted(Comparator.comparingLong(LeaderboardEntryDto::getWins).reversed()
                        .thenComparing(Comparator.comparingDouble(LeaderboardEntryDto::getAverageScore).reversed()))
                .toList();

        List<ScoreTypeHighlightDto> scoreTypeHighlights = game.getScoreTypes().stream()
                .sorted(Comparator.comparing(ScoreType::getDisplayOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(st -> {
                    Score best = sessions.stream()
                            .flatMap(s -> s.getScores().stream())
                            .filter(sc -> sc.getScoreType().getId().equals(st.getId()))
                            .max(Comparator.comparingDouble(sc -> sc.getValue() != null ? sc.getValue() : 0))
                            .orElse(null);
                    if (best == null) return null;
                    return ScoreTypeHighlightDto.builder()
                            .scoreTypeName(st.getName())
                            .colorCode(st.getColorCode())
                            .playerName(best.getPlayer().getDisplayName())
                            .value(best.getValue())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        long totalPlayers = byPlayer.size();

        return GameStatsDto.builder()
                .gameId(game.getId())
                .gameName(game.getName())
                .totalSessions(sessions.size())
                .totalPlayers(totalPlayers)
                .averageScore(averageScore)
                .highestScore(highest != null ? toHighlight(highest) : null)
                .lowestScore(lowest != null ? toHighlight(lowest) : null)
                .leaderboard(leaderboard)
                .scoreTypeHighlights(scoreTypeHighlights)
                .build();
    }

    private ScoreHighlightDto toHighlight(PlayerSessionTotal t) {
        return ScoreHighlightDto.builder()
                .sessionId(t.sessionId())
                .playerName(t.playerName())
                .value(t.total())
                .date(t.date())
                .build();
    }
}
