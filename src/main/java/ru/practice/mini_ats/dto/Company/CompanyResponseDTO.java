package ru.practice.mini_ats.dto.Company;

public record CompanyResponseDTO(
        Integer companyId,
        String name,
        String description,
        String logoUrl) {
}
