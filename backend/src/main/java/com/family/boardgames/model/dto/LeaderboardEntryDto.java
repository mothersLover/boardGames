package com.family.boardgames.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDto {
    private Long playerId;
    private String playerName;
    private long sessionsPlayed;
    private long wins;
    private Double winRate;
    private Double averageScore;
    private Double bestScore;
}
