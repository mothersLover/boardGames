package com.family.boardgames.service;

import com.family.boardgames.mapper.PlayerMapperImpl;
import com.family.boardgames.model.GameSession;
import com.family.boardgames.model.Player;
import com.family.boardgames.model.Score;
import com.family.boardgames.model.dto.PlayerDto;
import com.family.boardgames.repo.GameSessionRepository;
import com.family.boardgames.repo.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final GameSessionRepository sessionRepository;
    private final PlayerMapperImpl playerMapper;

    @Transactional(readOnly = true)
    public List<PlayerDto> searchPlayers(String query, int limit) {
        // Ищем по username и displayName
        List<Player> players = playerRepository.searchPlayers(query, limit);

        // Можно добавить логику релевантности
        List<PlayerDto> dtos = players.stream()
                .map(playerMapper::toDto)
                .collect(Collectors.toList());
        populateStats(dtos);
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<PlayerDto> getRecentPlayers(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        List<PlayerDto> dtos = playerRepository.findAll(pageable)
                .stream()
                .map(playerMapper::toDto)
                .collect(Collectors.toList());
        populateStats(dtos);
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<PlayerDto> getAllPlayers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("displayName"));
        List<PlayerDto> dtos = playerRepository.findAll(pageable)
                .stream()
                .map(playerMapper::toDto)
                .collect(Collectors.toList());
        populateStats(dtos);
        return dtos;
    }

    @Transactional(readOnly = true)
    public PlayerDto getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
        PlayerDto dto = playerMapper.toDto(player);
        populateStats(List.of(dto));
        return dto;
    }

    public PlayerDto createPlayer(PlayerDto dto) {
        Player player = playerMapper.toEntity(dto);
        player.setId(null);
        return playerMapper.toDto(playerRepository.save(player));
    }

    public PlayerDto updatePlayer(Long id, PlayerDto dto) {
        Player existing = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
        // Копируем только скалярные поля, не трогая коллекцию scores (cascade=ALL).
        // gamesPlayed/gamesWon сюда не входят: это уже не редактируемые вручную
        // значения, а всегда пересчитываются из реальных завершённых партий (см. populateStats).
        existing.setUsername(dto.getUserName());
        existing.setDisplayName(dto.getDisplayName());
        existing.setEmail(dto.getEmail());
        existing.setRating(dto.getRating());
        PlayerDto saved = playerMapper.toDto(playerRepository.save(existing));
        populateStats(List.of(saved));
        return saved;
    }

    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new RuntimeException("Player not found with id: " + id);
        }
        playerRepository.deleteById(id);
    }

    /**
     * Колонки games_played/games_won в таблице players нигде не инкрементировались
     * при завершении партий, поэтому всегда показывали 0. Вместо ручной синхронизации
     * счётчиков пересчитываем их каждый раз напрямую из завершённых GameSession —
     * это единственный источник правды и он не может рассинхронизироваться.
     */
    private void populateStats(List<PlayerDto> dtos) {
        if (dtos.isEmpty()) return;

        List<GameSession> completed = sessionRepository.findByIsCompletedTrue();

        Map<Long, Integer> playedCount = new HashMap<>();
        Map<Long, Integer> wonCount = new HashMap<>();

        for (GameSession session : completed) {
            Set<Long> playersInSession = new HashSet<>();
            for (Score score : session.getScores()) {
                playersInSession.add(score.getPlayer().getId());
            }
            for (Long playerId : playersInSession) {
                playedCount.merge(playerId, 1, Integer::sum);
            }
            if (session.getWinner() != null) {
                wonCount.merge(session.getWinner().getId(), 1, Integer::sum);
            }
        }

        for (PlayerDto dto : dtos) {
            dto.setTotalGames(playedCount.getOrDefault(dto.getId(), 0));
            dto.setWins(wonCount.getOrDefault(dto.getId(), 0));
        }
    }
}
