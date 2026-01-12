package com.family.boardgames.service;

import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    
    private final MinioClient minioClient;
    
    @Value("${minio.bucket}")
    private String bucketName;
    
    @Value("${app.minio-public-url}")
    private String minioPublicUrl;
    
    /**
     * Загрузка файла в MinIO
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // Проверяем существование бакета
            ensureBucketExists();
            
            // Генерируем уникальное имя файла
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID() + extension;
            String objectPath = folder + "/" + filename;
            
            // Загружаем файл
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectPath)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            log.info("Файл {} успешно загружен в MinIO: {}", originalFilename, objectPath);
            return objectPath;
            
        } catch (Exception e) {
            log.error("Ошибка загрузки файла в MinIO", e);
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }
    
    /**
     * Удаление файла из MinIO
     */
    public void deleteFile(String objectPath) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectPath)
                    .build()
            );
            log.info("Файл {} удален из MinIO", objectPath);
        } catch (Exception e) {
            log.error("Ошибка удаления файла из MinIO", e);
            throw new RuntimeException("Failed to delete file from MinIO", e);
        }
    }
    
    /**
     * Получение URL для доступа к файлу
     */
    public String getFileUrl(String objectPath) {
        return minioPublicUrl + "/" + bucketName + "/" + objectPath;
    }
    
    /**
     * Проверка существования бакета
     */
    private void ensureBucketExists() throws Exception {
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
        
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            log.info("Бакет {} создан", bucketName);
        }
    }
    
    /**
     * Получение расширения файла
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}