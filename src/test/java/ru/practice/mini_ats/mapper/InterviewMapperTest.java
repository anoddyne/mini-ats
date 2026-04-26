package ru.practice.mini_ats.mapper;

import org.junit.jupiter.api.Test;
import ru.practice.mini_ats.dto.Interview.InterviewFeedbackDTO;
import ru.practice.mini_ats.dto.Interview.InterviewRequestDTO;
import ru.practice.mini_ats.dto.Interview.InterviewResponseDTO;
import ru.practice.mini_ats.models.*;
import ru.practice.mini_ats.models.enums.InterviewStatus;
import ru.practice.mini_ats.models.enums.InterviewType;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewMapperImplTest {

    private final InterviewMapper mapper = new InterviewMapperImpl();

    @Test
    void toResponseDto_shouldMapAllFields() {
        // given
        User user = new User();
        user.setUserId(10);
        user.setName("Анна");
        user.setSurname("Смирнова");
        user.setPatronymic("Ивановна");

        Resume resume = new Resume();
        resume.setResumeId(22);
        resume.setUser(user);

        Company company = new Company();
        company.setCompanyId(5);
        company.setName("ООО Рога и Копыта");

        Vacancy vacancy = new Vacancy();
        vacancy.setVacancyId(37);
        vacancy.setTitle("Старший Java-разработчик");
        vacancy.setCompany(company);

        ResumeReaction reaction = new ResumeReaction();
        reaction.setResumeReactionId(101);
        reaction.setResume(resume);
        reaction.setVacancy(vacancy);

        Interview interview = new Interview();
        interview.setInterviewId(202);
        interview.setDate(LocalDate.of(2026, 5, 10));
        interview.setType(InterviewType.TECHNICAL);
        interview.setStatus(InterviewStatus.SCHEDULED);
        interview.setFeedback("Предварительное согласие получено");
        interview.setResumeReaction(reaction);

        // when
        InterviewResponseDTO dto = mapper.toResponseDto(interview);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.interviewId()).isEqualTo(202);
        assertThat(dto.date()).isEqualTo(LocalDate.of(2026, 5, 10));
        assertThat(dto.type()).isEqualTo(InterviewType.TECHNICAL);
        assertThat(dto.status()).isEqualTo(InterviewStatus.SCHEDULED);
        assertThat(dto.feedback()).isEqualTo("Предварительное согласие получено");
        assertThat(dto.resumeReactionId()).isEqualTo(101);
        assertThat(dto.vacancyTitle()).isEqualTo("Старший Java-разработчик");
        assertThat(dto.candidateFullName()).isEqualTo("Смирнова Анна Ивановна");
        assertThat(dto.companyName()).isEqualTo("ООО Рога и Копыта");
    }

    @Test
    void toResponseDto_shouldHandleNullRelations() {
        // given
        Interview interview = new Interview();
        interview.setInterviewId(1);
        interview.setResumeReaction(null);  // нет отклика

        // when
        InterviewResponseDTO dto = mapper.toResponseDto(interview);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.resumeReactionId()).isNull();
        assertThat(dto.vacancyTitle()).isNull();
        assertThat(dto.candidateFullName()).isNull();
        assertThat(dto.companyName()).isNull();
    }

    @Test
    void toResponseDto_shouldHandlePartialNullChain() {
        // given
        ResumeReaction reaction = new ResumeReaction();
        reaction.setResumeReactionId(50);
        reaction.setVacancy(null);    // вакансия отсутствует
        reaction.setResume(null);     // резюме отсутствует

        Interview interview = new Interview();
        interview.setResumeReaction(reaction);

        // when
        InterviewResponseDTO dto = mapper.toResponseDto(interview);

        // then
        assertThat(dto.resumeReactionId()).isEqualTo(50);
        assertThat(dto.vacancyTitle()).isNull();
        assertThat(dto.candidateFullName()).isNull();
        assertThat(dto.companyName()).isNull();
    }

    @Test
    void toResponseDto_shouldHandleNullInput() {
        InterviewResponseDTO dto = mapper.toResponseDto(null);
        assertThat(dto).isNull();
    }

    @Test
    void toEntity_shouldMapDateAndType() {
        // given
        InterviewRequestDTO request = new InterviewRequestDTO(
                LocalDate.of(2026, 6, 15),
                InterviewType.HR,
                99  // resumeReactionId – игнорируется маппером (не используется в текущей реализации)
        );

        // when
        Interview entity = mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getDate()).isEqualTo(LocalDate.of(2026, 6, 15));
        assertThat(entity.getType()).isEqualTo(InterviewType.HR);
        // Связь с ResumeReaction не устанавливается маппером (это ответственность сервиса)
        assertThat(entity.getResumeReaction()).isNull();
        assertThat(entity.getStatus()).isNull();
        assertThat(entity.getFeedback()).isNull();
    }

    @Test
    void toEntity_shouldHandleNullInput() {
        Interview entity = mapper.toEntity(null);
        assertThat(entity).isNull();
    }

    @Test
    void updateEntityFromFeedback_shouldUpdateStatusAndFeedback() {
        // given
        Interview existing = new Interview();
        existing.setStatus(InterviewStatus.SCHEDULED);
        existing.setFeedback("Старый отзыв");

        InterviewFeedbackDTO feedbackDto = new InterviewFeedbackDTO(
                "Кандидат отлично подходит",
                InterviewStatus.COMPLETED
        );

        // when
        mapper.updateEntityFromFeedback(feedbackDto, existing);

        // then
        assertThat(existing.getStatus()).isEqualTo(InterviewStatus.COMPLETED);
        assertThat(existing.getFeedback()).isEqualTo("Кандидат отлично подходит");
    }

    @Test
    void updateEntityFromFeedback_shouldHandleNullDto() {
        // given
        Interview existing = new Interview();
        existing.setStatus(InterviewStatus.SCHEDULED);
        existing.setFeedback("Не должно измениться");

        // when
        mapper.updateEntityFromFeedback(null, existing);

        // then
        assertThat(existing.getStatus()).isEqualTo(InterviewStatus.SCHEDULED);
        assertThat(existing.getFeedback()).isEqualTo("Не должно измениться");
    }
}