package ru.practice.mini_ats.repositories;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practice.mini_ats.models.Company;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Company findByName(@NotBlank String name);
    void deleteByName(@NotBlank String name);

    @Query("SELECT c FROM Company c JOIN c.recruiters r WHERE r.login = :login")
    List<Company> findAllByRecruiterLogin(@Param("login") String login);
}
