package com.family.boardgames.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "players")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @NotBlank
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @Column(name = "rating")
    private Double rating;
    
    @Column(name = "games_played")
    @Builder.Default
    private Integer gamesPlayed = 0;
    
    @Column(name = "games_won")
    @Builder.Default
    private Integer gamesWon = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Score> scores = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}