package ru.practice.mini_ats.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.mini_ats.dto.Vacancy.VacancyResponseDTO;
import ru.practice.mini_ats.models.Vacancy;
import ru.practice.mini_ats.models.enums.VacancyStatus;

import java.util.List;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Integer> {
    List<Vacancy> findAllByStatus(VacancyStatus vacancyStatus);
    Vacancy findByTitle(String title);

    void deleteByTitle(String title);

    List<Vacancy> findByCompany_CompanyId(Integer companyId);
    List<Vacancy> findByStatus(VacancyStatus status);
}
