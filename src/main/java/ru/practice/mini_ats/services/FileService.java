package ru.practice.mini_ats.services;

import io.minio.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioClient minioClient;

    @Value("${minio.url}")
    private String minioEndpoint;

    @Getter
    @Value("${minio.bucket-name-resume}")
    private String resumeBucketName;

    @Getter
    @Value("${minio.bucket-name-companies}")
    private String companyBucketName;

    @PostConstruct
    public void initBuckets() {
        initBucket(resumeBucketName);
        initBucket(companyBucketName);
    }

    private void initBucket(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket '{}' created", bucketName);
                    setPublicBucketPolicy(bucketName);
            } else {
                log.info("Bucket '{}' already exists", bucketName);
                    setPublicBucketPolicy(bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize bucket '{}': {}", bucketName, e.getMessage());
            throw new RuntimeException("MinIO bucket initialization failed for " + bucketName, e);
        }
    }

    public String uploadFile(MultipartFile file, String login, String bucketName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        try {
            String uniqueFileName = login + file.getOriginalFilename();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(uniqueFileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            log.info("File uploaded to bucket '{}': {}", bucketName, uniqueFileName);
            return uniqueFileName;
        } catch (Exception e) {
            log.error("Upload failed to bucket '{}': {}", bucketName, e.getMessage());
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    public InputStream downloadFile(String bucketName, String fileName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            log.error("Download failed from bucket '{}': {}", bucketName, e.getMessage());
            throw new RuntimeException("Failed to download file from MinIO", e);
        }
    }

    public void deleteFile(String bucketName, String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
            log.info("File deleted from bucket '{}': {}", bucketName, fileName);
        } catch (Exception e) {
            log.error("Delete failed from bucket '{}': {}", bucketName, e.getMessage());
            throw new RuntimeException("Failed to delete file from MinIO", e);
        }
    }

    public String getPublicFileUrl(String bucketName, String fileName) {
        return String.format("%s/%s/%s", minioEndpoint.replaceFirst("/$", ""), bucketName, fileName);
    }
    private void setPublicBucketPolicy(String bucketName) {
        try {
            String policy = """
            {
              "Version": "2012-10-17",
              "Statement": [
                {
                  "Effect": "Allow",
                  "Principal": {"AWS": ["*"]},
                  "Action": ["s3:GetBucketLocation", "s3:ListBucket"],
                  "Resource": ["arn:aws:s3:::%s"]
                },
                {
                  "Effect": "Allow",
                  "Principal": {"AWS": ["*"]},
                  "Action": ["s3:GetObject"],
                  "Resource": ["arn:aws:s3:::%s/*"]
                }
              ]
            }
            """.formatted(bucketName, bucketName);
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
            log.info("Public read policy set for bucket '{}'", bucketName);
        } catch (Exception e) {
            log.warn("Failed to set public policy for bucket '{}': {}", bucketName, e.getMessage());
        }
    }
}