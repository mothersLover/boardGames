package com.family.boardgames.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScoreUpdateDto {
    @NotNull(message = "scoreId is required")
    private Long scoreId;

    @NotNull(message = "value is required")
    private Double value;
}
