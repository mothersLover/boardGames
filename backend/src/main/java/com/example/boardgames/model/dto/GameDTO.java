package com.example.boardgames.model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class GameDTO {
    private Long id;
    private String name;
    private String description;
    private String genre;
    private Integer minPlayers;
    private Integer maxPlayers;
    private Integer ageRating;
    private Double price;
    private Boolean isActive;
    private GameMediaDTO media;
    private Set<String> tags;
    private LocalDateTime createdAt;
}

@Data
class GameMediaDTO {
    private Long id;
    private List<String> audioUrls;
    private List<String> videoUrls;
    private List<String> instructionUrls;
    private List<String> otherUrls;
}