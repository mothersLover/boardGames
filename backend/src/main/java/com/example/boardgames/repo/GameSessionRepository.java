
package com.example.boardgames.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.boardgames.model.GameSession;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
}
