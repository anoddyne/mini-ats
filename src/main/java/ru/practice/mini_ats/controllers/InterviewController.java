package ru.practice.mini_ats.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practice.mini_ats.dto.Interview.InterviewFeedbackDTO;
import ru.practice.mini_ats.dto.Interview.InterviewRequestDTO;
import ru.practice.mini_ats.dto.Interview.InterviewResponseDTO;
import ru.practice.mini_ats.services.InterviewService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/interviews")
public class InterviewController {
    private final InterviewService interviewService;

    @PostMapping("/schedule")
    public ResponseEntity<InterviewResponseDTO> schedule(@Valid @RequestBody InterviewRequestDTO dto) {
        return new ResponseEntity<>(interviewService.scheduleInterview(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/feedback")
    public ResponseEntity<InterviewResponseDTO> addFeedback(@PathVariable Integer id, @Valid @RequestBody InterviewFeedbackDTO dto) {
        return ResponseEntity.ok(interviewService.addFeedback(id, dto));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<InterviewResponseDTO>> getByCompany(@PathVariable Integer companyId){
        return ResponseEntity.ok(interviewService.getInterviewsByCompany(companyId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        interviewService.deleteInterview(id);
        return ResponseEntity.noContent().build();
    }
}
