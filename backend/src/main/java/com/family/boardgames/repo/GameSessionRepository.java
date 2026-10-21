
package com.family.boardgames.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.family.boardgames.model.GameSession;

import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    List<GameSession> findByGame_IdAndIsCompletedFalseOrderByStartedAtDesc(Long gameId);

    List<GameSession> findByGame_IdAndIsCompletedTrueOrderByStartedAtDesc(Long gameId);

    Page<GameSession> findByGame_IdAndIsCompletedTrueOrderByStartedAtDesc(Long gameId, Pageable pageable);

    Page<GameSession> findByGame_IdOrderByStartedAtDesc(Long gameId, Pageable pageable);

    long countByGame_IdAndIsCompletedTrue(Long gameId);

    long countByGame_IdAndIsCompletedFalse(Long gameId);

    List<GameSession> findByIsCompletedTrue();
}
