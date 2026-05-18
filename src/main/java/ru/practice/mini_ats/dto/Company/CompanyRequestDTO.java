package ru.practice.mini_ats.dto.Company;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record CompanyRequestDTO(
        @NotBlank(message = "Необходимо указать название компании")
        @Size(min = 2, message = "Название компании должно быть от 2 символов")
        String name,

        @Size(max = 3000, message = "Описание компании слишком длинное (максимум 3000 символов)")
        String description
) {}
