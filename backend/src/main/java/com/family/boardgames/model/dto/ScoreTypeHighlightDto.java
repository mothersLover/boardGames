package com.family.boardgames.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreTypeHighlightDto {
    private String scoreTypeName;
    private String colorCode;
    private String playerName;
    private Double value;
}
