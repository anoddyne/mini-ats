package ru.practice.mini_ats.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.enums.UserRole;
import ru.practice.mini_ats.repositories.ResumeRepository;
import ru.practice.mini_ats.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
public class ResumeRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17.0");

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
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
        resume.setResumeFileUrl("https://minio.local/resume.pdf");
        resume.setFileName("resume.pdf");
        resume.setUser(testUser);

        Resume saved = resumeRepository.save(resume);
        assertThat(saved.getResumeId()).isNotNull();

        Optional<Resume> found = resumeRepository.findById(saved.getResumeId());
        assertThat(found).isPresent();
        assertThat(found.get().getFileName()).isEqualTo("resume.pdf");
    }

    @Test
    void findByIdTest() {
        Resume resume = new Resume();
        resume.setResumeFileUrl("url");
        resume.setFileName("file.pdf");
        resume.setUser(testUser);
        Resume saved = resumeRepository.save(resume);

        Optional<Resume> found = resumeRepository.findById(saved.getResumeId());
        assertThat(found).isPresent();
        assertThat(found.get().getResumeId()).isEqualTo(saved.getResumeId());
    }

    @Test
    void findAllTest() {
        Resume r1 = new Resume();
        r1.setResumeFileUrl("url1");
        r1.setFileName("r1.pdf");
        r1.setUser(testUser);

        Resume r2 = new Resume();
        r2.setResumeFileUrl("url2");
        r2.setFileName("r2.pdf");
        r2.setUser(testUser);

        resumeRepository.saveAll(List.of(r1, r2));
        List<Resume> resumes = resumeRepository.findAll();

        assertThat(resumes).hasSize(2);
        assertThat(resumes).extracting(Resume::getFileName).containsExactlyInAnyOrder("r1.pdf", "r2.pdf");
    }

    @Test
    void deleteByIdTest() {
        Resume resume = new Resume();
        resume.setResumeFileUrl("toDelete");
        resume.setFileName("delete.pdf");
        resume.setUser(testUser);
        Resume saved = resumeRepository.save(resume);

        resumeRepository.deleteById(saved.getResumeId());
        Optional<Resume> found = resumeRepository.findById(saved.getResumeId());
        assertThat(found).isEmpty();
    }

    @Test
    void deleteAllTest() {
        Resume r1 = new Resume();
        r1.setResumeFileUrl("temp1");
        r1.setFileName("t1.pdf");
        r1.setUser(testUser);

        Resume r2 = new Resume();
        r2.setResumeFileUrl("temp2");
        r2.setFileName("t2.pdf");
        r2.setUser(testUser);

        resumeRepository.saveAll(List.of(r1, r2));
        resumeRepository.deleteAll();

        assertThat(resumeRepository.findAll()).isEmpty();
    }

    @Test
    void updateResumeTest() {
        Resume resume = new Resume();
        resume.setResumeFileUrl("oldUrl");
        resume.setFileName("old.pdf");
        resume.setUser(testUser);
        Resume saved = resumeRepository.save(resume);

        saved.setResumeFileUrl("newUrl");
        saved.setFileName("new.pdf");
        resumeRepository.save(saved);

        Optional<Resume> updated = resumeRepository.findById(saved.getResumeId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getResumeFileUrl()).isEqualTo("newUrl");
        assertThat(updated.get().getFileName()).isEqualTo("new.pdf");
    }

    @Test
    void saveResumeWithOnlyRequiredFieldsTest() {
        Resume resume = new Resume();
        resume.setUser(testUser); // только ссылка на пользователя

        Resume saved = resumeRepository.save(resume);
        Optional<Resume> found = resumeRepository.findById(saved.getResumeId());

        assertThat(found).isPresent();
        assertThat(found.get().getResumeFileUrl()).isNull();
        assertThat(found.get().getFileName()).isNull();
        assertThat(found.get().getUser()).isEqualTo(testUser);
    }

    // ========== Кастомный метод findByUserUserId ==========

    @Test
    void findByUserUserIdReturnsResumeWhenExists() {
        Resume resume = new Resume();
        resume.setResumeFileUrl("url");
        resume.setFileName("user.pdf");
        resume.setUser(testUser);
        resumeRepository.save(resume);

        Optional<Resume> found = resumeRepository.findByUserUserId(testUser.getUserId());
        assertThat(found).isPresent();
        assertThat(found.get().getFileName()).isEqualTo("user.pdf");
    }

    @Test
    void findByUserUserIdReturnsEmptyWhenNotFound() {
        Optional<Resume> found = resumeRepository.findByUserUserId(testUser.getUserId());
        assertThat(found).isEmpty();
    }

    @Test
    void findByUserUserIdReturnsLatestForUser() {

        Resume r = new Resume();
        r.setResumeFileUrl("latest");
        r.setFileName("latest.pdf");
        r.setUser(testUser);
        resumeRepository.save(r);

        Optional<Resume> found = resumeRepository.findByUserUserId(testUser.getUserId());
        assertThat(found).isPresent();
        assertThat(found.get().getFileName()).isEqualTo("latest.pdf");
    }
}