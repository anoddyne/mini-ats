package ru.practice.mini_ats.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionRequestDTO;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionResponseDTO;
import ru.practice.mini_ats.services.ResumeReactionService;

import java.util.List;

@Tag(name = "Resume reaction Controller",description = "Обрабатывает запросы, связанные с откликами резюме")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reactions")
public class ResumeReactionController {
    private final ResumeReactionService resumeReactionService;

    // Кандидат откликается на вакансию
    @Operation(summary = "Отклик на вакансию")
    @PostMapping("/apply")
    public ResponseEntity<ResumeReactionResponseDTO> applyToVacancy(@Valid @RequestBody ResumeReactionRequestDTO dto) {
        return new ResponseEntity<>(resumeReactionService.applyToVacancy(dto), HttpStatus.CREATED);
    }

    // Рекрутер смотрит отклики на конкретную вакансию
    @Operation(summary = "Получить список откликов по id вакансии")
    @GetMapping("/vacancies/{vacancyId}")
    public ResponseEntity<List<ResumeReactionResponseDTO>> getResumeReactionsByVacancyId(@PathVariable Integer vacancyId) {
        return ResponseEntity.ok(resumeReactionService.getReactionsForVacancy(vacancyId));
    }

    // Кандидат смотрит историю своих откликов по ID своего резюме
    @Operation(summary = "Получить список откликов по id резюме")
    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<List<ResumeReactionResponseDTO>> getResumeReactionsByResumeId(@PathVariable Integer resumeId) {
        return ResponseEntity.ok(resumeReactionService.getMyReactions(resumeId));
    }
}
