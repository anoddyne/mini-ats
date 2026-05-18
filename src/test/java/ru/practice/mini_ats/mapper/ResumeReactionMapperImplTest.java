package ru.practice.mini_ats.mapper;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionRequestDTO;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionResponseDTO;
import ru.practice.mini_ats.models.Company;
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.ResumeReaction;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.Vacancy;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ResumeReactionMapperImplTest {

    private final ResumeReactionMapper mapper = Mappers.getMapper(ResumeReactionMapper.class);

    @Test
    void toResponseDto_ShouldMapAllFields() {
        // Подготовка данных
        ResumeReaction reaction = getResumeReaction();

        // Действие
        ResumeReactionResponseDTO dto = mapper.toResponseDto(reaction);

        // Проверки
        assertThat(dto.resumeReactionId()).isEqualTo(1);
        assertThat(dto.coverLetter()).isEqualTo("Заинтересован в вакансии");
        assertThat(dto.appliedAt()).isEqualTo(LocalDate.of(2026, 5, 12));
        assertThat(dto.vacancyId()).isEqualTo(100);
        assertThat(dto.vacancyTitle()).isEqualTo("Java Developer");
        assertThat(dto.companyName()).isEqualTo("ООО Ромашка");
        assertThat(dto.resumeId()).isEqualTo(200);
        assertThat(dto.candidateFullName()).isEqualTo("Иванов Иван Иванович");
    }

    private static @NonNull ResumeReaction getResumeReaction() {
        User user = new User();
        user.setUserId(1);
        user.setName("Иван");
        user.setSurname("Иванов");
        user.setPatronymic("Иванович");

        Company company = new Company();
        company.setCompanyId(10);
        company.setName("ООО Ромашка");

        Vacancy vacancy = new Vacancy();
        vacancy.setVacancyId(100);
        vacancy.setTitle("Java Developer");
        vacancy.setCompany(company);

        Resume resume = new Resume();
        resume.setResumeId(200);
        resume.setUser(user);

        ResumeReaction reaction = new ResumeReaction();
        reaction.setResumeReactionId(1);
        reaction.setCoverLetter("Заинтересован в вакансии");
        reaction.setAppliedAt(LocalDate.of(2026, 5, 12));
        reaction.setVacancy(vacancy);
        reaction.setResume(resume);
        return reaction;
    }

    @Test
    void toEntity_ShouldMapRequestDtoToEntity_IgnoringSpecificFields() {
        // Данные
        ResumeReactionRequestDTO dto = new ResumeReactionRequestDTO(
                "Хочу работать у вас",
                42
        );

        // Действие
        ResumeReaction reaction = mapper.toEntity(dto);

        // Проверки
        assertThat(reaction.getCoverLetter()).isEqualTo("Хочу работать у вас");
        assertThat(reaction.getVacancy()).isNull(); // игнорируется
        assertThat(reaction.getResume()).isNull();  // игнорируется
        assertThat(reaction.getResumeReactionId()).isNull(); // игнорируется
        assertThat(reaction.getAppliedAt()).isNull();        // игнорируется
    }

    @Test
    void toEntity_WhenRequestDtoHasNullCoverLetter_SetsNull() {
        ResumeReactionRequestDTO dto = new ResumeReactionRequestDTO(null, 55);
        ResumeReaction reaction = mapper.toEntity(dto);
        assertThat(reaction.getCoverLetter()).isNull();
        assertThat(reaction.getVacancy()).isNull();
    }
}