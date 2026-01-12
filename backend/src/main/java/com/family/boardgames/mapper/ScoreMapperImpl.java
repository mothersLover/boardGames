//package com.family.boardgames.mapper;
//
//import com.family.boardgames.model.Score;
//import com.family.boardgames.model.dto.ScoreDto;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ScoreMapperImpl {
//
//    public ScoreDto toDto(Score score) {
//        if (score == null) {
//            return null;
//        }
//
//        return ScoreDto.builder()
//                .id(score.getId())
//                .sessionId(score.getSession() != null ? score.getSession().getId() : null)
//                .sessionName(score.getSession() != null ? score.getSession().getSessionName() : null)
////                .sessionDate(score.getSession() != null ? score.getSession().getStartedAt() : null)
//                .playerId(score.getPlayer() != null ? score.getPlayer().getId() : null)
//                .playerName(score.getPlayer() != null ? score.getPlayer().getDisplayName() : null)
//                .playerAvatarUrl(score.getPlayer() != null ? score.getPlayer().getAvatarUrl() : null)
//                .scoreTypeId(score.getScoreType() != null ? score.getScoreType().getId() : null)
//                .scoreTypeName(score.getScoreType() != null ? score.getScoreType().getName() : null)
////                .scoreTypeCode(score.getScoreType() != null ? score.getScoreType().getCode() : null)
//                .value(score.getValue())
//                .calculatedValue(score.getCalculatedValue())
//                .weight(score.getScoreType() != null ? score.getScoreType().getWeight() : null)
//                .notes(score.getNotes())
////                .isWinner(score.getIsWinner())
////                .position(score.getPosition())
//                .build();
//    }
//
//    public PlayerScoreDto toPlayerScoreDto(Score score) {
//        if (score == null) {
//            return null;
//        }
//
//        return PlayerScoreDto.builder()
//                .id(score.getId())
//                .sessionDate(score.getSession() != null ? score.getSession().getStartedAt() : null)
//                .gameName(score.getSession() != null && score.getSession().getGame() != null
//                        ? score.getSession().getGame().getName() : null)
//                .scoreTypeName(score.getScoreType() != null ? score.getScoreType().getName() : null)
//                .value(score.getValue())
//                .calculatedValue(score.getCalculatedValue())
//                .position(score.getPosition())
//                .isWinner(score.getIsWinner())
//                .sessionNotes(score.getSession() != null ? score.getSession().getNotes() : null)
//                .build();
//    }
//
//    public Score toEntity(CreateScoreDto dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        return Score.builder()
//                .value(dto.getValue())
//                .notes(dto.getNotes())
//                .isWinner(dto.getIsWinner() != null ? dto.getIsWinner() : false)
//                .position(dto.getPosition())
//                .build();
//    }
//
//    public void setRelations(Score score, GameSession session, Player player, ScoreType scoreType) {
//        score.setSession(session);
//        score.setPlayer(player);
//        score.setScoreType(scoreType);
//
//        // Recalculate after setting scoreType
//        if (score.getValue() != null && scoreType != null) {
//            score.calculateValue();
//        }
//    }
//
//    public Score createScoreWithRelations(CreateScoreDto dto, GameSession session,
//                                         Player player, ScoreType scoreType) {
//        Score score = toEntity(dto);
//        setRelations(score, session, player, scoreType);
//        return score;
//    }
//}