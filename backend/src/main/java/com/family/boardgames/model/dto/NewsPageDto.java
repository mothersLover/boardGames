package com.family.boardgames.model.dto;

import com.family.boardgames.model.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsPageDto {
    private List<News> content;
    private int page;
    private int totalPages;
    private long totalElements;
}
