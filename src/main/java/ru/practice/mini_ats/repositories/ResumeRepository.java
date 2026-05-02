package ru.practice.mini_ats.repositories;

import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.mini_ats.models.Resume;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    boolean existsByUserUserId(Integer userId);

    Optional<Resume> findByUserUserId(Integer userId);
}
