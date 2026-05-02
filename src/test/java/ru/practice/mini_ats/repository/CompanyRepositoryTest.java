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
import ru.practice.mini_ats.repositories.CompanyRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CompanyRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgresSQLContainer =
            new PostgreSQLContainer<>("postgres:17.0");

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void testThatConnectionEstablished() {
        assertThat(postgresSQLContainer.isCreated()).isTrue();
        assertThat(postgresSQLContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void beforeEach() {
        companyRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        companyRepository.deleteAll();
    }

    @Test
    void saveCompanyTest() {
        Company company = new Company();
        company.setName("Tech Corp");
        company.setDescription("Software development company");
        company.setLogoUrl("https://example.com/logo.png");

        Company savedCompany = companyRepository.save(company);
        Company foundCompany = companyRepository.findById(savedCompany.getCompanyId()).orElse(null);

        assertThat(foundCompany).isNotNull();
        assertThat(foundCompany).isEqualTo(savedCompany);
    }

    @Test
    void findAllCompaniesTest() {
        Company company1 = new Company();
        company1.setName("Company A");
        company1.setDescription("Description A");

        Company company2 = new Company();
        company2.setName("Company B");
        company2.setDescription("Description B");

        companyRepository.saveAll(List.of(company1, company2));
        List<Company> companiesFromRepo = companyRepository.findAll();

        assertTrue(companiesFromRepo.size() == 2 &&
                companiesFromRepo.contains(company1) &&
                companiesFromRepo.contains(company2));
    }

    @Test
    void deleteCompanyByIdTest() {
        Company company = new Company();
        company.setName("ToDelete");
        company.setDescription("Will be deleted");

        Company saved = companyRepository.save(company);
        companyRepository.deleteById(saved.getCompanyId());

        assertThat(companyRepository.findById(saved.getCompanyId()).isEmpty()).isTrue();
    }

    @Test
    void deleteAllCompaniesTest() {
        Company company1 = new Company();
        company1.setName("Temp1");
        Company company2 = new Company();
        company2.setName("Temp2");

        companyRepository.saveAll(List.of(company1, company2));
        companyRepository.deleteAll();

        List<Company> companies = companyRepository.findAll();
        assertThat(companies.isEmpty()).isTrue();
    }

    @Test
    void findCompanyByName() {
        Company company = new Company();
        company.setName("UniqueName");
        company.setDescription("Some description");

        companyRepository.save(company);
        Company found = companyRepository.findByName(company.getName());

        assertThat(found).isEqualTo(company);
    }

    @Test
    void deleteCompanyByName() {
        Company company = new Company();
        company.setName("DeleteByName");
        company.setDescription("To be deleted by name");

        companyRepository.save(company);
        companyRepository.deleteByName(company.getName());

        assertThat(companyRepository.findByName(company.getName())).isNull();
    }

    @Test
    void updateCompanyTest() {
        Company company = new Company();
        company.setName("OldName");
        company.setDescription("Old description");

        Company saved = companyRepository.save(company);
        saved.setName("NewName");
        saved.setDescription("New description");
        companyRepository.save(saved);

        Company updated = companyRepository.findById(saved.getCompanyId()).orElse(null);
        assertThat(updated).isNotNull();
        assert updated != null;
        assertThat(updated.getName()).isEqualTo("NewName");
        assertThat(updated.getDescription()).isEqualTo("New description");
    }

    @Test
    void saveCompanyWithoutOptionalFieldsTest() {
        Company company = new Company();
        company.setName("Minimal Company");

        Company saved = companyRepository.save(company);
        Company found = companyRepository.findById(saved.getCompanyId()).orElse(null);

        assertThat(found).isNotNull();
        assert found != null;
        assertThat(found.getName()).isEqualTo("Minimal Company");
        assertThat(found.getDescription()).isNull();
        assertThat(found.getLogoUrl()).isNull();
    }
}