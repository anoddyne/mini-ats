package ru.practice.mini_ats.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionRequestDTO;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionResponseDTO;
import ru.practice.mini_ats.mapper.ResumeReactionMapper;
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.ResumeReaction;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.Vacancy;
import ru.practice.mini_ats.models.enums.VacancyStatus;
import ru.practice.mini_ats.repositories.ResumeReactionRepository;
import ru.practice.mini_ats.repositories.ResumeRepository;
import ru.practice.mini_ats.repositories.UserRepository;
import ru.practice.mini_ats.repositories.VacancyRepository;
import ru.practice.mini_ats.security.SecurityUtils;

import java.time.LocalDate;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeReactionService {
    private final VacancyRepository vacancyRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeReactionMapper resumeReactionMapper;
    private final ResumeReactionRepository resumeReactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResumeReactionResponseDTO applyToVacancy(ResumeReactionRequestDTO dto) {
        String login = SecurityUtils.getCurrentUserLogin();
        User user = userRepository.findByLogin(login).orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Resume resume = resumeRepository.findByUserUserId(user.getUserId()).orElseThrow(() -> new RuntimeException("У вас нет резюме. Создайте его перед откликом."));

        Vacancy vacancy = vacancyRepository.findById(dto.vacancyId()).orElseThrow(() -> new EntityNotFoundException("Вакансия не найдена"));

        if (vacancy.getStatus() != VacancyStatus.OPEN) {
            throw new IllegalStateException("Нельзя откликнуться на закрытую вакансию");
        }

        if (resumeReactionRepository.existsByVacancyVacancyIdAndResumeResumeId(dto.vacancyId(), resume.getResumeId())) {
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
        return resumeReactionRepository.findByVacancy_VacancyId(vacancyId)
                .stream()
                .map(resumeReactionMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResumeReactionResponseDTO> getMyReactions() {
        // Метод для кандидата, чтобы посмотреть, куда он откликался
        String login = SecurityUtils.getCurrentUserLogin();
        Resume resume = resumeRepository.findByUser_Login(login).orElseThrow(() -> new EntityNotFoundException("Резюме не найдено"));

        return resumeReactionRepository.findByResume_ResumeId(resume.getResumeId())
                .stream()
                .map(resumeReactionMapper::toResponseDto)
                .toList();
    }
}
