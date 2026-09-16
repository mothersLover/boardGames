package com.family.boardgames.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreHighlightDto {
    private Long sessionId;
    private String playerName;
    private Double value;
    private LocalDateTime date;
}
