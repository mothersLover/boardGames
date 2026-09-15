package com.family.boardgames.controller.admin;

import com.family.boardgames.model.News;
import com.family.boardgames.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

    private final NewsService newsService;

    @GetMapping
    public List<News> all() {
        return newsService.all();
    }

    @GetMapping("/{id}")
    public News getById(@PathVariable Long id) {
        return newsService.getById(id);
    }

    @PostMapping
    public ResponseEntity<News> create(@RequestBody News news) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.create(news));
    }

    @PutMapping("/{id}")
    public News update(@PathVariable Long id, @RequestBody News news) {
        return newsService.update(id, news);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
