package com.example.boardgames.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "score_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @NotNull
    @Column(name = "weight", nullable = false)
    @Builder.Default
    private Double weight = 1.0;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Column(name = "is_cumulative")
    @Builder.Default
    private Boolean isCumulative = true;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    
    @Column(name = "color_code", length = 20)
    private String colorCode; // Для визуализации в UI

    @Column(name = "picture", length = 20)
    private String picture;
}