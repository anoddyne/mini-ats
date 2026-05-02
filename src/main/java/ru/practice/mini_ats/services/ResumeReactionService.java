package ru.practice.mini_ats.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionRequestDTO;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionResponseDTO;
import ru.practice.mini_ats.mapper.ResumeReactionMapper;
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.ResumeReaction;
import ru.practice.mini_ats.models.Vacancy;
import ru.practice.mini_ats.models.enums.VacancyStatus;
import ru.practice.mini_ats.repositories.ResumeReactionRepository;
import ru.practice.mini_ats.repositories.ResumeRepository;
import ru.practice.mini_ats.repositories.VacancyRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeReactionService {
    private final VacancyRepository vacancyRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeReactionMapper resumeReactionMapper;
    private final ResumeReactionRepository resumeReactionRepository;

    @Transactional
    public ResumeReactionResponseDTO applyToVacancy(ResumeReactionRequestDTO dto) {
        Vacancy vacancy = vacancyRepository.findById(dto.vacancyId()).orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена"));

        if (vacancy.getStatus() != VacancyStatus.OPEN) {
            throw new IllegalStateException("Нельзя откликнуться на закрытую вакансию");
        }

        Resume resume = resumeRepository.findById(dto.resumeId()).orElseThrow(() -> new EntityNotFoundException("Резюме не найдено"));

        if (resumeReactionRepository.existsByVacancyVacancyIdAndResumeResumeId(dto.vacancyId(), dto.resumeId())) {
            throw new RuntimeException("Вы уже откликались на эту вакансию");
        }

        ResumeReaction reaction = resumeReactionMapper.toEntity(dto);
        reaction.setVacancy(vacancy);
        reaction.setResume(resume);
        reaction.setAppliedAt(LocalDate.now());

        return resumeReactionMapper.toResponseDto(resumeReactionRepository.save(reaction));
    }

    @Transactional(readOnly = true)
    public List<ResumeReactionResponseDTO> getReactionsForVacancy(Integer vacancyId) {
        // Метод для рекрутера, чтобы посмотреть все отклики на его вакансию
        return resumeReactionRepository.findByVacancyVacancyId(vacancyId)
                .stream()
                .map(resumeReactionMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResumeReactionResponseDTO> getMyReactions(Integer resumeId) {
        // Метод для кандидата, чтобы посмотреть, куда он откликался
        return resumeReactionRepository.findAllByResumeResumeId(resumeId)
                .stream()
                .map(resumeReactionMapper::toResponseDto)
                .toList();
    }
}
