package ru.practice.mini_ats.dto.Vacancy;

import ru.practice.mini_ats.models.enums.EmploymentType;
import ru.practice.mini_ats.models.enums.ExperienceLevel;
import ru.practice.mini_ats.models.enums.VacancyStatus;


public record VacancyResponseDTO(
        Integer vacancyId,
        String title,
        String description,
        Integer salaryFrom,
        Integer salaryTo,
        String location,
        EmploymentType employmentType,
        VacancyStatus status,
        String requiredSkills,
        ExperienceLevel experienceLevel,
        Integer companyId,
        String companyName,
        int applicationsCount
) {
}
