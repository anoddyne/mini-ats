package ru.practice.mini_ats.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.services.ResumeService;

import java.io.InputStream;

@Tag(name = "Resume Controller", description = "Обрабатывает запросы, связанные с резюме")
@RestController
@RequestMapping("/api/v1/resume")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;

    @Operation(summary = "Загрузить резюме")
    @PostMapping
    public ResponseEntity<ResumeResponseDTO> loadResume(
            @RequestPart("file") MultipartFile file) {
        return new ResponseEntity<>(resumeService.createResume(file), HttpStatus.CREATED);
    }

    @Operation(summary = "Скачать резюме")
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> getResume() {
        try {
            InputStream inputStream = resumeService.getResumeByUserId();
            byte[] content = inputStream.readAllBytes();
            ByteArrayResource resource = new ByteArrayResource(content);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resume.pdf\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Удалить резюме по id пользователя")
    @DeleteMapping
    public ResponseEntity<Void> deleteResumeById() {
        resumeService.deleteResume();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить информацию о резюме текущего пользователя")
    @GetMapping
    public ResponseEntity<ResumeResponseDTO> getResumeInfo() {
        ResumeResponseDTO resume = resumeService.getCurrentUserResume();
        if (resume == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(resume);
    }
}
