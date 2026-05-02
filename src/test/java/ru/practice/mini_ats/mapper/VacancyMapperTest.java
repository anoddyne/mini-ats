package ru.practice.mini_ats.mapper;

import org.junit.jupiter.api.Test;
import ru.practice.mini_ats.dto.Vacancy.VacancyRequestDTO;
import ru.practice.mini_ats.dto.Vacancy.VacancyResponseDTO;
import ru.practice.mini_ats.models.Company;
import ru.practice.mini_ats.models.Vacancy;
import ru.practice.mini_ats.models.enums.EmploymentType;
import ru.practice.mini_ats.models.enums.VacancyStatus;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class VacancyMapperImplTest {

    private final VacancyMapper mapper = new VacancyMapperImpl();

    @Test
    void toResponseDto_shouldMapAllFields() {
        // given
        Company company = new Company();
        company.setCompanyId(10);
        company.setName("Test Company");

        Vacancy vacancy = new Vacancy();
        vacancy.setVacancyId(101);
        vacancy.setTitle("Java Developer");
        vacancy.setDescription("Develop microservices");
        vacancy.setSalaryFrom(200_000);
        vacancy.setSalaryTo(300_000);
        vacancy.setLocation("Moscow");
        vacancy.setEmploymentType(EmploymentType.HYBRID);
        vacancy.setStatus(VacancyStatus.DRAFT);
        vacancy.setRequiredSkills(Map.of("Java", 5, "Spring", 4));
        vacancy.setExperienceLevel(3);
        vacancy.setCompany(company);

        // when
        VacancyResponseDTO dto = mapper.toResponseDto(vacancy);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.vacancyId()).isEqualTo(101);
        assertThat(dto.title()).isEqualTo("Java Developer");
        assertThat(dto.description()).isEqualTo("Develop microservices");
        assertThat(dto.salaryFrom()).isEqualTo(200_000);
        assertThat(dto.salaryTo()).isEqualTo(300_000);
        assertThat(dto.location()).isEqualTo("Moscow");
        assertThat(dto.employmentType()).isEqualTo(EmploymentType.HYBRID);
        assertThat(dto.status()).isEqualTo(VacancyStatus.DRAFT);
        assertThat(dto.requiredSkills()).containsExactlyInAnyOrderEntriesOf(Map.of("Java", 5, "Spring", 4));
        assertThat(dto.experienceLevel()).isEqualTo(3);
        assertThat(dto.companyId()).isEqualTo(10);
        assertThat(dto.companyName()).isEqualTo("Test Company");
    }

    @Test
    void toResponseDto_shouldHandleNullCompany() {
        // given
        Vacancy vacancy = new Vacancy();
        vacancy.setVacancyId(1);
        vacancy.setTitle("No Company");
        vacancy.setCompany(null);

        // when
        VacancyResponseDTO dto = mapper.toResponseDto(vacancy);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.companyId()).isNull();
        assertThat(dto.companyName()).isNull();
    }

    @Test
    void toResponseDto_shouldHandleNullRequiredSkills() {
        // given
        Vacancy vacancy = new Vacancy();
        vacancy.setRequiredSkills(null);

        // when
        VacancyResponseDTO dto = mapper.toResponseDto(vacancy);

        // then
        assertThat(dto.requiredSkills()).isNull();
    }

    @Test
    void toResponseDto_shouldCopyRequiredSkillsMap() {
        // given
        Map<String, Object> original = new LinkedHashMap<>();
        original.put("skill", "value");
        Vacancy vacancy = new Vacancy();
        vacancy.setRequiredSkills(original);

        // when
        VacancyResponseDTO dto = mapper.toResponseDto(vacancy);
        Map<String, Object> copied = dto.requiredSkills();

        // then
        assertThat(copied).isNotSameAs(original); // должна быть копия (новый экземпляр)
        assertThat(copied).isEqualTo(original);
    }

    @Test
    void toResponseDto_shouldHandleNullInput() {
        VacancyResponseDTO dto = mapper.toResponseDto(null);
        assertThat(dto).isNull();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // given
        VacancyRequestDTO request = new VacancyRequestDTO(
                "Senior QA",
                "Automation testing",
                180_000,
                220_000,
                "Saint Petersburg",
                EmploymentType.REMOTE,
                VacancyStatus.DRAFT,
                Map.of("Selenium", 4, "Java", 3),
                4,
                5  // companyId – не используется в маппере
        );

        // when
        Vacancy entity = mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getTitle()).isEqualTo("Senior QA");
        assertThat(entity.getDescription()).isEqualTo("Automation testing");
        assertThat(entity.getSalaryFrom()).isEqualTo(180_000);
        assertThat(entity.getSalaryTo()).isEqualTo(220_000);
        assertThat(entity.getLocation()).isEqualTo("Saint Petersburg");
        assertThat(entity.getEmploymentType()).isEqualTo(EmploymentType.REMOTE);
        assertThat(entity.getStatus()).isEqualTo(VacancyStatus.DRAFT);
        assertThat(entity.getRequiredSkills()).containsExactlyInAnyOrderEntriesOf(Map.of("Selenium", 4, "Java", 3));
        assertThat(entity.getExperienceLevel()).isEqualTo(4);
        // companyId не маппится, company остается null (это нормально)
        assertThat(entity.getCompany()).isNull();
        // vacancyId не устанавливается (id генерируется БД)
        assertThat(entity.getVacancyId()).isNull();
    }

    @Test
    void toEntity_shouldHandleNullInput() {
        Vacancy entity = mapper.toEntity(null);
        assertThat(entity).isNull();
    }

    @Test
    void toEntity_shouldHandleNullOptionalFields() {
        // given
        VacancyRequestDTO minimal = new VacancyRequestDTO(
                "Minimal Title",
                null,
                null,
                null,
                null,
                EmploymentType.HYBRID,
                null,
                null,
                null,
                100  // companyId
        );

        // when
        Vacancy entity = mapper.toEntity(minimal);

        // then
        assertThat(entity.getTitle()).isEqualTo("Minimal Title");
        assertThat(entity.getDescription()).isNull();
        assertThat(entity.getSalaryFrom()).isNull();
        assertThat(entity.getSalaryTo()).isNull();
        assertThat(entity.getLocation()).isNull();
        assertThat(entity.getEmploymentType()).isEqualTo(EmploymentType.HYBRID);
        assertThat(entity.getStatus()).isNull();
        assertThat(entity.getRequiredSkills()).isNull();
        assertThat(entity.getExperienceLevel()).isNull();
        assertThat(entity.getCompany()).isNull();
    }

    @Test
    void toEntity_shouldCopyRequiredSkillsMap() {
        // given
        Map<String, Object> original = Map.of("key", "value");
        VacancyRequestDTO request = new VacancyRequestDTO(
                "Title", null, null, null, null,
                EmploymentType.HYBRID, VacancyStatus.DRAFT,
                original, null, 1
        );

        // when
        Vacancy entity = mapper.toEntity(request);
        Map<String, Object> copied = entity.getRequiredSkills();

        // then
        assertThat(copied).isNotSameAs(original); // должна быть копия
        assertThat(copied).isEqualTo(original);
    }
}