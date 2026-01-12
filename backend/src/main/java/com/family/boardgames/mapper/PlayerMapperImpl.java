package com.family.boardgames.mapper;

import com.family.boardgames.model.Player;
import com.family.boardgames.model.dto.PlayerDto;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapperImpl {
    
    public PlayerDto toDto(Player player) {
        if (player == null) {
            return null;
        }
        
        return PlayerDto.builder()
                .id(player.getId())
                .userName(player.getUsername())
                .displayName(player.getDisplayName())
                .email(player.getEmail())
//                .avatarUrl(player.getAvatarUrl())
                .rating(player.getRating())
                .totalGames(player.getGamesPlayed())
                .wins(player.getGamesWon())
                .build();
    }

    
    public Player toEntity(PlayerDto dto) {
        if (dto == null) {
            return null;
        }
        
        return Player.builder()
                .id(dto.getId())
                .username(dto.getUserName())
                .displayName(dto.getDisplayName())
                .email(dto.getEmail())
                .rating(dto.getRating())
                .gamesPlayed(dto.getTotalGames())
                .gamesWon(dto.getWins())
                .build();
    }

}