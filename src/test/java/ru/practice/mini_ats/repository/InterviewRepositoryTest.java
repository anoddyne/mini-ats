package ru.practice.mini_ats.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practice.mini_ats.models.*;
import ru.practice.mini_ats.models.enums.EmploymentType;
import ru.practice.mini_ats.models.enums.InterviewStatus;
import ru.practice.mini_ats.models.enums.InterviewType;
import ru.practice.mini_ats.models.enums.UserRole;
import ru.practice.mini_ats.models.enums.VacancyStatus;
import ru.practice.mini_ats.repositories.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class InterviewRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgresSQLContainer =
            new PostgreSQLContainer<>("postgres:17.0");

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private ResumeReactionRepository resumeReactionRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private VacancyRepository vacancyRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    private ResumeReaction testResumeReaction;

    @Test
    void testThatConnectionEstablished() {
        assertThat(postgresSQLContainer.isCreated()).isTrue();
        assertThat(postgresSQLContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void beforeEach() {
        interviewRepository.deleteAll();
        resumeReactionRepository.deleteAll();
        resumeRepository.deleteAll();
        vacancyRepository.deleteAll();
        companyRepository.deleteAll();
        userRepository.deleteAll();

        // Создаём пользователя
        User user = new User();
        user.setName("Interview");
        user.setSurname("Tester");
        user.setEmail("interview.tester@example.com");
        user.setLogin("interviewer");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);
        user = userRepository.save(user);

        // Создаём резюме
        Resume resume = new Resume();
        resume.setSummary("Interview test resume");
        resume.setUser(user);
        resume = resumeRepository.save(resume);

        // Создаём компанию
        Company company = new Company();
        company.setName("Interview Company");
        company.setDescription("Company for interviews");
        company = companyRepository.save(company);

        // Создаём вакансию
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle("Interview Vacancy");
        vacancy.setEmploymentType(EmploymentType.REMOTE);
        vacancy.setStatus(VacancyStatus.DRAFT);
        vacancy.setCompany(company);
        vacancy = vacancyRepository.save(vacancy);

        // Создаём ResumeReaction (отклик на вакансию)
        testResumeReaction = new ResumeReaction();
        testResumeReaction.setResume(resume);
        testResumeReaction.setVacancy(vacancy);
        testResumeReaction.setCoverLetter("I would like to be interviewed");
        testResumeReaction = resumeReactionRepository.save(testResumeReaction);
    }

    @AfterEach
    void tearDown() {
        interviewRepository.deleteAll();
        resumeReactionRepository.deleteAll();
        resumeRepository.deleteAll();
        vacancyRepository.deleteAll();
        companyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void saveInterviewTest() {
        Interview interview = new Interview();
        interview.setDate(LocalDate.now());
        interview.setType(InterviewType.TECHNICAL);
        interview.setStatus(InterviewStatus.SCHEDULED);
        interview.setFeedback("Will be rescheduled");
        interview.setResumeReaction(testResumeReaction);

        Interview saved = interviewRepository.save(interview);
        Interview found = interviewRepository.findById(saved.getInterviewId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(saved);
    }

    @Test
    void findAllInterviewsTest() {
        Interview i1 = new Interview();
        i1.setResumeReaction(testResumeReaction);

        Interview i2 = new Interview();
        i2.setResumeReaction(testResumeReaction);

        interviewRepository.saveAll(List.of(i1, i2));
        List<Interview> interviews = interviewRepository.findAll();

        assertThat(interviews).hasSize(2);
        assertThat(interviews).containsExactlyInAnyOrder(i1, i2);
    }

    @Test
    void deleteInterviewByIdTest() {
        Interview interview = new Interview();
        interview.setResumeReaction(testResumeReaction);

        Interview saved = interviewRepository.save(interview);
        interviewRepository.deleteById(saved.getInterviewId());

        assertThat(interviewRepository.findById(saved.getInterviewId())).isEmpty();
    }

    @Test
    void deleteAllInterviewsTest() {
        Interview i1 = new Interview();
        i1.setResumeReaction(testResumeReaction);

        Interview i2 = new Interview();
        i2.setResumeReaction(testResumeReaction);

        interviewRepository.saveAll(List.of(i1, i2));
        interviewRepository.deleteAll();

        List<Interview> interviews = interviewRepository.findAll();
        assertThat(interviews).isEmpty();
    }

    @Test
    void updateInterviewTest() {
        Interview interview = new Interview();
        interview.setDate(LocalDate.of(2023, 1, 1));
        interview.setType(InterviewType.HR);
        interview.setStatus(InterviewStatus.SCHEDULED);
        interview.setFeedback("Initial feedback");
        interview.setResumeReaction(testResumeReaction);

        Interview saved = interviewRepository.save(interview);
        saved.setDate(LocalDate.of(2024, 1, 1));
        saved.setType(InterviewType.TECHNICAL);
        saved.setStatus(InterviewStatus.COMPLETED);
        saved.setFeedback("Updated feedback");
        interviewRepository.save(saved);

        Interview updated = interviewRepository.findById(saved.getInterviewId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(updated.getType()).isEqualTo(InterviewType.TECHNICAL);
        assertThat(updated.getStatus()).isEqualTo(InterviewStatus.COMPLETED);
        assertThat(updated.getFeedback()).isEqualTo("Updated feedback");
    }

    @Test
    void saveInterviewWithoutOptionalFieldsTest() {
        Interview interview = new Interview();
        interview.setResumeReaction(testResumeReaction);

        Interview saved = interviewRepository.save(interview);
        Interview found = interviewRepository.findById(saved.getInterviewId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getDate()).isNull();
        assertThat(found.getType()).isNull();
        assertThat(found.getStatus()).isNull();
        assertThat(found.getFeedback()).isNull();
    }

    @Test
    void findByResumeReactionIdTest() {
        Interview i1 = new Interview();
        i1.setResumeReaction(testResumeReaction);

        Interview i2 = new Interview();
        i2.setResumeReaction(testResumeReaction);

        interviewRepository.saveAll(List.of(i1, i2));
        List<Interview> interviews = interviewRepository.findByResumeReaction_ResumeReactionId(testResumeReaction.getResumeReactionId());

        assertThat(interviews).hasSize(2);
        assertThat(interviews).containsExactlyInAnyOrder(i1, i2);
    }

    @Test
    void findByStatusTest() {
        Interview scheduled = new Interview();
        scheduled.setStatus(InterviewStatus.SCHEDULED);
        scheduled.setResumeReaction(testResumeReaction);

        Interview completed = new Interview();
        completed.setStatus(InterviewStatus.COMPLETED);
        completed.setResumeReaction(testResumeReaction);

        interviewRepository.saveAll(List.of(scheduled, completed));

        List<Interview> scheduledInterviews = interviewRepository.findByStatus(InterviewStatus.SCHEDULED);
        assertThat(scheduledInterviews).hasSize(1);
        assertThat(scheduledInterviews.getFirst().getStatus()).isEqualTo(InterviewStatus.SCHEDULED);
    }
}