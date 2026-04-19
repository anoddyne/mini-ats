package ru.practice.mini_ats.dto.Vacancy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practice.mini_ats.models.Company;
import ru.practice.mini_ats.models.enums.EmploymentType;
import ru.practice.mini_ats.models.enums.VacancyStatus;

import java.util.Map;

public record VacancyRequestDTO(
        @NotBlank(message = "Заголовок вакансии обязателен")
        String title,

        String description,

        @PositiveOrZero(message = "Зарплата не может быть отрицательной")
        Integer salaryFrom,

        @PositiveOrZero(message = "Зарплата не может быть отрицательной")
        Integer salaryTo,

        String location,

        @NotNull(message = "Укажите тип занятости")
        EmploymentType employmentType,

        VacancyStatus status,

        Map<String, Object> requiredSkills,

        Integer experienceLevel,

        @NotNull(message = "Укажите компанию")
        Integer companyId
) {
}
