
package com.family.boardgames.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.family.boardgames.model.GameSession;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
}
