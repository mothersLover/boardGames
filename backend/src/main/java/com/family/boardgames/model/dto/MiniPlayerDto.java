package com.family.boardgames.model.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MiniPlayerDto {
    private Boolean isValid;
    private String name;
    private Long playerId;
}
