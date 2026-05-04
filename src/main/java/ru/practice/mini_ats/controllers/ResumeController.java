package ru.practice.mini_ats.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practice.mini_ats.dto.Resume.ResumeRequestDTO;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.services.ResumeService;

@RestController
@RequestMapping("/api/v1/resume")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;

    @PostMapping("/create")
    public ResponseEntity<ResumeResponseDTO> create(@Valid @RequestBody ResumeRequestDTO dto, Integer userId){
        return new ResponseEntity<>(resumeService.createResume(dto, userId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(resumeService.getByUserId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResumeResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody ResumeRequestDTO dto) {
        return ResponseEntity.ok(resumeService.updateResume(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        resumeService.deleteResume(id);
        return ResponseEntity.noContent().build();
    }

}
