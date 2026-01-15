package com.family.boardgames.repo;

import com.family.boardgames.model.ScoreType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreTypeRepository extends CrudRepository<ScoreType, Long> {
}
