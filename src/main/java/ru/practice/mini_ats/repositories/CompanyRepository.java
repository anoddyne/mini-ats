package ru.practice.mini_ats.repositories;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.mini_ats.models.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Company findByName(@NotBlank String name);

    void deleteByName(@NotBlank String name);
}
