
package com.family.boardgames.repo;

import com.family.boardgames.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByIsActiveTrue();

    List<Game> findByNameContainingIgnoreCase(String name);

    Optional<Game> findByName(String gameName);

    List<Game> findByGenre(String genre);

    @Query("SELECT g FROM Game g JOIN g.tags t WHERE t.name = :tagName")
    List<Game> findByTagName(@Param("tagName") String tagName);

    Optional<Game> findByIdAndIsActiveTrue(Long id);
}
