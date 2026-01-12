package com.family.boardgames.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "scores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Score {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private GameSession session;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score_type_id", nullable = false)
    private ScoreType scoreType;
    
    @NotNull
    @Column(name = "value", nullable = false)
    private Double value;
    
    @Column(name = "calculated_value")
    private Double calculatedValue; // value * weight
    
    @Column(name = "notes", length = 500)
    private String notes;
    
    @PrePersist
    @PreUpdate
    protected void calculateValue() {
        if (scoreType != null && scoreType.getWeight() != null && value != null) {
            this.calculatedValue = value * scoreType.getWeight();
        }
    }
}