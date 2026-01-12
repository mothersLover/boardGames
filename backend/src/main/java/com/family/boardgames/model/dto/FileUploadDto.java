package com.family.boardgames.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadDto {
    private MultipartFile file;
    private FileType fileType;
    
    public enum FileType {
        AUDIO, VIDEO, INSTRUCTION, OTHER
    }
}