package ru.practice.mini_ats.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practice.mini_ats.dto.Resume.ResumeRequestDTO;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.services.ResumeService;

@Tag(name = "Resume Controller",description = "Обрабатывает запросы, связанные с резюме")
@RestController
@RequestMapping("/api/v1/resume")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;

    @Operation(summary = "Загрузить резюме")
    @PostMapping
    public ResponseEntity<ResumeResponseDTO> loadResume(@Valid @RequestBody ResumeRequestDTO dto, Integer userId){
        return new ResponseEntity<>(resumeService.createResume(dto, userId), HttpStatus.CREATED);
    }

    @Operation(summary = "Получить резюме по id")
    @GetMapping("/{id}")
    public ResponseEntity<ResumeResponseDTO> getResumeById(@PathVariable Integer id) {
        return ResponseEntity.ok(resumeService.getByUserId(id));
    }

    @Operation(summary = "Обновить резюме по id")
    @PutMapping("/{id}")
    public ResponseEntity<ResumeResponseDTO> updateResume(@PathVariable Integer id, @Valid @RequestBody ResumeRequestDTO dto) {
        return ResponseEntity.ok(resumeService.updateResume(id, dto));
    }

    @Operation(summary = "Удалить резюме по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResumeById(@PathVariable Integer id) {
        resumeService.deleteResume(id);
        return ResponseEntity.noContent().build();
    }
}
