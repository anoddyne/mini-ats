package ru.practice.mini_ats.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionRequestDTO;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionResponseDTO;
import ru.practice.mini_ats.services.ResumeReactionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reactions")
public class ResumeReactionController {
    private final ResumeReactionService resumeReactionService;

    // Кандидат откликается на вакансию
    @PostMapping("/apply")
    public ResponseEntity<ResumeReactionResponseDTO> apply(@Valid @RequestBody ResumeReactionRequestDTO dto) {
        return new ResponseEntity<>(resumeReactionService.applyToVacancy(dto), HttpStatus.CREATED);
    }

    // Рекрутер смотрит отклики на конкретную вакансию
    @GetMapping("/vacancy/{vacancyId}")
    public ResponseEntity<List<ResumeReactionResponseDTO>> getByVacancy(@PathVariable Integer vacancyId) {
        return ResponseEntity.ok(resumeReactionService.getReactionsForVacancy(vacancyId));
    }

    // Кандидат смотрит историю своих откликов по ID своего резюме
    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<List<ResumeReactionResponseDTO>> getByResume(@PathVariable Integer resumeId) {
        return ResponseEntity.ok(resumeReactionService.getMyReactions(resumeId));
    }
}
