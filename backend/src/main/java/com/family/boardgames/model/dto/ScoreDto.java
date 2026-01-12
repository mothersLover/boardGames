package com.family.boardgames.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDto {
    private Long id;
    private Long sessionId;
    private String sessionName;
    private Long playerId;
    private String playerName;
    private String playerAvatarUrl;
    private Long scoreTypeId;
    private String scoreTypeName;
    private Double value;
    private Double calculatedValue;
    private String notes;
    private Double weight;

    // Calculated field for display
    public String getFormattedValue() {
        if (value == null) return "N/A";
        return String.format("%.1f", value);
    }

    public String getFormattedCalculatedValue() {
        if (calculatedValue == null) return "N/A";
        return String.format("%.1f", calculatedValue);
    }

    public Double getWeightedValue() {
        if (value != null && weight != null) {
            return value * weight;
        }
        return calculatedValue;
    }
}