package com.family.boardgames.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
    private Long id;
    private Long gameId;
    private String gameName;
    private String gameLogoUrl;
    private String sessionName;
    private String location;
    private String comment;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Boolean isCompleted;
    private Long winnerId;
    private String winnerName;
    private List<ScoreTypeDto> scoreTypes;
    private List<SessionPlayerDto> players;
    private List<ScoreDto> scores;
}
