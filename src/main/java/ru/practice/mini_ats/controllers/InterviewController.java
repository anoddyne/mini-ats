package ru.practice.mini_ats.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practice.mini_ats.dto.Interview.InterviewFeedbackDTO;
import ru.practice.mini_ats.dto.Interview.InterviewRequestDTO;
import ru.practice.mini_ats.dto.Interview.InterviewResponseDTO;
import ru.practice.mini_ats.services.InterviewService;

import java.util.List;
@Slf4j
@Tag(name = "Interview Controller",description = "Обрабатывает запросы, связанные с интервью")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/interviews")
public class InterviewController {
    private final InterviewService interviewService;

    @Operation(summary = "Запланировать интервью")
    @PostMapping("/schedule")
    public ResponseEntity<InterviewResponseDTO> scheduleInterview(@Valid @RequestBody InterviewRequestDTO dto) {
        return new ResponseEntity<>(interviewService.scheduleInterview(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Добавить отзыв по интервью")
    @PatchMapping("/{id}/feedback")
    public ResponseEntity<InterviewResponseDTO> addFeedbackToInterview(@PathVariable Integer id, @Valid @RequestBody InterviewFeedbackDTO dto) {
        return ResponseEntity.ok(interviewService.addFeedback(id, dto));
    }

    @Operation(summary = "Получить список интервью по id компании")
    @GetMapping("/companies/{companyId}")
    public ResponseEntity<List<InterviewResponseDTO>> getListInterviewsByCompanyId(@PathVariable Integer companyId){
        return ResponseEntity.ok(interviewService.getInterviewsByCompany(companyId));
    }

    @Operation(summary = "Удалить интервью по id компании")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterview(@PathVariable Integer id) {
        interviewService.deleteInterview(id);
        return ResponseEntity.noContent().build();
    }
}
