package com.family.boardgames.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_media")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Аудио файлы - храним пути в MinIO
    @ElementCollection
    @CollectionTable(
            name = "game_media_audio",
            joinColumns = @JoinColumn(name = "game_media_id")
    )
    @Column(name = "audio_path", length = 500)
    @Builder.Default
    private List<String> audioPaths = new ArrayList<>();

    // Видео файлы - храним пути в MinIO
    @ElementCollection
    @CollectionTable(
            name = "game_media_video",
            joinColumns = @JoinColumn(name = "game_media_id")
    )
    @Column(name = "video_path", length = 500)
    @Builder.Default
    private List<String> videoPaths = new ArrayList<>();

    // Файлы инструкций
    @ElementCollection
    @CollectionTable(
            name = "game_media_instructions",
            joinColumns = @JoinColumn(name = "game_media_id")
    )
    @Column(name = "instruction_path", length = 500)
    @Builder.Default
    private List<String> instructionPaths = new ArrayList<>();

    // Другие файлы
    @ElementCollection
    @CollectionTable(
            name = "game_media_other",
            joinColumns = @JoinColumn(name = "game_media_id")
    )
    @Column(name = "other_path", length = 500)
    @Builder.Default
    private List<String> otherPaths = new ArrayList<>();

    // Методы для удобной работы с путями
    public void addAudioPath(String path) {
        if (this.audioPaths == null) {
            this.audioPaths = new ArrayList<>();
        }
        this.audioPaths.add(path);
    }

    public void addVideoPath(String path) {
        if (this.videoPaths == null) {
            this.videoPaths = new ArrayList<>();
        }
        this.videoPaths.add(path);
    }

    public void addInstructionPath(String path) {
        if (this.instructionPaths == null) {
            this.instructionPaths = new ArrayList<>();
        }
        this.instructionPaths.add(path);
    }

    public void addOtherPath(String path) {
        if (this.otherPaths == null) {
            this.otherPaths = new ArrayList<>();
        }
        this.otherPaths.add(path);
    }

    // Методы для получения полных URL (если нужно)
    public List<String> getAudioUrls(String baseUrl) {
        if (audioPaths == null) return new ArrayList<>();
        return audioPaths.stream()
                .map(path -> baseUrl + "/api/media/" + path)
                .toList();
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