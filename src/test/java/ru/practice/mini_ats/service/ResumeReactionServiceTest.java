package ru.practice.mini_ats.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.practice.mini_ats.services.ResumeReactionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeReactionServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private ResumeReactionMapper resumeReactionMapper;

    @Mock
    private ResumeReactionRepository resumeReactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ResumeReactionService resumeReactionService;

    private static final String CURRENT_LOGIN = "candidate";
    private static final int USER_ID = 1;
    private static final int RESUME_ID = 100;
    private static final int VACANCY_ID = 10;

    private User getUser() {
        User user = new User();
        user.setUserId(USER_ID);
        user.setLogin(CURRENT_LOGIN);
        user.setName("John");
        user.setSurname("Doe");
        return user;
    }

    private Resume getResume() {
        Resume resume = new Resume();
        resume.setResumeId(RESUME_ID);
        resume.setResumeFileUrl("http://example.com/resume.pdf");
        resume.setFileName("resume.pdf");
        resume.setUser(getUser());
        return resume;
    }

    private Vacancy getOpenVacancy() {
        Vacancy vacancy = new Vacancy();
        vacancy.setVacancyId(VACANCY_ID);
        vacancy.setTitle("Java Developer");
        vacancy.setStatus(VacancyStatus.OPEN);
        return vacancy;
    }

    private Vacancy getClosedVacancy() {
        Vacancy vacancy = new Vacancy();
        vacancy.setVacancyId(VACANCY_ID);
        vacancy.setTitle("Closed Position");
        vacancy.setStatus(VacancyStatus.CLOSED);
        return vacancy;
    }

    private ResumeReaction getResumeReaction() {
        ResumeReaction reaction = new ResumeReaction();
        reaction.setResumeReactionId(100);
        reaction.setCoverLetter("I'm interested");
        reaction.setAppliedAt(LocalDate.now());
        reaction.setVacancy(getOpenVacancy());
        reaction.setResume(getResume());
        return reaction;
    }

    private ResumeReactionRequestDTO getRequestDto() {
        return new ResumeReactionRequestDTO("I'm interested", VACANCY_ID);
    }

    private ResumeReactionResponseDTO getResponseDto() {
        return new ResumeReactionResponseDTO(
                100, "I'm interested", LocalDate.now(),
                VACANCY_ID, "Java Developer", "Test Company",
                RESUME_ID, "John Doe"
        );
    }

    // ==================== APPLY TO VACANCY ====================

    @Test
    void applyToVacancy_WhenVacancyOpenAndUserHasResumeAndNotAppliedYet_ShouldSaveAndReturnResponseDto() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            ResumeReactionRequestDTO requestDto = getRequestDto();
            User user = getUser();
            Resume resume = getResume();
            Vacancy vacancy = getOpenVacancy();
            ResumeReaction reactionToSave = new ResumeReaction();
            ResumeReaction savedReaction = getResumeReaction();
            ResumeReactionResponseDTO expectedResponse = getResponseDto();

            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(resumeRepository.findByUserUserId(USER_ID)).thenReturn(Optional.of(resume));
            when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
            when(resumeReactionRepository.existsByVacancyVacancyIdAndResumeResumeId(VACANCY_ID, RESUME_ID))
                    .thenReturn(false);
            when(resumeReactionMapper.toEntity(requestDto)).thenReturn(reactionToSave);
            when(resumeReactionRepository.save(reactionToSave)).thenReturn(savedReaction);
            when(resumeReactionMapper.toResponseDto(savedReaction)).thenReturn(expectedResponse);

            ResumeReactionResponseDTO result = resumeReactionService.applyToVacancy(requestDto);

            assertThat(result).isEqualTo(expectedResponse);
            assertThat(reactionToSave.getVacancy()).isEqualTo(vacancy);
            assertThat(reactionToSave.getResume()).isEqualTo(resume);
            assertThat(reactionToSave.getAppliedAt()).isNotNull();
            verify(userRepository).findByLogin(CURRENT_LOGIN);
            verify(resumeRepository).findByUserUserId(USER_ID);
            verify(vacancyRepository).findById(VACANCY_ID);
            verify(resumeReactionRepository).existsByVacancyVacancyIdAndResumeResumeId(VACANCY_ID, RESUME_ID);
            verify(resumeReactionMapper).toEntity(requestDto);
            verify(resumeReactionRepository).save(reactionToSave);
            verify(resumeReactionMapper).toResponseDto(savedReaction);
        }
    }

    @Test
    void applyToVacancy_WhenUserNotFound_ShouldThrowEntityNotFoundException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> resumeReactionService.applyToVacancy(getRequestDto()))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Пользователь не найден");

            verify(resumeRepository, never()).findByUserUserId(anyInt());
            verify(vacancyRepository, never()).findById(anyInt());
            verify(resumeReactionRepository, never()).existsByVacancyVacancyIdAndResumeResumeId(anyInt(), anyInt());
        }
    }

    @Test
    void applyToVacancy_WhenUserHasNoResume_ShouldThrowRuntimeException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);
            User user = getUser();
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(resumeRepository.findByUserUserId(USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> resumeReactionService.applyToVacancy(getRequestDto()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("У вас нет резюме. Создайте его перед откликом.");

            verify(vacancyRepository, never()).findById(anyInt());
            verify(resumeReactionRepository, never()).existsByVacancyVacancyIdAndResumeResumeId(anyInt(), anyInt());
        }
    }

    @Test
    void applyToVacancy_WhenVacancyNotFound_ShouldThrowEntityNotFoundException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);
            User user = getUser();
            Resume resume = getResume();
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(resumeRepository.findByUserUserId(USER_ID)).thenReturn(Optional.of(resume));
            when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> resumeReactionService.applyToVacancy(getRequestDto()))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Вакансия не найдена");

            verify(resumeReactionRepository, never()).existsByVacancyVacancyIdAndResumeResumeId(anyInt(), anyInt());
        }
    }

    @Test
    void applyToVacancy_WhenVacancyClosed_ShouldThrowIllegalStateException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);
            User user = getUser();
            Resume resume = getResume();
            Vacancy closedVacancy = getClosedVacancy();
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(resumeRepository.findByUserUserId(USER_ID)).thenReturn(Optional.of(resume));
            when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(closedVacancy));

            assertThatThrownBy(() -> resumeReactionService.applyToVacancy(getRequestDto()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Нельзя откликнуться на закрытую вакансию");

            verify(resumeReactionRepository, never()).existsByVacancyVacancyIdAndResumeResumeId(anyInt(), anyInt());
        }
    }

    @Test
    void applyToVacancy_WhenAlreadyApplied_ShouldThrowRuntimeException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);
            User user = getUser();
            Resume resume = getResume();
            Vacancy vacancy = getOpenVacancy();
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(resumeRepository.findByUserUserId(USER_ID)).thenReturn(Optional.of(resume));
            when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(vacancy));
            when(resumeReactionRepository.existsByVacancyVacancyIdAndResumeResumeId(VACANCY_ID, RESUME_ID))
                    .thenReturn(true);

            assertThatThrownBy(() -> resumeReactionService.applyToVacancy(getRequestDto()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Вы уже откликались на эту вакансию");

            verify(resumeReactionMapper, never()).toEntity(any());
            verify(resumeReactionRepository, never()).save(any());
        }
    }

    // ==================== GET REACTIONS FOR VACANCY ====================

    @Test
    void getReactionsForVacancy_WhenSomeExist_ShouldReturnListOfResponseDto() {
        List<ResumeReaction> reactions = List.of(getResumeReaction());
        ResumeReactionResponseDTO expectedDto = getResponseDto();

        when(resumeReactionRepository.findByVacancy_VacancyId(VACANCY_ID)).thenReturn(reactions);
        when(resumeReactionMapper.toResponseDto(reactions.getFirst())).thenReturn(expectedDto);

        List<ResumeReactionResponseDTO> result = resumeReactionService.getReactionsForVacancy(VACANCY_ID);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(expectedDto);
        verify(resumeReactionRepository).findByVacancy_VacancyId(VACANCY_ID);
        verify(resumeReactionMapper).toResponseDto(reactions.getFirst());
    }

    @Test
    void getReactionsForVacancy_WhenNoneExist_ShouldReturnEmptyList() {
        when(resumeReactionRepository.findByVacancy_VacancyId(VACANCY_ID)).thenReturn(List.of());

        List<ResumeReactionResponseDTO> result = resumeReactionService.getReactionsForVacancy(VACANCY_ID);

        assertThat(result).isEmpty();
        verify(resumeReactionRepository).findByVacancy_VacancyId(VACANCY_ID);
        verify(resumeReactionMapper, never()).toResponseDto(any());
    }

    // ==================== GET MY REACTIONS ====================

    @Test
    void getMyReactions_WhenResumeExistsAndReactionsPresent_ShouldReturnListOfResponseDto() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            Resume resume = getResume();
            List<ResumeReaction> reactions = List.of(getResumeReaction());
            ResumeReactionResponseDTO expectedDto = getResponseDto();

            when(resumeRepository.findByUser_Login(CURRENT_LOGIN)).thenReturn(Optional.of(resume));
            when(resumeReactionRepository.findByResume_ResumeId(RESUME_ID)).thenReturn(reactions);
            when(resumeReactionMapper.toResponseDto(reactions.getFirst())).thenReturn(expectedDto);

            List<ResumeReactionResponseDTO> result = resumeReactionService.getMyReactions();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst()).isEqualTo(expectedDto);
            verify(resumeRepository).findByUser_Login(CURRENT_LOGIN);
            verify(resumeReactionRepository).findByResume_ResumeId(RESUME_ID);
            verify(resumeReactionMapper).toResponseDto(reactions.getFirst());
        }
    }

    @Test
    void getMyReactions_WhenResumeNotFound_ShouldThrowEntityNotFoundException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);
            when(resumeRepository.findByUser_Login(CURRENT_LOGIN)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> resumeReactionService.getMyReactions())
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Резюме не найдено");

            verify(resumeReactionRepository, never()).findByResume_ResumeId(anyInt());
        }
    }

    @Test
    void getMyReactions_WhenNoReactions_ShouldReturnEmptyList() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            Resume resume = getResume();
            when(resumeRepository.findByUser_Login(CURRENT_LOGIN)).thenReturn(Optional.of(resume));
            when(resumeReactionRepository.findByResume_ResumeId(RESUME_ID)).thenReturn(List.of());

            List<ResumeReactionResponseDTO> result = resumeReactionService.getMyReactions();

            assertThat(result).isEmpty();
            verify(resumeReactionRepository).findByResume_ResumeId(RESUME_ID);
            verify(resumeReactionMapper, never()).toResponseDto(any());
        }
    }
}