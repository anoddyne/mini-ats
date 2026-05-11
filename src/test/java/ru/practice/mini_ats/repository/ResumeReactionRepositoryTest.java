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
import ru.practice.mini_ats.models.enums.UserRole;
import ru.practice.mini_ats.models.enums.VacancyStatus;
import ru.practice.mini_ats.repositories.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ResumeReactionRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgresSQLContainer =
            new PostgreSQLContainer<>("postgres:17.0");

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

    private Resume testResume;
    private Vacancy testVacancy;

    @Test
    void testThatConnectionEstablished() {
        assertThat(postgresSQLContainer.isCreated()).isTrue();
        assertThat(postgresSQLContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void beforeEach() {
        resumeReactionRepository.deleteAll();
        resumeRepository.deleteAll();
        vacancyRepository.deleteAll();
        companyRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setName("ResumeUser");
        user.setSurname("Test");
        user.setEmail("resumeuser@example.com");
        user.setLogin("resumeuser");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);
        user = userRepository.save(user);

        // Создаём резюме
        testResume = new Resume();
        testResume.setUser(user);
        testResume = resumeRepository.save(testResume);

        Company company = new Company();
        company.setName("Test Company");
        company.setDescription("Company for vacancy");
        company = companyRepository.save(company);

        testVacancy = new Vacancy();
        testVacancy.setTitle("Test Vacancy");
        testVacancy.setEmploymentType(EmploymentType.REMOTE);
        testVacancy.setStatus(VacancyStatus.DRAFT);
        testVacancy.setCompany(company);
        testVacancy = vacancyRepository.save(testVacancy);
    }

    @AfterEach
    void tearDown() {
        resumeReactionRepository.deleteAll();
        resumeRepository.deleteAll();
        vacancyRepository.deleteAll();
        companyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void saveResumeReactionTest() {
        ResumeReaction reaction = new ResumeReaction();
        reaction.setCoverLetter("I am interested in this position");
        reaction.setAppliedAt(LocalDate.now());
        reaction.setResume(testResume);
        reaction.setVacancy(testVacancy);

        ResumeReaction saved = resumeReactionRepository.save(reaction);
        ResumeReaction found = resumeReactionRepository.findById(saved.getResumeReactionId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(saved);
    }

    @Test
    void findAllResumeReactionsTest() {
        ResumeReaction r1 = new ResumeReaction();
        r1.setResume(testResume);
        r1.setVacancy(testVacancy);

        ResumeReaction r2 = new ResumeReaction();
        r2.setResume(testResume);
        r2.setVacancy(testVacancy);

        resumeReactionRepository.saveAll(List.of(r1, r2));
        List<ResumeReaction> reactions = resumeReactionRepository.findAll();

        assertThat(reactions).hasSize(2);
        assertThat(reactions).containsExactlyInAnyOrder(r1, r2);
    }

    @Test
    void deleteResumeReactionByIdTest() {
        ResumeReaction reaction = new ResumeReaction();
        reaction.setResume(testResume);
        reaction.setVacancy(testVacancy);

        ResumeReaction saved = resumeReactionRepository.save(reaction);
        resumeReactionRepository.deleteById(saved.getResumeReactionId());

        assertThat(resumeReactionRepository.findById(saved.getResumeReactionId())).isEmpty();
    }

    @Test
    void deleteAllResumeReactionsTest() {
        ResumeReaction r1 = new ResumeReaction();
        r1.setResume(testResume);
        r1.setVacancy(testVacancy);

        ResumeReaction r2 = new ResumeReaction();
        r2.setResume(testResume);
        r2.setVacancy(testVacancy);

        resumeReactionRepository.saveAll(List.of(r1, r2));
        resumeReactionRepository.deleteAll();

        List<ResumeReaction> reactions = resumeReactionRepository.findAll();
        assertThat(reactions).isEmpty();
    }

    @Test
    void updateResumeReactionTest() {
        ResumeReaction reaction = new ResumeReaction();
        reaction.setCoverLetter("Old letter");
        reaction.setAppliedAt(LocalDate.of(2023, 1, 1));
        reaction.setResume(testResume);
        reaction.setVacancy(testVacancy);

        ResumeReaction saved = resumeReactionRepository.save(reaction);
        saved.setCoverLetter("New letter");
        saved.setAppliedAt(LocalDate.of(2024, 1, 1));
        resumeReactionRepository.save(saved);

        ResumeReaction updated = resumeReactionRepository.findById(saved.getResumeReactionId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getCoverLetter()).isEqualTo("New letter");
        assertThat(updated.getAppliedAt()).isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    void saveResumeReactionWithoutOptionalFieldsTest() {
        ResumeReaction reaction = new ResumeReaction();
        reaction.setResume(testResume);
        reaction.setVacancy(testVacancy);

        ResumeReaction saved = resumeReactionRepository.save(reaction);
        ResumeReaction found = resumeReactionRepository.findById(saved.getResumeReactionId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getCoverLetter()).isNull();
        assertThat(found.getAppliedAt()).isNull();
    }

    @Test
    void findByResumeIdTest() {
        ResumeReaction r1 = new ResumeReaction();
        r1.setResume(testResume);
        r1.setVacancy(testVacancy);

        ResumeReaction r2 = new ResumeReaction();
        r2.setResume(testResume);
        r2.setVacancy(testVacancy);

        resumeReactionRepository.saveAll(List.of(r1, r2));
        List<ResumeReaction> reactions = resumeReactionRepository.findByResume_ResumeId(testResume.getResumeId());

        assertThat(reactions).hasSize(2);
        assertThat(reactions).containsExactlyInAnyOrder(r1, r2);
    }

    @Test
    void findByVacancyIdTest() {
        ResumeReaction r1 = new ResumeReaction();
        r1.setResume(testResume);
        r1.setVacancy(testVacancy);

        ResumeReaction r2 = new ResumeReaction();
        r2.setResume(testResume);
        r2.setVacancy(testVacancy);

        resumeReactionRepository.saveAll(List.of(r1, r2));
        List<ResumeReaction> reactions = resumeReactionRepository.findByVacancy_VacancyId(testVacancy.getVacancyId());

        assertThat(reactions).hasSize(2);
        assertThat(reactions).containsExactlyInAnyOrder(r1, r2);
    }
}