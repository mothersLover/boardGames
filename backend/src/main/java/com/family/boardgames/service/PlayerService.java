package com.family.boardgames.service;

import com.family.boardgames.mapper.PlayerMapperImpl;
import com.family.boardgames.model.Player;
import com.family.boardgames.model.dto.PlayerDto;
import com.family.boardgames.repo.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService {
    
    private final PlayerRepository playerRepository;
    private final PlayerMapperImpl playerMapper;
    
    @Transactional(readOnly = true)
    public List<PlayerDto> searchPlayers(String query, int limit) {
        // Ищем по username и displayName
        List<Player> players = playerRepository.searchPlayers(query, limit);
        
        // Можно добавить логику релевантности
        return players.stream()
                .map(playerMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PlayerDto> getRecentPlayers(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return playerRepository.findAll(pageable)
                .stream()
                .map(playerMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PlayerDto> getAllPlayers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("displayName"));
        return playerRepository.findAll(pageable)
                .stream()
                .map(playerMapper::toDto)
                .collect(Collectors.toList());
    }
}