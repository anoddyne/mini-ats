package ru.practice.mini_ats.services;

import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioEndpoint;

    @PostConstruct
    public void initBucket() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                log.info("Bucket '{}' created successfully", bucketName);
            } else {
                log.info("Bucket '{}' already exists", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize bucket: {}", e.getMessage());
            throw new RuntimeException("MinIO bucket initialization failed", e);
        }
    }

    /**
     * Загрузка файла в MinIO
     *
     * @param file MultipartFile из запроса
     * @return уникальное имя файла (для сохранения в БД)
     */
    public String uploadFile(MultipartFile file, String login) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            String uniqueFileName =  login + file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("File uploaded successfully: {}", uniqueFileName);
            return uniqueFileName;

        } catch (Exception e) {
            log.error("Upload failed: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    /**
     * Скачивание файла из MinIO
     *
     * @param fileName уникальное имя файла
     * @return InputStream содержимого файла
     */
    public InputStream downloadFile(String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Download failed: {}", e.getMessage());
            throw new RuntimeException("Failed to download file from MinIO", e);
        }
    }

    /**
     * Удаление файла из MinIO
     *
     * @param fileName уникальное имя файла
     */
    public void deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            log.info("File deleted: {}", fileName);
        } catch (Exception e) {
            log.error("Delete failed: {}", e.getMessage());
            throw new RuntimeException("Failed to delete file from MinIO", e);
        }
    }

    /**
     * Получение публичной ссылки на файл (если бакет публичный)
     * @param fileName уникальное имя файла
     * @return прямая ссылка на файл
     */
    public String getPublicFileUrl(String fileName) {
        return String.format("%s/%s/%s",
                minioEndpoint.replaceFirst("/$", ""),
                bucketName,
                fileName
        );
    }

    // Приватный метод для установки публичной политики (если нужно)
    private void setPublicBucketPolicy() throws Exception {
        String policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": "*",
                      "Action": "s3:GetObject",
                      "Resource": "arn:aws:s3:::%s/*"
                    }
                  ]
                }
                """.formatted(bucketName);

        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policy)
                        .build()
        );
        log.info("Public read policy set for bucket: {}", bucketName);
    }
}