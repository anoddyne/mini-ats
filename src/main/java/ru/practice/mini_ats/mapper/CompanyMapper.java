package ru.practice.mini_ats.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practice.mini_ats.dto.Company.CompanyRequestDTO;
import ru.practice.mini_ats.dto.Company.CompanyResponseDTO;
import ru.practice.mini_ats.models.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyResponseDTO toResponseDto(Company company);

    @Mapping(target = "companyId", ignore = true)
    Company toEntity(CompanyRequestDTO dto);
}
