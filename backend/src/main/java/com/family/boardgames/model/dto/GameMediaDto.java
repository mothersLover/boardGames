package com.family.boardgames.model.dto;

import lombok.Data;

import java.util.List;

@Data
class GameMediaDto {
    private Long id;
    private List<String> audioUrls;
    private List<String> videoUrls;
    private List<String> instructionUrls;
    private List<String> otherUrls;
}