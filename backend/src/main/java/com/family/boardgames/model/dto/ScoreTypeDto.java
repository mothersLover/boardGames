package com.family.boardgames.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreTypeDto {
    private Long id;
    private String name;
    private String description;
    private Integer displayOrder;
    private String colorCode;
    private Double weight;
}
