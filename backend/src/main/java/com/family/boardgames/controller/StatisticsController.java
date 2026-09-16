package com.family.boardgames.controller;

import com.family.boardgames.model.dto.GameStatsDto;
import com.family.boardgames.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games/{gameId}/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public GameStatsDto getStats(@PathVariable Long gameId) {
        return statisticsService.getGameStats(gameId);
    }
}
