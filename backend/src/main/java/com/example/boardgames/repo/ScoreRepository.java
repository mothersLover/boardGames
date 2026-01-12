
package com.example.boardgames.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.boardgames.model.Score;

public interface ScoreRepository extends JpaRepository<Score, Long> {
}
