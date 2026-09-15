package com.family.boardgames.service;

import com.family.boardgames.model.News;
import com.family.boardgames.model.dto.NewsPageDto;
import com.family.boardgames.repo.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsService {

    private final NewsRepository newsRepository;

    @Transactional(readOnly = true)
    public NewsPageDto getPage(int page, int size) {
        Page<News> result = newsRepository.findAllByOrderByPublishedAtDesc(PageRequest.of(page, size));
        return NewsPageDto.builder()
                .content(result.getContent())
                .page(result.getNumber())
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .build();
    }

    @Transactional(readOnly = true)
    public List<News> all() {
        return newsRepository.findAllByOrderByPublishedAtDesc();
    }

    @Transactional(readOnly = true)
    public News getById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
    }

    public News create(News news) {
        return newsRepository.save(news);
    }

    public News update(Long id, News incoming) {
        News existing = getById(id);
        existing.setTitle(incoming.getTitle());
        existing.setContent(incoming.getContent());
        existing.setPublishedAt(incoming.getPublishedAt() != null ? incoming.getPublishedAt() : LocalDateTime.now());
        return newsRepository.save(existing);
    }

    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new RuntimeException("News not found with id: " + id);
        }
        newsRepository.deleteById(id);
    }
}
