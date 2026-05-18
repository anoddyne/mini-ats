package ru.practice.mini_ats.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.mapper.ResumeMapper;
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.repositories.ResumeRepository;
import ru.practice.mini_ats.repositories.UserRepository;
import ru.practice.mini_ats.security.SecurityUtils;

import java.io.InputStream;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ResumeMapper resumeMapper;
    private final FileService fileService;

    @Transactional
    public ResumeResponseDTO createResume(MultipartFile file) {
        String login = SecurityUtils.getCurrentUserLogin();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));


        Optional<Resume> resumeOptional = resumeRepository.findByUserUserId(user.getUserId());

        String fileName;

        if (resumeOptional.isPresent()) {
            fileName = login + file.getOriginalFilename();
            fileService.deleteFile(fileService.getResumeBucketName(), fileName);
        }

        fileName = fileService.uploadFile(file, login, fileService.getResumeBucketName());
        String fileUrl = fileService.getPublicFileUrl(fileService.getResumeBucketName(), fileName);

        Resume resume = new Resume();
        resume.setUser(user);
        resume.setFileName(file.getOriginalFilename());
        resume.setResumeFileUrl(fileUrl);
        return resumeMapper.toResponseDto(resumeRepository.save(resume));
    }

    @Transactional(readOnly = true)
    public InputStream getResumeByUserId() {
        String login = SecurityUtils.getCurrentUserLogin();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        Optional<Resume> resume = resumeRepository.findByUserUserId(user.getUserId());
        if (resume.isPresent()) {
            String fileName = login + resume.get().getFileName();
            return fileService.downloadFile(fileService.getResumeBucketName(), fileName);
        }
        return null;
    }

    @Transactional
    public void deleteResume() {
        String login = SecurityUtils.getCurrentUserLogin();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Resume resume = resumeRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Резюме для указанного пользователя не найдено"));

        String fileName = login + resume.getFileName();

        fileService.deleteFile(fileService.getResumeBucketName(), fileName);

        resumeRepository.deleteById(resume.getResumeId());
    }

    @Transactional
    public ResumeResponseDTO getCurrentUserResume() {
        String login = SecurityUtils.getCurrentUserLogin();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Resume resume = resumeRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Резюме для указанного пользователя не найдено"));

        return resumeMapper.toResponseDto(resume);
    }
}
