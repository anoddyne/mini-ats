package ru.practice.mini_ats.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.practice.mini_ats.dto.Company.CompanyRequestDTO;
import ru.practice.mini_ats.dto.Company.CompanyResponseDTO;
import ru.practice.mini_ats.services.CompanyService;

import java.util.List;

@Tag(name = "Company Controller", description = "Обрабатывает запросы, связанные с компаниями")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "Создать новую компанию с логотипом")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompanyResponseDTO> createCompany(
            @RequestPart("logo") MultipartFile file,
            @RequestPart("dto") CompanyRequestDTO dto) {
        return new ResponseEntity<>(companyService.createCompany(dto, file), HttpStatus.CREATED);
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
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompanyResponseDTO> updateCompany(
            @RequestPart(name = "logo",required = false) MultipartFile file,
            @RequestPart(name = "dto",required = false) CompanyRequestDTO dto,
            @PathVariable Integer id) {
        return new ResponseEntity<>(companyService.updateCompany(dto, file,id), HttpStatus.OK);
    }

    @Operation(summary = "Удалить компанию по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Integer id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить компании, в которых я являюсь рекрутером")
    @GetMapping("/my")
    public ResponseEntity<List<CompanyResponseDTO>> getMyCompanies() {
        return ResponseEntity.ok(companyService.getMyCompanies());
    }
}
