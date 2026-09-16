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
public class SessionSummaryDto {
    private Long id;
    private String sessionName;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String location;
    private Boolean isCompleted;
    private List<String> playerNames;
    private String winnerName;
}
