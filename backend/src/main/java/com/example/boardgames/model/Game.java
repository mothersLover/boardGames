
package com.example.boardgames.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "games")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название игры обязательно")
    @Size(min = 3, max = 100, message = "Название должно быть от 3 до 100 символов")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "genre", length = 50)
    private String genre;

    @Column(name = "min_players")
    private Integer minPlayers;

    @Column(name = "max_players")
    private Integer maxPlayers;

    @Column(name = "age_rating")
    private Integer ageRating;

    @Column(name = "price")
    private Double price;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // Ссылка на медиа (One-to-One связь)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", referencedColumnName = "id")
    private GameMedia media;

    // Типы очков для этой игры
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ScoreType> scoreTypes = new ArrayList<>();

    // Сессии этой игры
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @Builder.Default
    private List<GameSession> sessions = new ArrayList<>();

    // Метод для получения пресетов очков
    public static List<ScoreType> getSevenWondersScoreTypes() {
        return List.of(
                ScoreType.builder().name("Военные очки").displayOrder(1).weight(1.0).build(),
                ScoreType.builder().name("Очки денег").displayOrder(2).weight(1.0).build(),
                ScoreType.builder().name("Гражданские очки").displayOrder(3).weight(1.0).build(),
                ScoreType.builder().name("Научные очки").displayOrder(4).weight(1.0).build(),
                ScoreType.builder().name("Коммерческие очки").displayOrder(5).weight(1.0).build(),
                ScoreType.builder().name("Очки гильдий").displayOrder(6).weight(1.0).build(),
                ScoreType.builder().name("Очки чудес").displayOrder(7).weight(1.0).build()
        );
    }

    public static List<ScoreType> getScytheScoreTypes() {
        return List.of(
                ScoreType.builder().name("Популярность").displayOrder(1).weight(1.0).build(),
                ScoreType.builder().name("Территории").displayOrder(2).weight(1.0).build(),
                ScoreType.builder().name("Ресурсы").displayOrder(3).weight(1.0).build(),
                ScoreType.builder().name("Строения").displayOrder(4).weight(1.0).build(),
                ScoreType.builder().name("Монеты").displayOrder(5).weight(1.0).build()
        );
    }

    // Теги для игры (многие-ко-многим)
    @ManyToMany
    @JoinTable(
            name = "game_tags",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "version")
    @Version
    private Long version;

    // Метод для установки медиа
    public void setMedia(GameMedia media) {
        this.media = media;
        if (media != null) {
            media.setId(this.id);
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
