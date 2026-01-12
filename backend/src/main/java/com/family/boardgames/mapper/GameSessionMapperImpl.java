//package com.family.boardgames.mapper;
//
//import com.family.boardgames.model.GameSession;
//import com.family.boardgames.model.dto.SessionDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class GameSessionMapperImpl {
//
//    private final GameMapper gameMapper;
//    private final PlayerMapper playerMapper;
//    private final ScoreMapper scoreMapper;
//
//    public SessionDto toDto(GameSession session) {
//        if (session == null) {
//            return null;
//        }
//
//        return SessionDto.builder()
//                .id(session.getId())
//                .gameId(session.getGame().getName())
//                .name(session.getSessionName())
//                .startedAt(session.getStartedAt())
//                .endedAt(session.getEndedAt())
//                .location(session.getLocation())
//                .comment(session.getNotes())
//                .isCompleted(session.getIsCompleted())
////                .winner(playerMapper.toDto(session.getWinner()))
//                .scores(scoreMapper.toDtoList(session.getScores()))
//                .playerCount(getPlayerCount(session))
//                .durationMinutes(getDurationMinutes(session))
//                .build();
//    }
//
//    public GameSessionSummaryDto toSummaryDto(GameSession session) {
//        if (session == null) {
//            return null;
//        }
//
//        return GameSessionSummaryDto.builder()
//                .id(session.getId())
//                .sessionName(session.getSessionName())
//                .gameName(session.getGame() != null ? session.getGame().getName() : null)
//                .gameImageUrl(session.getGame() != null ? session.getGame().getImageUrl() : null)
//                .startedAt(session.getStartedAt())
//                .endedAt(session.getEndedAt())
//                .location(session.getLocation())
//                .isCompleted(session.getIsCompleted())
//                .winnerName(session.getWinner() != null ? session.getWinner().getDisplayName() : null)
//                .winnerAvatarUrl(session.getWinner() != null ? session.getWinner().getAvatarUrl() : null)
//                .playerCount(getPlayerCount(session))
//                .durationMinutes(getDurationMinutes(session))
//                .build();
//    }
//
//    public GameSession toEntity(CreateGameSessionDto dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        return GameSession.builder()
//                .sessionName(dto.getSessionName())
//                .startedAt(dto.getStartedAt() != null ? dto.getStartedAt() : java.time.LocalDateTime.now())
//                .location(dto.getLocation())
//                .notes(dto.getNotes())
//                .isCompleted(false)
//                .build();
//    }
//
//    public void updateEntity(GameSession session, UpdateGameSessionDto dto) {
//        if (session == null || dto == null) {
//            return;
//        }
//
//        if (dto.getSessionName() != null) {
//            session.setSessionName(dto.getSessionName());
//        }
//        if (dto.getEndedAt() != null) {
//            session.setEndedAt(dto.getEndedAt());
//        }
//        if (dto.getLocation() != null) {
//            session.setLocation(dto.getLocation());
//        }
//        if (dto.getNotes() != null) {
//            session.setNotes(dto.getNotes());
//        }
//        if (dto.getIsCompleted() != null) {
//            session.setIsCompleted(dto.getIsCompleted());
//        }
//    }
//
//    public List<GameSessionDto> toDtoList(List<GameSession> sessions) {
//        return sessions.stream()
//                .map(this::toDto)
//                .collect(Collectors.toList());
//    }
//
//    public List<GameSessionSummaryDto> toSummaryDtoList(List<GameSession> sessions) {
//        return sessions.stream()
//                .map(this::toSummaryDto)
//                .collect(Collectors.toList());
//    }
//
//    public GameSessionStatsDto toStatsDto(List<GameSession> sessions) {
//        if (sessions == null || sessions.isEmpty()) {
//            return GameSessionStatsDto.builder()
//                    .totalSessions(0L)
//                    .totalPlayingTimeMinutes(0L)
//                    .averageSessionDuration(0.0)
//                    .sessionsThisMonth(0)
//                    .build();
//        }
//
//        long totalDuration = sessions.stream()
//                .filter(s -> s.getStartedAt() != null && s.getEndedAt() != null)
//                .mapToLong(s -> Duration.between(s.getStartedAt(), s.getEndedAt()).toMinutes())
//                .sum();
//
//        // Find most played game
//        String mostPlayedGame = sessions.stream()
//                .collect(Collectors.groupingBy(s -> s.getGame().getName(), Collectors.counting()))
//                .entrySet().stream()
//                .max(java.util.Map.Entry.comparingByValue())
//                .map(java.util.Map.Entry::getKey)
//                .orElse(null);
//
//        return GameSessionStatsDto.builder()
//                .totalSessions((long) sessions.size())
//                .totalPlayingTimeMinutes(totalDuration)
//                .averageSessionDuration(sessions.size() > 0 ? (double) totalDuration / sessions.size() : 0.0)
//                .sessionsThisMonth(countSessionsThisMonth(sessions))
//                .mostPlayedGame(mostPlayedGame)
//                .build();
//    }
//
//    private Integer getPlayerCount(GameSession session) {
//        return session.getScores() != null ? session.getScores().size() : 0;
//    }
//
//    private Long getDurationMinutes(GameSession session) {
//        if (session.getStartedAt() != null && session.getEndedAt() != null) {
//            return Duration.between(session.getStartedAt(), session.getEndedAt()).toMinutes();
//        }
//        return null;
//    }
//
//    private Integer countSessionsThisMonth(List<GameSession> sessions) {
//        java.time.LocalDateTime startOfMonth = java.time.LocalDateTime.now()
//                .withDayOfMonth(1)
//                .withHour(0)
//                .withMinute(0)
//                .withSecond(0);
//
//        return (int) sessions.stream()
//                .filter(s -> s.getStartedAt() != null && s.getStartedAt().isAfter(startOfMonth))
//                .count();
//    }
//
//    public void setGameAndScores(GameSession session, com.yourproject.entity.Game game, List<Score> scores) {
//        session.setGame(game);
//        session.setScores(scores);
//
//        // Calculate winner
//        calculateWinner(session);
//    }
//
//    private void calculateWinner(GameSession session) {
//        if (session.getScores() != null && !session.getScores().isEmpty()) {
//            Score winnerScore = session.getScores().stream()
//                    .filter(score -> Boolean.TRUE.equals(score.getIsWinner()))
//                    .findFirst()
//                    .orElseGet(() -> session.getScores().stream()
//                            .max((s1, s2) -> Integer.compare(
//                                    s1.getScore() != null ? s1.getScore() : 0,
//                                    s2.getScore() != null ? s2.getScore() : 0))
//                            .orElse(null));
//
//            if (winnerScore != null) {
//                session.setWinner(winnerScore.getPlayer());
//                winnerScore.setIsWinner(true);
//            }
//        }
//    }
//}