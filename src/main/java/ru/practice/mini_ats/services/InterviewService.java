package ru.practice.mini_ats.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.mini_ats.dto.Interview.InterviewFeedbackDTO;
import ru.practice.mini_ats.dto.Interview.InterviewRequestDTO;
import ru.practice.mini_ats.dto.Interview.InterviewResponseDTO;
import ru.practice.mini_ats.mapper.InterviewMapper;
import ru.practice.mini_ats.models.Interview;
import ru.practice.mini_ats.models.ResumeReaction;
import ru.practice.mini_ats.models.enums.InterviewStatus;
import ru.practice.mini_ats.repositories.InterviewRepository;
import ru.practice.mini_ats.repositories.ResumeReactionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final ResumeReactionRepository resumeReactionRepository;
    private final InterviewMapper interviewMapper;

    @Transactional
    public InterviewResponseDTO scheduleInterview(InterviewRequestDTO dto) {
        ResumeReaction reaction = resumeReactionRepository.findById(dto.resumeReactionId())
                .orElseThrow(() -> new EntityNotFoundException("Отклик на вакансию не найден"));

        Interview interview = interviewMapper.toEntity(dto);

        interview.setStatus(InterviewStatus.SCHEDULED);
        interview.setResumeReaction(reaction);
        return interviewMapper.toResponseDto(interviewRepository.save(interview));
    }

    @Transactional
    public InterviewResponseDTO addFeedback(Integer interviewId, InterviewFeedbackDTO dto) {
        Interview interview = interviewRepository.findById(interviewId).orElseThrow(() -> new EntityNotFoundException("Интервью не найдено"));
        interviewMapper.updateEntityFromFeedback(dto, interview);
        return interviewMapper.toResponseDto(interviewRepository.save(interview));
    }

    @Transactional
    public List<InterviewResponseDTO> getInterviewsByCompany(Integer companyId) {
        return interviewRepository.findAllByResumeReactionVacancyCompanyCompanyId(companyId)
                .stream()
                .map(interviewMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public void deleteInterview(Integer interviewId) {
        if (!interviewRepository.existsById(interviewId)) {
            throw new EntityNotFoundException("Интервью не найдено");
        }
        interviewRepository.deleteById(interviewId);
    }

}
