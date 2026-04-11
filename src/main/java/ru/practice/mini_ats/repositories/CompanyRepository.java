package ru.practice.mini_ats.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.mini_ats.models.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
}
