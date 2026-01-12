package com.example.boardgames.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadDTO {
    private MultipartFile file;
    private FileType fileType;
    
    public enum FileType {
        AUDIO, VIDEO, INSTRUCTION, OTHER
    }
}