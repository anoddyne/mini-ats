package ru.practice.mini_ats.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.practice.mini_ats.dto.Vacancy.VacancyRequestDTO;
import ru.practice.mini_ats.dto.Vacancy.VacancyResponseDTO;
import ru.practice.mini_ats.models.enums.VacancyStatus;
import ru.practice.mini_ats.services.VacancyService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Tag(name = "Vacancy Controller",description = "Обрабатывает запросы, связанные с вакансиями")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vacancies")
public class VacancyController {

    private final VacancyService vacancyService;

    @Operation(summary = "Создать новую вакансию")
    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<VacancyResponseDTO> createVacancy(@Valid @RequestBody VacancyRequestDTO dto) throws AccessDeniedException {
        return new ResponseEntity<>(vacancyService.createVacancy(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Получить список вакансий")
    @GetMapping
    public ResponseEntity<Page<VacancyResponseDTO>> getListVacancies(@RequestParam(required = false, defaultValue = "OPEN") VacancyStatus status, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(vacancyService.getVacanciesByStatus(pageable, status));
    }

    @Operation(summary = "Получить информацию о вакансии по id")
    @GetMapping("/{id}")
    public ResponseEntity<VacancyResponseDTO> getVacancyById(@PathVariable Integer id) {
        return new ResponseEntity<>(vacancyService.getVacancyById(id), HttpStatus.OK);
    }

    @Operation(summary = "Обновить вакансию по id")
    @PutMapping("/{id}")
    public ResponseEntity<VacancyResponseDTO> updateVacancy(@PathVariable Integer id, @Valid @RequestBody VacancyRequestDTO dto) {
        return new ResponseEntity<>(vacancyService.updateVacancy(id, dto), HttpStatus.OK);
    }

    @Operation(summary = "Закрыть вакансию по id")
    @PatchMapping("/{id}/close")
    public ResponseEntity<Void> closeVacancy(@PathVariable Integer id) {
        vacancyService.closeVacancy(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить вакансию по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVacancyById(@PathVariable Integer id) {
        vacancyService.deleteVacancy(id);
        return ResponseEntity.noContent().build();
    }
}
