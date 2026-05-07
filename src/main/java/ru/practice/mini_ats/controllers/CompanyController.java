package ru.practice.mini_ats.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practice.mini_ats.dto.Company.CompanyRequestDTO;
import ru.practice.mini_ats.dto.Company.CompanyResponseDTO;
import ru.practice.mini_ats.services.CompanyService;

import java.util.List;
@Tag(name = "Company Controller",description = "Обрабатывает запросы, связанные с компаниями")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "Создать новую компанию")
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(@Valid @RequestBody CompanyRequestDTO dto) {
        return new ResponseEntity<>(companyService.createCompany(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Получить список компаний")
    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getListCompanies() {
        return new ResponseEntity<>(companyService.getAllCompanies(), HttpStatus.OK);
    }

    @Operation(summary = "Получить компанию по id")
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(@PathVariable Integer id) {
        return new ResponseEntity<>(companyService.getCompanyById(id), HttpStatus.OK);
    }

    @Operation(summary = "Обновить информацию о компании по id")
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(@PathVariable Integer id, @Valid @RequestBody CompanyRequestDTO dto) {
        return new ResponseEntity<>(companyService.updateCompany(id, dto), HttpStatus.OK);
    }

    @Operation(summary = "Удалить компанию по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Integer id){
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
