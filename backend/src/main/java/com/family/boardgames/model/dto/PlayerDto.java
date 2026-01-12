package com.family.boardgames.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerDto {

    private Long id;
    private String userName;
    private String displayName;
    private String email;
    private Double rating;
    private Integer totalGames;
    private Integer wins;

    public Double getWinRate() {
        if (totalGames == null || totalGames == 0) return 0.0;
        return wins != null ? (double) wins / totalGames * 100 : 0.0;
    }
}