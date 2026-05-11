package ru.practice.mini_ats.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.mini_ats.dto.Resume.ResumeRequestDTO;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.mapper.ResumeMapper;
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.repositories.ResumeRepository;
import ru.practice.mini_ats.repositories.UserRepository;
import ru.practice.mini_ats.security.SecurityUtils;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ResumeMapper resumeMapper;

    @Transactional
    public ResumeResponseDTO createResume(ResumeRequestDTO dto) {
        String login = SecurityUtils.getCurrentUserLogin();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        if (resumeRepository.existsByUserUserId(user.getUserId())) {
            throw new RuntimeException("У пользователя уже есть резюме");
        }
        Resume resume = resumeMapper.toEntity(dto);
        resume.setUser(user);
        return resumeMapper.toResponseDto(resumeRepository.save(resume));
    }

    @Transactional(readOnly = true)
    public ResumeResponseDTO getByUserId(Integer userId) {
        Resume resume = resumeRepository.findByUserUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Резюме для указанного пользователя не найдено"));
        return resumeMapper.toResponseDto(resume);
    }

    @Transactional
    public ResumeResponseDTO updateResume(Integer resumeId, ResumeRequestDTO dto){
        Resume existingResume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Резюме не найдено"));

        resumeMapper.updateEntityFromDto(dto, existingResume);
        return resumeMapper.toResponseDto(resumeRepository.save(existingResume));
    }

    @Transactional
    public void deleteResume(Integer resumeId){
        if (!resumeRepository.existsById(resumeId)) {
            throw new EntityNotFoundException("Не удалось удалить: резюме с id" + resumeId + " не существует");
        }
        resumeRepository.deleteById(resumeId);
    }

}
