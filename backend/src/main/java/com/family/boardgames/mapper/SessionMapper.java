//package com.family.boardgames.mapper;
//
//import com.family.boardgames.model.GameSession;
//import com.family.boardgames.model.Player;
//import com.family.boardgames.model.dto.PlayerDto;
//import com.family.boardgames.model.dto.SessionDto;
//import com.family.boardgames.model.dto.StartSessionDto;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.factory.Mappers;
//
//@Mapper(componentModel = "spring")
//public interface SessionMapper {
//
//    SessionMapper INSTANCE = Mappers.getMapper(SessionMapper.class);
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "endedAt", ignore = true)
//    @Mapping(target = "status", constant = "ACTIVE")
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
//    GameSession toEntity(StartSessionDto dto);
//
//    @Mapping(target = "players", source = "players")
//    SessionDto toDto(GameSession entity);
//
//    default Player mapPlayer(PlayerDto dto) {
//        Player player = new Player();
////        player.setUsername(dto.getName());
//        return player;
//    }
//}