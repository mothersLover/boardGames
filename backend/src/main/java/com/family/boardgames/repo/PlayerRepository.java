
package com.family.boardgames.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.family.boardgames.model.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByUsername(String name);

    @Query("SELECT p FROM Player p WHERE " +
            "LOWER(p.userName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.displayName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "ORDER BY " +
            "CASE WHEN LOWER(p.userName) = LOWER(:query) THEN 0 " +
            "     WHEN LOWER(p.displayName) = LOWER(:query) THEN 1 " +
            "     WHEN LOWER(p.userName) LIKE LOWER(CONCAT(:query, '%')) THEN 2 " +
            "     WHEN LOWER(p.displayName) LIKE LOWER(CONCAT(:query, '%')) THEN 3 " +
            "     ELSE 4 END, " +
            "p.displayName ASC")
    List<Player> searchPlayers(@Param("query") String query, Pageable pageable);

    default List<Player> searchPlayers(String query, int limit) {
        return searchPlayers(query, org.springframework.data.domain.PageRequest.of(0, limit));
    }

    List<Player> findByUserNameContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
            String userName, String displayName, Pageable pageable);
}
