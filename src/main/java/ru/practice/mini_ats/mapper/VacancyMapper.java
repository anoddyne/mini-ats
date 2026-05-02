package ru.practice.mini_ats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practice.mini_ats.dto.Vacancy.VacancyRequestDTO;
import ru.practice.mini_ats.dto.Vacancy.VacancyResponseDTO;
import ru.practice.mini_ats.models.Vacancy;

@Mapper(componentModel = "spring")
public interface VacancyMapper {

    @Mapping(source = "company.companyId", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    VacancyResponseDTO toResponseDto(Vacancy vacancy);

    @Mapping(target = "vacancyId", ignore = true)
    @Mapping(target = "company", ignore = true)
    Vacancy toEntity(VacancyRequestDTO dto);
}
