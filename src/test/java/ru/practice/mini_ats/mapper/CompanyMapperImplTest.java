package ru.practice.mini_ats.mapper;

import org.junit.jupiter.api.Test;
import ru.practice.mini_ats.dto.Company.CompanyRequestDTO;
import ru.practice.mini_ats.dto.Company.CompanyResponseDTO;
import ru.practice.mini_ats.models.Company;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyMapperImplTest {

    private final CompanyMapper mapper = new CompanyMapperImpl();

    @Test
    void toResponseDto_shouldMapAllFields() {
        Company company = new Company();
        company.setCompanyId(42);
        company.setName("Test Company");
        company.setDescription("Some description");
        company.setLogoUrl("https://example.com/logo.png");

        CompanyResponseDTO dto = mapper.toResponseDto(company);

        assertThat(dto.companyId()).isEqualTo(42);
        assertThat(dto.name()).isEqualTo("Test Company");
        assertThat(dto.description()).isEqualTo("Some description");
        assertThat(dto.logoUrl()).isEqualTo("https://example.com/logo.png");
    }

    @Test
    void toResponseDto_shouldHandleNullInput() {
        CompanyResponseDTO dto = mapper.toResponseDto(null);
        assertThat(dto).isNull();
    }

    @Test
    void toResponseDto_shouldHandleNullFields() {
        Company company = new Company();
        company.setCompanyId(10);
        company.setName("Only name");      // description and logoUrl are null

        CompanyResponseDTO dto = mapper.toResponseDto(company);

        assertThat(dto.companyId()).isEqualTo(10);
        assertThat(dto.name()).isEqualTo("Only name");
        assertThat(dto.description()).isNull();
        assertThat(dto.logoUrl()).isNull();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        CompanyRequestDTO request = new CompanyRequestDTO(
                "New Company",
                "Innovative startup",
                "https://startup.com/logo.jpg"
        );

        Company entity = mapper.toEntity(request);

        assertThat(entity.getCompanyId()).isNull(); // ID is generated, not set from DTO
        assertThat(entity.getName()).isEqualTo("New Company");
        assertThat(entity.getDescription()).isEqualTo("Innovative startup");
        assertThat(entity.getLogoUrl()).isEqualTo("https://startup.com/logo.jpg");
    }

    @Test
    void toEntity_shouldHandleNullInput() {
        Company entity = mapper.toEntity(null);
        assertThat(entity).isNull();
    }

    @Test
    void toEntity_shouldHandleNullOptionalFields() {
        CompanyRequestDTO request = new CompanyRequestDTO(
                "Minimal Company",
                null,
                null
        );

        Company entity = mapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("Minimal Company");
        assertThat(entity.getDescription()).isNull();
        assertThat(entity.getLogoUrl()).isNull();
    }
}