package com.family.boardgames.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StartSessionDto {
    
    @NotBlank(message = "Game ID is required")
    private String gameId;
    
    @NotEmpty(message = "At least one player is required")
    @Valid
    private List<PlayerDto> players;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startedAt;
    
    private String comment;
}