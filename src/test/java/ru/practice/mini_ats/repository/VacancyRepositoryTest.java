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
import ru.practice.mini_ats.models.Company;
import ru.practice.mini_ats.models.Vacancy;
import ru.practice.mini_ats.models.enums.EmploymentType;
import ru.practice.mini_ats.models.enums.VacancyStatus;
import ru.practice.mini_ats.repositories.CompanyRepository;
import ru.practice.mini_ats.repositories.VacancyRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VacancyRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgresSQLContainer =
            new PostgreSQLContainer<>("postgres:17.0");

    @Autowired
    private VacancyRepository vacancyRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company testCompany;

    @Test
    void testThatConnectionEstablished() {
        assertThat(postgresSQLContainer.isCreated()).isTrue();
        assertThat(postgresSQLContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void beforeEach() {
        vacancyRepository.deleteAll();
        companyRepository.deleteAll();

        testCompany = new Company();
        testCompany.setName("Test Company");
        testCompany.setDescription("Company for vacancy tests");
        testCompany.setLogoUrl("https://example.com/logo.png");
        testCompany = companyRepository.save(testCompany);
    }

    @AfterEach
    void tearDown() {
        vacancyRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    void saveVacancyTest() {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle("Java Developer");
        vacancy.setDescription("Develop microservices");
        vacancy.setSalaryFrom(200000);
        vacancy.setSalaryTo(300000);
        vacancy.setLocation("Moscow");
        vacancy.setEmploymentType(EmploymentType.REMOTE);
        vacancy.setStatus(VacancyStatus.DRAFT);

        vacancy.setCompany(testCompany);

        Vacancy saved = vacancyRepository.save(vacancy);
        Vacancy found = vacancyRepository.findById(saved.getVacancyId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(saved);
    }

    @Test
    void findAllVacanciesTest() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("DevOps");
        v1.setEmploymentType(EmploymentType.REMOTE);
        v1.setStatus(VacancyStatus.DRAFT);
        v1.setCompany(testCompany);

        Vacancy v2 = new Vacancy();
        v2.setTitle("QA Engineer");
        v2.setEmploymentType(EmploymentType.REMOTE);
        v2.setStatus(VacancyStatus.DRAFT);
        v2.setCompany(testCompany);

        vacancyRepository.saveAll(List.of(v1, v2));
        List<Vacancy> vacancies = vacancyRepository.findAll();

        assertThat(vacancies).hasSize(2);
        assertThat(vacancies).containsExactlyInAnyOrder(v1, v2);
    }

    @Test
    void deleteVacancyByIdTest() {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle("ToDelete");
        vacancy.setEmploymentType(EmploymentType.REMOTE);
        vacancy.setStatus(VacancyStatus.DRAFT);
        vacancy.setCompany(testCompany);

        Vacancy saved = vacancyRepository.save(vacancy);
        vacancyRepository.deleteById(saved.getVacancyId());

        assertThat(vacancyRepository.findById(saved.getVacancyId())).isEmpty();
    }

    @Test
    void deleteAllVacanciesTest() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("Temp1");
        v1.setEmploymentType(EmploymentType.REMOTE);
        v1.setStatus(VacancyStatus.DRAFT);
        v1.setCompany(testCompany);

        Vacancy v2 = new Vacancy();
        v2.setTitle("Temp2");
        v2.setEmploymentType(EmploymentType.REMOTE);
        v2.setStatus(VacancyStatus.DRAFT);
        v2.setCompany(testCompany);

        vacancyRepository.saveAll(List.of(v1, v2));
        vacancyRepository.deleteAll();

        List<Vacancy> vacancies = vacancyRepository.findAll();
        assertThat(vacancies).isEmpty();
    }

    @Test
    void updateVacancyTest() {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle("Old Title");
        vacancy.setEmploymentType(EmploymentType.REMOTE);
        vacancy.setStatus(VacancyStatus.DRAFT);
        vacancy.setCompany(testCompany);

        Vacancy saved = vacancyRepository.save(vacancy);
        saved.setTitle("New Title");
        saved.setSalaryFrom(250000);
        saved.setSalaryTo(350000);
        saved.setLocation("Saint Petersburg");
        vacancyRepository.save(saved);

        Vacancy updated = vacancyRepository.findById(saved.getVacancyId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getTitle()).isEqualTo("New Title");
        assertThat(updated.getSalaryFrom()).isEqualTo(250000);
        assertThat(updated.getSalaryTo()).isEqualTo(350000);
        assertThat(updated.getLocation()).isEqualTo("Saint Petersburg");
    }

    @Test
    void saveVacancyWithoutOptionalFieldsTest() {
        Vacancy vacancy = new Vacancy();
        vacancy.setTitle("Minimal Vacancy");
        vacancy.setEmploymentType(EmploymentType.REMOTE);
        vacancy.setStatus(VacancyStatus.DRAFT);
        vacancy.setCompany(testCompany);

        Vacancy saved = vacancyRepository.save(vacancy);
        Vacancy found = vacancyRepository.findById(saved.getVacancyId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Minimal Vacancy");
        assertThat(found.getDescription()).isNull();
        assertThat(found.getSalaryFrom()).isNull();
        assertThat(found.getSalaryTo()).isNull();
        assertThat(found.getLocation()).isNull();
        assertThat(found.getEmploymentType()).isEqualTo(EmploymentType.REMOTE);
        assertThat(found.getRequiredSkills()).isNull();
        assertThat(found.getExperienceLevel()).isNull();
    }

    @Test
    void findByCompanyIdTest() {
        Vacancy v1 = new Vacancy();
        v1.setTitle("V1");
        v1.setEmploymentType(EmploymentType.REMOTE);
        v1.setStatus(VacancyStatus.DRAFT);
        v1.setCompany(testCompany);

        Vacancy v2 = new Vacancy();
        v2.setTitle("V2");
        v2.setEmploymentType(EmploymentType.REMOTE);
        v2.setStatus(VacancyStatus.DRAFT);
        v2.setCompany(testCompany);

        vacancyRepository.saveAll(List.of(v1, v2));
        List<Vacancy> vacancies = vacancyRepository.findByCompany_CompanyId(testCompany.getCompanyId());

        assertThat(vacancies).hasSize(2);
        assertThat(vacancies).containsExactlyInAnyOrder(v1, v2);
    }

    @Test
    void findByStatusTest() {
        Vacancy active = new Vacancy();
        active.setTitle("Active vacancy");
        active.setEmploymentType(EmploymentType.REMOTE);
        active.setStatus(VacancyStatus.DRAFT);
        active.setCompany(testCompany);

        Vacancy closed = new Vacancy();
        closed.setTitle("Closed vacancy");
        closed.setEmploymentType(EmploymentType.REMOTE);
        closed.setStatus(VacancyStatus.CLOSED);
        closed.setCompany(testCompany);

        Vacancy draft = new Vacancy();
        draft.setTitle("Draft vacancy");
        draft.setEmploymentType(EmploymentType.REMOTE);
        draft.setStatus(VacancyStatus.CLOSED);
        draft.setCompany(testCompany);

        vacancyRepository.saveAll(List.of(active, closed, draft));

        List<Vacancy> activeVacancies = vacancyRepository.findByStatus(VacancyStatus.DRAFT);
        assertThat(activeVacancies).hasSize(1);
        assertThat(activeVacancies.getFirst().getTitle()).isEqualTo("Active vacancy");
    }
}