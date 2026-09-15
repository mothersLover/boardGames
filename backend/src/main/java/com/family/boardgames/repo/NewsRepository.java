package com.family.boardgames.repo;

import com.family.boardgames.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    Page<News> findAllByOrderByPublishedAtDesc(Pageable pageable);

    List<News> findAllByOrderByPublishedAtDesc();
}
