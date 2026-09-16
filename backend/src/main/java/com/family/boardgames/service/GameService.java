
package com.family.boardgames.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import com.family.boardgames.model.GameMedia;
import com.family.boardgames.model.dto.FileUploadDto;
import com.family.boardgames.repo.GameRepository;
import com.family.boardgames.repo.GameSessionRepository;
import com.family.boardgames.model.Game;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class GameService {

    private static final String LOGO_FOLDER = "games/logos";

    private final GameRepository repo;
    private final GameSessionRepository sessionRepository;
    private final MinioService minioService;

    public GameService(GameRepository repo, GameSessionRepository sessionRepository, MinioService minioService) {
        this.repo = repo;
        this.sessionRepository = sessionRepository;
        this.minioService = minioService;
    }

    @Transactional(readOnly = true)
    public List<Game> all() {
        List<Game> games = repo.findAll();
        games.forEach(this::populateComputedFields);
        return games;
    }

    @Transactional(readOnly = true)
    public List<Game> allActive() {
        List<Game> games = repo.findByIsActiveTrue();
        games.forEach(this::populateComputedFields);
        return games;
    }

    @Transactional(readOnly = true)
    public Game getById(Long id) {
        Game game = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));
        populateComputedFields(game);
        return game;
    }

    public Game create(Game g) {
        Game saved = repo.save(g);
        populateComputedFields(saved);
        return saved;
    }

    public Game update(Long id, Game incoming) {
        Game existing = getById(id);
        // Копируем только скалярные поля: sessions/scoreTypes/tags/media используют
        // cascade=ALL (+orphanRemoval для sessions/scoreTypes) и не должны затираться
        // значениями из формы админки, которая их вообще не редактирует.
        existing.setName(incoming.getName());
        existing.setDescription(incoming.getDescription());
        existing.setGenre(incoming.getGenre());
        existing.setMinPlayers(incoming.getMinPlayers());
        existing.setMaxPlayers(incoming.getMaxPlayers());
        existing.setAgeRating(incoming.getAgeRating());
        existing.setPrice(incoming.getPrice());
        existing.setIsActive(incoming.getIsActive());
        Game saved = repo.save(existing);
        populateComputedFields(saved);
        return saved;
    }

    public Game uploadLogo(Long id, MultipartFile file) {
        Game game = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));

        String oldKey = game.getLogoObjectKey();
        String newKey = minioService.uploadFile(file, LOGO_FOLDER);
        game.setLogoObjectKey(newKey);
        Game saved = repo.save(game);

        if (oldKey != null && !oldKey.isBlank()) {
            try {
                minioService.deleteFile(oldKey);
            } catch (Exception e) {
                log.warn("Не удалось удалить старый логотип {} из MinIO: {}", oldKey, e.getMessage());
            }
        }

        populateComputedFields(saved);
        return saved;
    }

    public Game uploadMedia(Long id, MultipartFile file, FileUploadDto.FileType type) {
        Game game = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));

        GameMedia media = game.getMedia();
        if (media == null) {
            media = GameMedia.builder().build();
            game.setMedia(media);
        }

        String objectKey = minioService.uploadFile(file, mediaFolder(type));
        addPath(media, type, objectKey);

        Game saved = repo.save(game);
        populateComputedFields(saved);
        return saved;
    }

    public Game deleteMediaItem(Long id, FileUploadDto.FileType type, String objectKey) {
        Game game = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));

        GameMedia media = game.getMedia();
        if (media != null && removePath(media, type, objectKey)) {
            try {
                minioService.deleteFile(objectKey);
            } catch (Exception e) {
                log.warn("Не удалось удалить файл {} из MinIO: {}", objectKey, e.getMessage());
            }
        }

        Game saved = repo.save(game);
        populateComputedFields(saved);
        return saved;
    }

    public void delete(Long id) {
        Game game = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));
        repo.deleteById(id);

        if (game.getLogoObjectKey() != null && !game.getLogoObjectKey().isBlank()) {
            try {
                minioService.deleteFile(game.getLogoObjectKey());
            } catch (Exception e) {
                log.warn("Не удалось удалить логотип {} из MinIO: {}", game.getLogoObjectKey(), e.getMessage());
            }
        }
    }

    private void populateComputedFields(Game game) {
        if (game.getLogoObjectKey() != null && !game.getLogoObjectKey().isBlank()) {
            game.setLogoUrl(minioService.getFileUrl(game.getLogoObjectKey()));
        }

        game.setCompletedSessionsCount(sessionRepository.countByGame_IdAndIsCompletedTrue(game.getId()));
        game.setActiveSessionsCount(sessionRepository.countByGame_IdAndIsCompletedFalse(game.getId()));

        GameMedia media = game.getMedia();
        if (media != null) {
            media.setAudioUrls(toUrls(media.getAudioPaths()));
            media.setVideoUrls(toUrls(media.getVideoPaths()));
            media.setInstructionUrls(toUrls(media.getInstructionPaths()));
            media.setOtherUrls(toUrls(media.getOtherPaths()));
        }
    }

    private List<String> toUrls(List<String> paths) {
        if (paths == null) return List.of();
        return paths.stream().map(minioService::getFileUrl).toList();
    }

    private String mediaFolder(FileUploadDto.FileType type) {
        return switch (type) {
            case AUDIO -> "games/audio";
            case VIDEO -> "games/video";
            case INSTRUCTION -> "games/instructions";
            case OTHER -> "games/other";
        };
    }

    private void addPath(GameMedia media, FileUploadDto.FileType type, String path) {
        switch (type) {
            case AUDIO -> media.addAudioPath(path);
            case VIDEO -> media.addVideoPath(path);
            case INSTRUCTION -> media.addInstructionPath(path);
            case OTHER -> media.addOtherPath(path);
        }
    }

    private boolean removePath(GameMedia media, FileUploadDto.FileType type, String path) {
        List<String> list = switch (type) {
            case AUDIO -> media.getAudioPaths();
            case VIDEO -> media.getVideoPaths();
            case INSTRUCTION -> media.getInstructionPaths();
            case OTHER -> media.getOtherPaths();
        };
        return list != null && list.remove(path);
    }
}
