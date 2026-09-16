
package com.family.boardgames.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.family.boardgames.model.GameSession;

import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    List<GameSession> findByGame_IdAndIsCompletedFalseOrderByStartedAtDesc(Long gameId);

    List<GameSession> findByGame_IdAndIsCompletedTrueOrderByStartedAtDesc(Long gameId);

    long countByGame_IdAndIsCompletedTrue(Long gameId);

    long countByGame_IdAndIsCompletedFalse(Long gameId);

    List<GameSession> findByIsCompletedTrue();
}
