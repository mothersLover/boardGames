package com.family.boardgames.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SaveResultsDto {

    @NotEmpty(message = "At least one score is required")
    @Valid
    private List<ScoreUpdateDto> scores;

    private String comment;

    // Необязательно: ручное переопределение победителя. Если не указано,
    // победитель определяется автоматически по сумме очков.
    private Long winnerId;
}
