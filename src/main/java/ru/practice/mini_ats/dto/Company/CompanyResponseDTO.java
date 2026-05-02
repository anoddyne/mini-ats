package ru.practice.mini_ats.dto.Company;

import jakarta.validation.constraints.NotBlank;

public record CompanyResponseDTO(
        Integer companyId,
        String name,
        String description,
        String logoUrl) {
}
