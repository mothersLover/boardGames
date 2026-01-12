package com.family.boardgames.model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class GameDto {
    private Long id;
    private String name;
    private String description;
    private String genre;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Integer ageRating;
    private Double price;
    private Boolean isActive;
    private GameMediaDto media;
    private Set<String> tags;
    private LocalDateTime createdAt;
}