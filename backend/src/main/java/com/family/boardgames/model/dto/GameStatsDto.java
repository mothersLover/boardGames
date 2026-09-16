package com.family.boardgames.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameStatsDto {
    private Long gameId;
    private String gameName;
    private long totalSessions;
    private long totalPlayers;
    private Double averageScore;
    private ScoreHighlightDto highestScore;
    private ScoreHighlightDto lowestScore;
    private List<LeaderboardEntryDto> leaderboard;
    private List<ScoreTypeHighlightDto> scoreTypeHighlights;
}
