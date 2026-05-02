package ru.practice.mini_ats.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practice.mini_ats.dto.Company.CompanyRequestDTO;
import ru.practice.mini_ats.dto.Company.CompanyResponseDTO;
import ru.practice.mini_ats.models.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyResponseDTO toResponseDto(Company company);

    @Mapping(target = "companyId", ignore = true)
    Company toEntity(CompanyRequestDTO dto);

    @Mapping(target = "companyId", ignore = true)
    void updateEntityFromDto(CompanyRequestDTO dto, @MappingTarget Company company);
}
