package ru.practice.mini_ats.mapper;

import org.junit.jupiter.api.Test;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionRequestDTO;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionResponseDTO;
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.ResumeReaction;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.Vacancy;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ResumeReactionMapperImplTest {

    private final ResumeReactionMapper mapper = new ResumeReactionMapperImpl();

    @Test
    void toResponseDto_shouldMapAllFields() {
        // given
        User user = new User();
        user.setUserId(7);
        user.setName("Иван");
        user.setSurname("Петров");
        user.setPatronymic("Сергеевич");

        Resume resume = new Resume();
        resume.setResumeId(20);
        resume.setUser(user);

        Vacancy vacancy = new Vacancy();
        vacancy.setVacancyId(15);
        vacancy.setTitle("Java разработчик");

        ResumeReaction reaction = new ResumeReaction();
        reaction.setResumeReactionId(100);
        reaction.setCoverLetter("Заинтересован в позиции");
        reaction.setAppliedAt(LocalDate.of(2026, 4, 26));
        reaction.setResume(resume);
        reaction.setVacancy(vacancy);

        // when
        ResumeReactionResponseDTO dto = mapper.toResponseDto(reaction);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.resumeReactionId()).isEqualTo(100);
        assertThat(dto.coverLetter()).isEqualTo("Заинтересован в позиции");
        assertThat(dto.appliedAt()).isEqualTo(LocalDate.of(2026, 4, 26));
        assertThat(dto.vacancyId()).isEqualTo(15);
        assertThat(dto.vacancyTitle()).isEqualTo("Java разработчик");
        assertThat(dto.resumeId()).isEqualTo(20);
        assertThat(dto.candidateFullName()).isEqualTo("Петров Иван Сергеевич");
    }

    @Test
    void toResponseDto_shouldHandleNullRelations() {
        // given
        ResumeReaction reaction = new ResumeReaction();
        reaction.setResumeReactionId(1);
        reaction.setCoverLetter("no relations");
        reaction.setResume(null);
        reaction.setVacancy(null);

        // when
        ResumeReactionResponseDTO dto = mapper.toResponseDto(reaction);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.vacancyId()).isNull();
        assertThat(dto.vacancyTitle()).isNull();
        assertThat(dto.resumeId()).isNull();
        assertThat(dto.candidateFullName()).isNull();
    }

    @Test
    void toResponseDto_shouldHandleNullResumeUser() {
        // given
        Resume resume = new Resume();
        resume.setResumeId(5);
        resume.setUser(null);  // пользователь не задан

        ResumeReaction reaction = new ResumeReaction();
        reaction.setResume(resume);

        // when
        ResumeReactionResponseDTO dto = mapper.toResponseDto(reaction);

        // then
        assertThat(dto.resumeId()).isEqualTo(5);
        assertThat(dto.candidateFullName()).isNull();
    }

    @Test
    void toResponseDto_shouldHandleNullInput() {
        ResumeReactionResponseDTO dto = mapper.toResponseDto(null);
        assertThat(dto).isNull();
    }

    @Test
    void toEntity_shouldMapCoverLetterOnly() {
        // given
        ResumeReactionRequestDTO request = new ResumeReactionRequestDTO(
                "Мое сопроводительное письмо",
                99,    // vacancyId – не используется
                100    // resumeId – не используется
        );

        // when
        ResumeReaction entity = mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getCoverLetter()).isEqualTo("Мое сопроводительное письмо");
        // vacancy и resume не устанавливаются (текущая реализация маппера не использует vacancyId/resumeId)
        assertThat(entity.getVacancy()).isNull();
        assertThat(entity.getResume()).isNull();
        assertThat(entity.getResumeReactionId()).isNull();
        assertThat(entity.getAppliedAt()).isNull();
    }

    @Test
    void toEntity_shouldHandleNullInput() {
        ResumeReaction entity = mapper.toEntity(null);
        assertThat(entity).isNull();
    }

    @Test
    void toEntity_shouldHandleNullCoverLetter() {
        ResumeReactionRequestDTO request = new ResumeReactionRequestDTO(null, 1, 2);

        ResumeReaction entity = mapper.toEntity(request);

        assertThat(entity.getCoverLetter()).isNull();
    }
}