package com.family.boardgames.repo;

import com.family.boardgames.model.GameMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameMediaRepository extends JpaRepository<GameMedia, Long> {
}