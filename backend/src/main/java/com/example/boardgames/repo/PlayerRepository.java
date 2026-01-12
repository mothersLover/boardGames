
package com.example.boardgames.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.boardgames.model.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
