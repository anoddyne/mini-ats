package ru.practice.mini_ats.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.mini_ats.dto.Resume.ResumeRequestDTO;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.mapper.ResumeMapper;
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.repositories.ResumeRepository;
import ru.practice.mini_ats.repositories.UserRepository;
import ru.practice.mini_ats.services.ResumeService;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResumeMapper resumeMapper;

    @InjectMocks
    private ResumeService resumeService;

    private static User getUser() {
        User user = new User();
        user.setUserId(1);
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john@example.com");
        user.setLogin("johndoe");
        user.setPassword("pass");
        return user;
    }

    private static Resume getResume() {
        Resume resume = new Resume();
        resume.setResumeId(10);
        resume.setSummary("Experienced Java developer");
        resume.setEducation("Master's in CS");
        resume.setDesiredSalary(250_000);
        resume.setResumeFileUrl("https://example.com/resume.pdf");
        resume.setSkills(Map.of("Java", 5));
        resume.setExperience(Map.of("years", 5));
        resume.setUser(getUser());
        return resume;
    }

    private static ResumeRequestDTO getRequestDto() {
        return new ResumeRequestDTO(
                "Experienced Java developer",
                "Master's in CS",
                250_000,
                "https://example.com/resume.pdf",
                Map.of("Java", 5),
                Map.of("years", 5)
        );
    }

    private static ResumeResponseDTO getResponseDto() {
        return new ResumeResponseDTO(
                10,
                "Experienced Java developer",
                "Master's in CS",
                250_000,
                "https://example.com/resume.pdf",
                Map.of("Java", 5),
                Map.of("years", 5),
                1,
                "John Doe"
        );
    }

    @Test
    void createResume_WhenUserExistsAndNoResumeYet_ShouldSaveAndReturnResponseDto() {
        Integer userId = 1;
        ResumeRequestDTO requestDto = getRequestDto();
        User user = getUser();
        Resume resumeToSave = new Resume();
        Resume savedResume = getResume();
        ResumeResponseDTO expectedResponse = getResponseDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(resumeRepository.existsByUserUserId(userId)).thenReturn(false);
        when(resumeMapper.toEntity(requestDto)).thenReturn(resumeToSave);
        when(resumeRepository.save(resumeToSave)).thenReturn(savedResume);
        when(resumeMapper.toResponseDto(savedResume)).thenReturn(expectedResponse);

        ResumeResponseDTO result = resumeService.createResume(requestDto, userId);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(resumeToSave.getUser()).isEqualTo(user);
        verify(userRepository).findById(userId);
        verify(resumeRepository).existsByUserUserId(userId);
        verify(resumeMapper).toEntity(requestDto);
        verify(resumeRepository).save(resumeToSave);
        verify(resumeMapper).toResponseDto(savedResume);
    }

    @Test
    void createResume_WhenUserNotFound_ShouldThrowEntityNotFoundException() {
        Integer userId = 999;
        ResumeRequestDTO requestDto = getRequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.createResume(requestDto, userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");

        verify(userRepository).findById(userId);
        verify(resumeRepository, never()).existsByUserUserId(any());
        verify(resumeMapper, never()).toEntity(any());
        verify(resumeRepository, never()).save(any());
    }

    @Test
    void createResume_WhenUserAlreadyHasResume_ShouldThrowRuntimeException() {
        Integer userId = 1;
        ResumeRequestDTO requestDto = getRequestDto();
        User user = getUser();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(resumeRepository.existsByUserUserId(userId)).thenReturn(true);

        assertThatThrownBy(() -> resumeService.createResume(requestDto, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("У пользователя уже есть резюме");

        verify(userRepository).findById(userId);
        verify(resumeRepository).existsByUserUserId(userId);
        verify(resumeMapper, never()).toEntity(any());
        verify(resumeRepository, never()).save(any());
    }

    @Test
    void getByUserId_WhenResumeExists_ShouldReturnResponseDto() {
        Integer userId = 1;
        Resume resume = getResume();
        ResumeResponseDTO expectedDto = getResponseDto();

        when(resumeRepository.findByUserUserId(userId)).thenReturn(Optional.of(resume));
        when(resumeMapper.toResponseDto(resume)).thenReturn(expectedDto);

        ResumeResponseDTO result = resumeService.getByUserId(userId);

        assertThat(result).isEqualTo(expectedDto);
        verify(resumeRepository).findByUserUserId(userId);
        verify(resumeMapper).toResponseDto(resume);
    }

    @Test
    void getByUserId_WhenResumeNotExists_ShouldThrowEntityNotFoundException() {
        Integer userId = 999;

        when(resumeRepository.findByUserUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.getByUserId(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Резюме для указанного пользователя не найдено");

        verify(resumeRepository).findByUserUserId(userId);
        verify(resumeMapper, never()).toResponseDto(any());
    }

    @Test
    void updateResume_WhenResumeExists_ShouldUpdateAndReturnResponseDto() {
        Integer resumeId = 10;
        ResumeRequestDTO updateDto = getRequestDto();
        Resume existingResume = getResume();
        Resume updatedResume = getResume();
        updatedResume.setSummary("Updated summary");
        ResumeResponseDTO expectedResponse = new ResumeResponseDTO(
                10, "Updated summary", "Master's in CS", 250_000,
                "https://example.com/resume.pdf", Map.of("Java", 5),
                Map.of("years", 5), 1, "John Doe"
        );

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.of(existingResume));
        doNothing().when(resumeMapper).updateEntityFromDto(updateDto, existingResume);
        when(resumeRepository.save(existingResume)).thenReturn(updatedResume);
        when(resumeMapper.toResponseDto(updatedResume)).thenReturn(expectedResponse);

        ResumeResponseDTO result = resumeService.updateResume(resumeId, updateDto);

        assertThat(result).isEqualTo(expectedResponse);
        verify(resumeRepository).findById(resumeId);
        verify(resumeMapper).updateEntityFromDto(updateDto, existingResume);
        verify(resumeRepository).save(existingResume);
        verify(resumeMapper).toResponseDto(updatedResume);
    }

    @Test
    void updateResume_WhenResumeNotExists_ShouldThrowEntityNotFoundException() {
        Integer resumeId = 999;
        ResumeRequestDTO updateDto = getRequestDto();

        when(resumeRepository.findById(resumeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.updateResume(resumeId, updateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Резюме не найдено");

        verify(resumeRepository).findById(resumeId);
        verify(resumeMapper, never()).updateEntityFromDto(any(), any());
        verify(resumeRepository, never()).save(any());
    }

    @Test
    void deleteResume_WhenExists_ShouldDelete() {
        Integer resumeId = 10;

        when(resumeRepository.existsById(resumeId)).thenReturn(true);
        doNothing().when(resumeRepository).deleteById(resumeId);

        resumeService.deleteResume(resumeId);

        verify(resumeRepository).existsById(resumeId);
        verify(resumeRepository).deleteById(resumeId);
    }

    @Test
    void deleteResume_WhenNotExists_ShouldThrowEntityNotFoundException() {
        Integer resumeId = 999;

        when(resumeRepository.existsById(resumeId)).thenReturn(false);

        assertThatThrownBy(() -> resumeService.deleteResume(resumeId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Не удалось удалить: резюме с id999 не существует");

        verify(resumeRepository).existsById(resumeId);
        verify(resumeRepository, never()).deleteById(any());
    }
}