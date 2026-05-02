package ru.practice.mini_ats.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.mini_ats.models.ResumeReaction;

import java.util.List;

@Repository
public interface ResumeReactionRepository extends JpaRepository<ResumeReaction, Integer> {
    List<ResumeReaction> findByResume_ResumeId(Integer resumeId);

    List<ResumeReaction> findByVacancy_VacancyId(Integer vacancyId);
}
