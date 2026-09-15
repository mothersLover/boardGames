package com.family.boardgames.controller;

import com.family.boardgames.model.dto.NewsPageDto;
import com.family.boardgames.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public NewsPageDto getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return newsService.getPage(page, size);
    }
}
