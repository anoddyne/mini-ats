package ru.practice.mini_ats.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practice.mini_ats.dto.Vacancy.VacancyRequestDTO;
import ru.practice.mini_ats.dto.Vacancy.VacancyResponseDTO;
import ru.practice.mini_ats.models.enums.VacancyStatus;
import ru.practice.mini_ats.services.VacancyService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vacancies")
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping("/create")
    public ResponseEntity<VacancyResponseDTO> create(@Valid @RequestBody VacancyRequestDTO dto) {
        return new ResponseEntity<>(vacancyService.createVacancy(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VacancyResponseDTO>> getAll(@RequestParam(required = false, defaultValue = "OPEN") VacancyStatus status) {
        return new ResponseEntity<>(vacancyService.getByStatus(status), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacancyResponseDTO> getById(@PathVariable Integer id) {
        return new ResponseEntity<>(vacancyService.getVacancyById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VacancyResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody VacancyRequestDTO dto) {
        return new ResponseEntity<>(vacancyService.updateVacancy(id, dto), HttpStatus.OK);
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<Void> close(@PathVariable Integer id) {
        vacancyService.closeVacancy(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        vacancyService.deleteVacancy(id);
        return ResponseEntity.noContent().build();
    }
}
