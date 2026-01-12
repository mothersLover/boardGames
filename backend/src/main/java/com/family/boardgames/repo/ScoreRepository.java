
package com.family.boardgames.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.family.boardgames.model.Score;

public interface ScoreRepository extends JpaRepository<Score, Long> {
}
