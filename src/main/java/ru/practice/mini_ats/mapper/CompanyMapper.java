package ru.practice.mini_ats.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practice.mini_ats.dto.Company.CompanyRequestDTO;
import ru.practice.mini_ats.dto.Company.CompanyResponseDTO;
import ru.practice.mini_ats.models.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    // Автоматически маппит logoUrl и fileName, если поля совпадают
    CompanyResponseDTO toResponseDto(Company company);

    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)      // эти поля не маппятся из DTO
    @Mapping(target = "fileName", ignore = true)
    Company toEntity(CompanyRequestDTO dto);

    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    void updateEntityFromDto(CompanyRequestDTO dto, @MappingTarget Company company);
}
