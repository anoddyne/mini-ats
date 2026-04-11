package ru.practice.mini_ats.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.mini_ats.models.ResumeReaction;

@Repository
public interface ResumeReactionRepository extends JpaRepository<ResumeReaction, Integer> {
}
