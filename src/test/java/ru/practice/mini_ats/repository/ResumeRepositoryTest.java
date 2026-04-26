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
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.enums.UserRole;
import ru.practice.mini_ats.repositories.ResumeRepository;
import ru.practice.mini_ats.repositories.UserRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ResumeRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgresSQLContainer =
            new PostgreSQLContainer<>("postgres:17.0");

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @Test
    void testThatConnectionEstablished() {
        assertThat(postgresSQLContainer.isCreated()).isTrue();
        assertThat(postgresSQLContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void beforeEach() {
        resumeRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setName("Resume");
        testUser.setSurname("Tester");
        testUser.setEmail("resume.test@example.com");
        testUser.setLogin("resumetest");
        testUser.setPassword("password");
        testUser.setRole(UserRole.CANDIDATE);
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        resumeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void saveResumeTest() {
        Resume resume = new Resume();
        resume.setSummary("Experienced Java developer with Spring");
        resume.setEducation("Master's in Computer Science");
        resume.setDesiredSalary(250000);
        resume.setResumeFileUrl("https://example.com/resume.pdf");
        resume.setSkills(Map.of("Java", 5, "Spring", 4));
        resume.setExperience(Map.of("years", 5, "lastPosition", "Tech Lead"));
        resume.setUser(testUser);

        Resume saved = resumeRepository.save(resume);
        Resume found = resumeRepository.findById(saved.getResumeId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(saved);
    }

    @Test
    void findAllResumesTest() {
        Resume r1 = new Resume();
        r1.setSummary("First resume");
        r1.setUser(testUser);

        Resume r2 = new Resume();
        r2.setSummary("Second resume");
        r2.setUser(testUser);

        resumeRepository.saveAll(List.of(r1, r2));
        List<Resume> resumes = resumeRepository.findAll();

        assertThat(resumes).hasSize(2);
        assertThat(resumes).containsExactlyInAnyOrder(r1, r2);
    }

    @Test
    void deleteResumeByIdTest() {
        Resume resume = new Resume();
        resume.setSummary("To be deleted");
        resume.setUser(testUser);

        Resume saved = resumeRepository.save(resume);
        resumeRepository.deleteById(saved.getResumeId());

        assertThat(resumeRepository.findById(saved.getResumeId())).isEmpty();
    }

    @Test
    void deleteAllResumesTest() {
        Resume r1 = new Resume();
        r1.setSummary("Temp1");
        r1.setUser(testUser);

        Resume r2 = new Resume();
        r2.setSummary("Temp2");
        r2.setUser(testUser);

        resumeRepository.saveAll(List.of(r1, r2));
        resumeRepository.deleteAll();

        List<Resume> resumes = resumeRepository.findAll();
        assertThat(resumes).isEmpty();
    }

    @Test
    void updateResumeTest() {
        Resume resume = new Resume();
        resume.setSummary("Old summary");
        resume.setDesiredSalary(100000);
        resume.setUser(testUser);

        Resume saved = resumeRepository.save(resume);
        saved.setSummary("New summary");
        saved.setDesiredSalary(150000);
        resumeRepository.save(saved);

        Resume updated = resumeRepository.findById(saved.getResumeId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getSummary()).isEqualTo("New summary");
        assertThat(updated.getDesiredSalary()).isEqualTo(150000);
    }

    @Test
    void saveResumeWithoutOptionalFieldsTest() {
        Resume resume = new Resume();
        resume.setSummary("Minimal resume");
        resume.setUser(testUser);

        Resume saved = resumeRepository.save(resume);
        Resume found = resumeRepository.findById(saved.getResumeId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getSummary()).isEqualTo("Minimal resume");
        assertThat(found.getEducation()).isNull();
        assertThat(found.getDesiredSalary()).isNull();
        assertThat(found.getResumeFileUrl()).isNull();
        assertThat(found.getSkills()).isNull();
        assertThat(found.getExperience()).isNull();
    }

    @Test
    void findByUserIdTest() {
        Resume r1 = new Resume();
        r1.setSummary("First resume for user");
        r1.setUser(testUser);

        Resume r2 = new Resume();
        r2.setSummary("Second resume for user");
        r2.setUser(testUser);

        // Создаём другого пользователя для проверки, что его резюме не попадают в выборку
        User otherUser = new User();
        otherUser.setName("Other");
        otherUser.setSurname("User");
        otherUser.setEmail("other@example.com");
        otherUser.setLogin("other");
        otherUser.setPassword("pass");
        otherUser.setRole(UserRole.CANDIDATE);
        otherUser = userRepository.save(otherUser);

        Resume r3 = new Resume();
        r3.setSummary("Other user's resume");
        r3.setUser(otherUser);

        resumeRepository.saveAll(List.of(r1, r2, r3));

        List<Resume> resumesForTestUser = resumeRepository.findByUser_UserId(testUser.getUserId());

        assertThat(resumesForTestUser).hasSize(2);
        assertThat(resumesForTestUser).containsExactlyInAnyOrder(r1, r2);
    }
}