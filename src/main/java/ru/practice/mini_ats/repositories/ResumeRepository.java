package ru.practice.mini_ats.repositories;

import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.User;

import java.util.Optional;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Integer> {
    Optional<Resume> findByUserUserId(Integer userId);
    Optional<Resume> findByUser_Login(String login);
}
