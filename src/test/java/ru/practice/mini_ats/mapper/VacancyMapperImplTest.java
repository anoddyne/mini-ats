package ru.practice.mini_ats.mapper;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practice.mini_ats.dto.Vacancy.VacancyRequestDTO;
import ru.practice.mini_ats.dto.Vacancy.VacancyResponseDTO;
import ru.practice.mini_ats.models.Company;
import ru.practice.mini_ats.models.Vacancy;
import ru.practice.mini_ats.models.enums.EmploymentType;
import ru.practice.mini_ats.models.enums.ExperienceLevel;
import ru.practice.mini_ats.models.enums.VacancyStatus;

import static org.assertj.core.api.Assertions.assertThat;

class VacancyMapperImplTest {

    private final VacancyMapper mapper = Mappers.getMapper(VacancyMapper.class);

    @Test
    void toResponseDto_ShouldMapAllFields() {
        Vacancy vacancy = getVacancy();

        VacancyResponseDTO dto = mapper.toResponseDto(vacancy);

        assertThat(dto.vacancyId()).isEqualTo(100);
        assertThat(dto.title()).isEqualTo("Java Developer");
        assertThat(dto.description()).isEqualTo("Develop cool apps");
        assertThat(dto.salaryFrom()).isEqualTo(50000);
        assertThat(dto.salaryTo()).isEqualTo(70000);
        assertThat(dto.location()).isEqualTo("Moscow");
        assertThat(dto.employmentType()).isEqualTo(EmploymentType.HYBRID);
        assertThat(dto.status()).isEqualTo(VacancyStatus.CLOSED);
        assertThat(dto.requiredSkills()).isEqualTo("Java, Spring");
        assertThat(dto.experienceLevel()).isEqualTo(ExperienceLevel.JUNIOR);
        assertThat(dto.companyId()).isEqualTo(1);
        assertThat(dto.companyName()).isEqualTo("Tech Corp");
    }

    private static @NonNull Vacancy getVacancy() {
        Company company = new Company();
        company.setCompanyId(1);
        company.setName("Tech Corp");

        Vacancy vacancy = new Vacancy();
        vacancy.setVacancyId(100);
        vacancy.setTitle("Java Developer");
        vacancy.setDescription("Develop cool apps");
        vacancy.setSalaryFrom(50000);
        vacancy.setSalaryTo(70000);
        vacancy.setLocation("Moscow");
        vacancy.setEmploymentType(EmploymentType.HYBRID);
        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancy.setRequiredSkills("Java, Spring");
        vacancy.setExperienceLevel(ExperienceLevel.JUNIOR);
        vacancy.setCompany(company);
        return vacancy;
    }

    @Test
    void toEntity_ShouldMapRequestDtoToVacancy_IgnoringVacancyIdAndCompany() {
        VacancyRequestDTO dto = new VacancyRequestDTO(
                "Python Developer",
                "Write Python code",
                60000,
                80000,
                "Saint Petersburg",
                EmploymentType.REMOTE,
                VacancyStatus.DRAFT,
                "Python, Django",
                ExperienceLevel.MIDDLE,
                5  // companyId
        );

        Vacancy vacancy = mapper.toEntity(dto);

        assertThat(vacancy.getTitle()).isEqualTo("Python Developer");
        assertThat(vacancy.getDescription()).isEqualTo("Write Python code");
        assertThat(vacancy.getSalaryFrom()).isEqualTo(60000);
        assertThat(vacancy.getSalaryTo()).isEqualTo(80000);
        assertThat(vacancy.getLocation()).isEqualTo("Saint Petersburg");
        assertThat(vacancy.getEmploymentType()).isEqualTo(EmploymentType.REMOTE);
        assertThat(vacancy.getStatus()).isEqualTo(VacancyStatus.DRAFT);
        assertThat(vacancy.getRequiredSkills()).isEqualTo("Python, Django");
        assertThat(vacancy.getExperienceLevel()).isEqualTo(ExperienceLevel.MIDDLE);

        assertThat(vacancy.getVacancyId()).isNull();
        assertThat(vacancy.getCompany()).isNull();
    }

    @Test
    void updateEntityFromDto_ShouldUpdateExistingVacancy_IgnoringVacancyIdAndCompany() {
        Company existingCompany = new Company();
        existingCompany.setCompanyId(99);
        existingCompany.setName("Old Corp");

        Vacancy existing = getVacancy(existingCompany);

        VacancyRequestDTO updateDto = new VacancyRequestDTO(
                "New Title",
                "New desc",
                50000,
                60000,
                "New City",
                EmploymentType.HYBRID,
                VacancyStatus.CLOSED,
                "New skills",
                ExperienceLevel.SENIOR,
                77
        );

        mapper.updateEntityFromDto(updateDto, existing);

        assertThat(existing.getTitle()).isEqualTo("New Title");
        assertThat(existing.getDescription()).isEqualTo("New desc");
        assertThat(existing.getSalaryFrom()).isEqualTo(50000);
        assertThat(existing.getSalaryTo()).isEqualTo(60000);
        assertThat(existing.getLocation()).isEqualTo("New City");
        assertThat(existing.getEmploymentType()).isEqualTo(EmploymentType.HYBRID);
        assertThat(existing.getStatus()).isEqualTo(VacancyStatus.CLOSED);
        assertThat(existing.getRequiredSkills()).isEqualTo("New skills");
        assertThat(existing.getExperienceLevel()).isEqualTo(ExperienceLevel.SENIOR);

        // Игнорируемые поля
        assertThat(existing.getVacancyId()).isEqualTo(200); // не изменился
        assertThat(existing.getCompany()).isEqualTo(existingCompany); // не изменился
    }

    private static @NonNull Vacancy getVacancy(Company existingCompany) {
        Vacancy existing = new Vacancy();
        existing.setVacancyId(200);
        existing.setTitle("Old Title");
        existing.setDescription("Old desc");
        existing.setSalaryFrom(1000);
        existing.setSalaryTo(2000);
        existing.setLocation("Old City");
        existing.setEmploymentType(EmploymentType.REMOTE);
        existing.setStatus(VacancyStatus.CLOSED);
        existing.setRequiredSkills("Old skills");
        existing.setExperienceLevel(ExperienceLevel.JUNIOR);
        existing.setCompany(existingCompany);
        return existing;
    }
}