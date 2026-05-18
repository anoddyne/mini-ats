package ru.practice.mini_ats.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.mini_ats.models.Interview;
import ru.practice.mini_ats.models.enums.InterviewStatus;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Integer> {
    List<Interview> findAllByResumeReactionVacancyCompanyCompanyId(Integer companyId);
    List<Interview> findByResumeReaction_ResumeReactionId(Integer resumeReactionId);

    List<Interview> findByStatus(InterviewStatus interviewStatus);
}
