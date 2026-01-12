package com.family.boardgames.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SessionDto {
    private Long id;
    private String gameId;
    private String name;
    private String location;
    private List<PlayerDto> players;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Boolean isCompleted;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}