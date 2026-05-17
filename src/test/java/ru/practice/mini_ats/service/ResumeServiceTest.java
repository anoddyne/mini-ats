package ru.practice.mini_ats.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.mapper.ResumeMapper;
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.repositories.ResumeRepository;
import ru.practice.mini_ats.repositories.UserRepository;
import ru.practice.mini_ats.security.SecurityUtils;
import ru.practice.mini_ats.services.FileService;
import ru.practice.mini_ats.services.ResumeService;

import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResumeMapper resumeMapper;

    @Mock
    private FileService fileService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ResumeService resumeService;

    private static final String CURRENT_LOGIN = "john.doe";
    private static final int USER_ID = 1;
    private static final String BUCKET_NAME = "bck";
    private static final String ORIGINAL_FILE_NAME = "resume.pdf";
    private static final String UPLOADED_FILE_NAME = "john.doe-resume.pdf";
    private static final String FILE_URL = "https://storage.example.com/resumes/john.doe-resume.pdf";

    private User getUser() {
        User user = new User();
        user.setUserId(USER_ID);
        user.setLogin(CURRENT_LOGIN);
        return user;
    }

    private Resume getResume() {
        Resume resume = new Resume();
        resume.setResumeId(100);
        resume.setFileName(ORIGINAL_FILE_NAME);
        resume.setResumeFileUrl(FILE_URL);
        resume.setUser(getUser());
        return resume;
    }

    private ResumeResponseDTO getResponseDto() {
        return new ResumeResponseDTO(
                100,
                FILE_URL,
                ORIGINAL_FILE_NAME,
                USER_ID
        );
    }

    // ==================== CREATE RESUME ====================

    @Test
    void createResume_WhenUserExistsAndNoPreviousResume_ShouldUploadAndSave() throws Exception {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            User user = getUser();
            when(fileService.getResumeBucketName()).thenReturn(BUCKET_NAME);
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(resumeRepository.findByUserUserId(USER_ID)).thenReturn(Optional.empty());
            when(multipartFile.getOriginalFilename()).thenReturn(ORIGINAL_FILE_NAME);
            when(fileService.uploadFile(multipartFile, CURRENT_LOGIN, BUCKET_NAME))
                    .thenReturn(UPLOADED_FILE_NAME);
            when(fileService.getPublicFileUrl(BUCKET_NAME, UPLOADED_FILE_NAME))
                    .thenReturn(FILE_URL);

            Resume resumeToSave = new Resume();
            Resume savedResume = getResume();
            ResumeResponseDTO expectedResponse = getResponseDto();

            when(resumeRepository.save(any(Resume.class))).thenReturn(savedResume);
            when(resumeMapper.toResponseDto(savedResume)).thenReturn(expectedResponse);

            ResumeResponseDTO result = resumeService.createResume(multipartFile);

            assertThat(result).isEqualTo(expectedResponse);
            verify(fileService).uploadFile(multipartFile, CURRENT_LOGIN, BUCKET_NAME);
            verify(fileService).getPublicFileUrl(BUCKET_NAME, UPLOADED_FILE_NAME);
            verify(resumeRepository).save(any(Resume.class));
            verify(fileService, never()).deleteFile(anyString(), anyString());}
    }

    @Test
    void createResume_WhenUserExistsAndPreviousResumeExists_ShouldDeleteOldFileAndUploadNew() throws Exception {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            // Мокаем getResumeBucketName один раз
            when(fileService.getResumeBucketName()).thenReturn(BUCKET_NAME);

            User user = getUser();
            Resume existingResume = getResume();
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(resumeRepository.findByUserUserId(USER_ID)).thenReturn(Optional.of(existingResume));
            when(multipartFile.getOriginalFilename()).thenReturn(ORIGINAL_FILE_NAME);

            String oldFileName = CURRENT_LOGIN + ORIGINAL_FILE_NAME;
            // Используем BUCKET_NAME, а не вызов метода
            doNothing().when(fileService).deleteFile(BUCKET_NAME, oldFileName);
            when(fileService.uploadFile(multipartFile, CURRENT_LOGIN, BUCKET_NAME))
                    .thenReturn(UPLOADED_FILE_NAME);
            when(fileService.getPublicFileUrl(BUCKET_NAME, UPLOADED_FILE_NAME))
                    .thenReturn(FILE_URL);

            Resume newResume = getResume();
            ResumeResponseDTO expectedResponse = getResponseDto();
            when(resumeRepository.save(any(Resume.class))).thenReturn(newResume);
            when(resumeMapper.toResponseDto(newResume)).thenReturn(expectedResponse);

            ResumeResponseDTO result = resumeService.createResume(multipartFile);

            assertThat(result).isEqualTo(expectedResponse);
            verify(fileService).deleteFile(BUCKET_NAME, oldFileName);
            verify(fileService).uploadFile(multipartFile, CURRENT_LOGIN, BUCKET_NAME);
            verify(resumeRepository).save(any(Resume.class));
        }
    }

    @Test
    void createResume_WhenUserNotFound_ShouldThrowEntityNotFoundException() throws Exception {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> resumeService.createResume(multipartFile))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Пользователь не найден");

            verify(resumeRepository, never()).findByUserUserId(anyInt());
            verify(fileService, never()).uploadFile(any(), any(), any());
            verify(resumeRepository, never()).save(any());
        }
    }

    // ==================== GET RESUME AS STREAM ====================

    @Test
    void deleteResume_WhenResumeExists_ShouldDeleteFileAndRecord() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            User user = getUser();
            Resume resume = getResume();

            when(fileService.getResumeBucketName()).thenReturn(BUCKET_NAME);
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(resumeRepository.findByUserUserId(USER_ID)).thenReturn(Optional.of(resume));
            doNothing().when(fileService).deleteFile(BUCKET_NAME, CURRENT_LOGIN + resume.getFileName());
            doNothing().when(resumeRepository).deleteById(resume.getResumeId());

            resumeService.deleteResume();

            verify(fileService).deleteFile(BUCKET_NAME, CURRENT_LOGIN + resume.getFileName());
            verify(resumeRepository).deleteById(resume.getResumeId());
        }
    }

    @Test
    void getResumeByUserId_WhenResumeNotExists_ShouldReturnNull() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            User user = getUser();
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(resumeRepository.findByUserUserId(USER_ID)).thenReturn(Optional.empty());

            InputStream result = resumeService.getResumeByUserId();

            assertThat(result).isNull();
            verify(fileService, never()).downloadFile(anyString(), anyString());
        }
    }

    @Test
    void getResumeByUserId_WhenUserNotFound_ShouldThrowEntityNotFoundException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> resumeService.getResumeByUserId())
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Пользователь не найден");

            verify(resumeRepository, never()).findByUserUserId(anyInt());
            verify(fileService, never()).downloadFile(anyString(), anyString());
        }
    }

    // ==================== DELETE RESUME ====================

    @Test
    void deleteResume_WhenResumeNotFound_ShouldThrowEntityNotFoundException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            User user = getUser();
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(resumeRepository.findByUserUserId(USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> resumeService.deleteResume())
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Резюме для указанного пользователя не найдено");

            verify(fileService, never()).deleteFile(anyString(), anyString());
            verify(resumeRepository, never()).deleteById(anyInt());
        }
    }

    @Test
    void deleteResume_WhenUserNotFound_ShouldThrowEntityNotFoundException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> resumeService.deleteResume())
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Пользователь не найден");

            verify(resumeRepository, never()).findByUserUserId(anyInt());
            verify(fileService, never()).deleteFile(anyString(), anyString());
        }
    }

    // ==================== GET CURRENT USER RESUME DTO ====================

    @Test
    void getCurrentUserResume_WhenResumeExists_ShouldReturnResponseDto() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            User user = getUser();
            Resume resume = getResume();
            ResumeResponseDTO expectedDto = getResponseDto();

            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(resumeRepository.findByUserUserId(USER_ID)).thenReturn(Optional.of(resume));
            when(resumeMapper.toResponseDto(resume)).thenReturn(expectedDto);

            ResumeResponseDTO result = resumeService.getCurrentUserResume();

            assertThat(result).isEqualTo(expectedDto);
            verify(resumeMapper).toResponseDto(resume);
        }
    }

    @Test
    void getCurrentUserResume_WhenResumeNotFound_ShouldThrowEntityNotFoundException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            User user = getUser();
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(resumeRepository.findByUserUserId(USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> resumeService.getCurrentUserResume())
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Резюме для указанного пользователя не найдено");

            verify(resumeMapper, never()).toResponseDto(any());
        }
    }

    @Test
    void getCurrentUserResume_WhenUserNotFound_ShouldThrowEntityNotFoundException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> resumeService.getCurrentUserResume())
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Пользователь не найден");

            verify(resumeRepository, never()).findByUserUserId(anyInt());
            verify(resumeMapper, never()).toResponseDto(any());
        }
    }
}