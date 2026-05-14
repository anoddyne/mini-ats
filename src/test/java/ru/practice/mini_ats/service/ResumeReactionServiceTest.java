//package ru.practice.mini_ats.service;
//
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionRequestDTO;
//import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionResponseDTO;
//import ru.practice.mini_ats.mapper.ResumeReactionMapper;
//import ru.practice.mini_ats.models.Resume;
//import ru.practice.mini_ats.models.ResumeReaction;
//import ru.practice.mini_ats.models.Vacancy;
//import ru.practice.mini_ats.models.enums.VacancyStatus;
//import ru.practice.mini_ats.repositories.ResumeReactionRepository;
//import ru.practice.mini_ats.repositories.ResumeRepository;
//import ru.practice.mini_ats.repositories.VacancyRepository;
//import ru.practice.mini_ats.services.ResumeReactionService;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ResumeReactionServiceTest {
//
//    @Mock
//    private VacancyRepository vacancyRepository;
//
//    @Mock
//    private ResumeRepository resumeRepository;
//
//    @Mock
//    private ResumeReactionMapper resumeReactionMapper;
//
//    @Mock
//    private ResumeReactionRepository resumeReactionRepository;
//
//    @InjectMocks
//    private ResumeReactionService resumeReactionService;
//
//    private static Vacancy getOpenVacancy() {
//        Vacancy vacancy = new Vacancy();
//        vacancy.setVacancyId(10);
//        vacancy.setTitle("Java Developer");
//        vacancy.setStatus(VacancyStatus.OPEN);
//        return vacancy;
//    }
//
//    private static Vacancy getClosedVacancy() {
//        Vacancy vacancy = new Vacancy();
//        vacancy.setVacancyId(20);
//        vacancy.setTitle("Closed Position");
//        vacancy.setStatus(VacancyStatus.CLOSED);
//        return vacancy;
//    }
//
//    private static Resume getResume() {
//        Resume resume = new Resume();
//        resume.setResumeId(5);
//        resume.setSummary("Experienced dev");
//        return resume;
//    }
//
//    private static ResumeReaction getResumeReaction() {
//        ResumeReaction reaction = new ResumeReaction();
//        reaction.setResumeReactionId(100);
//        reaction.setCoverLetter("I'm interested");
//        reaction.setAppliedAt(LocalDate.now());
//        reaction.setVacancy(getOpenVacancy());
//        reaction.setResume(getResume());
//        return reaction;
//    }
//
//    private static ResumeReactionRequestDTO getRequestDto() {
//        return new ResumeReactionRequestDTO("I'm interested", 10, 5);
//    }
//
//    private static ResumeReactionResponseDTO getResponseDto() {
//        return new ResumeReactionResponseDTO(
//                100, "I'm interested", LocalDate.now(),
//                10, "Java Developer", 5, "John Doe"
//        );
//    }
//
//    @Test
//    void applyToVacancy_WhenVacancyOpenAndNotAppliedYet_ShouldSaveAndReturnResponseDto() {
//        ResumeReactionRequestDTO requestDto = getRequestDto();
//        Vacancy vacancy = getOpenVacancy();
//        Resume resume = getResume();
//        ResumeReaction reactionToSave = new ResumeReaction();
//        ResumeReaction savedReaction = getResumeReaction();
//        ResumeReactionResponseDTO expectedResponse = getResponseDto();
//
//        when(vacancyRepository.findById(10)).thenReturn(Optional.of(vacancy));
//        when(resumeRepository.findById(5)).thenReturn(Optional.of(resume));
//        when(resumeReactionRepository.existsByVacancyVacancyIdAndResumeResumeId(10, 5)).thenReturn(false);
//        when(resumeReactionMapper.toEntity(requestDto)).thenReturn(reactionToSave);
//        when(resumeReactionRepository.save(reactionToSave)).thenReturn(savedReaction);
//        when(resumeReactionMapper.toResponseDto(savedReaction)).thenReturn(expectedResponse);
//
//        ResumeReactionResponseDTO result = resumeReactionService.applyToVacancy(requestDto);
//
//        assertThat(result).isEqualTo(expectedResponse);
//        assertThat(reactionToSave.getVacancy()).isEqualTo(vacancy);
//        assertThat(reactionToSave.getResume()).isEqualTo(resume);
//        assertThat(reactionToSave.getAppliedAt()).isNotNull();
//        verify(vacancyRepository).findById(10);
//        verify(resumeRepository).findById(5);
//        verify(resumeReactionRepository).existsByVacancyVacancyIdAndResumeResumeId(10, 5);
//        verify(resumeReactionMapper).toEntity(requestDto);
//        verify(resumeReactionRepository).save(reactionToSave);
//        verify(resumeReactionMapper).toResponseDto(savedReaction);
//    }
//
//    @Test
//    void applyToVacancy_WhenVacancyNotFound_ShouldThrowEntityNotFoundException() {
//        ResumeReactionRequestDTO requestDto = getRequestDto();
//
//        when(vacancyRepository.findById(10)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> resumeReactionService.applyToVacancy(requestDto))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Вакансия не найдена");
//
//        verify(vacancyRepository).findById(10);
//        verify(resumeRepository, never()).findById(any());
//        verify(resumeReactionRepository, never()).existsByVacancyVacancyIdAndResumeResumeId(any(), any());
//        verify(resumeReactionMapper, never()).toEntity(any());
//        verify(resumeReactionRepository, never()).save(any());
//    }
//
//    @Test
//    void applyToVacancy_WhenVacancyClosed_ShouldThrowIllegalStateException() {
//        ResumeReactionRequestDTO requestDto = getRequestDto();
//        Vacancy closedVacancy = getClosedVacancy();
//
//        when(vacancyRepository.findById(20)).thenReturn(Optional.of(closedVacancy));
//
//        assertThatThrownBy(() -> resumeReactionService.applyToVacancy(
//                new ResumeReactionRequestDTO("text", 20, 5)))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("Нельзя откликнуться на закрытую вакансию");
//
//        verify(vacancyRepository).findById(20);
//        verify(resumeRepository, never()).findById(any());
//        verify(resumeReactionRepository, never()).existsByVacancyVacancyIdAndResumeResumeId(any(), any());
//    }
//
//    @Test
//    void applyToVacancy_WhenResumeNotFound_ShouldThrowEntityNotFoundException() {
//        ResumeReactionRequestDTO requestDto = getRequestDto();
//        Vacancy vacancy = getOpenVacancy();
//
//        when(vacancyRepository.findById(10)).thenReturn(Optional.of(vacancy));
//        when(resumeRepository.findById(5)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> resumeReactionService.applyToVacancy(requestDto))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Резюме не найдено");
//
//        verify(vacancyRepository).findById(10);
//        verify(resumeRepository).findById(5);
//        verify(resumeReactionRepository, never()).existsByVacancyVacancyIdAndResumeResumeId(any(), any());
//    }
//
//    @Test
//    void applyToVacancy_WhenAlreadyApplied_ShouldThrowRuntimeException() {
//        ResumeReactionRequestDTO requestDto = getRequestDto();
//        Vacancy vacancy = getOpenVacancy();
//        Resume resume = getResume();
//
//        when(vacancyRepository.findById(10)).thenReturn(Optional.of(vacancy));
//        when(resumeRepository.findById(5)).thenReturn(Optional.of(resume));
//        when(resumeReactionRepository.existsByVacancyVacancyIdAndResumeResumeId(10, 5)).thenReturn(true);
//
//        assertThatThrownBy(() -> resumeReactionService.applyToVacancy(requestDto))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Вы уже откликались на эту вакансию");
//
//        verify(vacancyRepository).findById(10);
//        verify(resumeRepository).findById(5);
//        verify(resumeReactionRepository).existsByVacancyVacancyIdAndResumeResumeId(10, 5);
//        verify(resumeReactionMapper, never()).toEntity(any());
//        verify(resumeReactionRepository, never()).save(any());
//    }
//
//    @Test
//    void getReactionsForVacancy_WhenSomeExist_ShouldReturnListOfResponseDto() {
//        Integer vacancyId = 10;
//        List<ResumeReaction> reactions = List.of(getResumeReaction());
//        ResumeReactionResponseDTO expectedDto = getResponseDto();
//
//        when(resumeReactionRepository.findByVacancy_VacancyId(vacancyId)).thenReturn(reactions);
//        when(resumeReactionMapper.toResponseDto(reactions.getFirst())).thenReturn(expectedDto);
//
//        List<ResumeReactionResponseDTO> result = resumeReactionService.getReactionsForVacancy(vacancyId);
//
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst()).isEqualTo(expectedDto);
//        verify(resumeReactionRepository).findByVacancy_VacancyId(vacancyId);
//        verify(resumeReactionMapper).toResponseDto(reactions.getFirst());
//    }
//
//    @Test
//    void getReactionsForVacancy_WhenNoneExist_ShouldReturnEmptyList() {
//        Integer vacancyId = 10;
//
//        when(resumeReactionRepository.findByVacancy_VacancyId(vacancyId)).thenReturn(List.of());
//
//        List<ResumeReactionResponseDTO> result = resumeReactionService.getReactionsForVacancy(vacancyId);
//
//        assertThat(result).isEmpty();
//        verify(resumeReactionRepository).findByVacancy_VacancyId(vacancyId);
//        verify(resumeReactionMapper, never()).toResponseDto(any());
//    }
//
//    @Test
//    void getMyReactions_WhenSomeExist_ShouldReturnListOfResponseDto() {
//        Integer resumeId = 5;
//        List<ResumeReaction> reactions = List.of(getResumeReaction());
//        ResumeReactionResponseDTO expectedDto = getResponseDto();
//
//        when(resumeReactionRepository.findByResume_ResumeId(resumeId)).thenReturn(reactions);
//        when(resumeReactionMapper.toResponseDto(reactions.getFirst())).thenReturn(expectedDto);
//
//        List<ResumeReactionResponseDTO> result = resumeReactionService.getMyReactions(resumeId);
//
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst()).isEqualTo(expectedDto);
//        verify(resumeReactionRepository).findByResume_ResumeId(resumeId);
//        verify(resumeReactionMapper).toResponseDto(reactions.getFirst());
//    }
//
//    @Test
//    void getMyReactions_WhenNoneExist_ShouldReturnEmptyList() {
//        Integer resumeId = 5;
//
//        when(resumeReactionRepository.findByResume_ResumeId(resumeId)).thenReturn(List.of());
//
//        List<ResumeReactionResponseDTO> result = resumeReactionService.getMyReactions(resumeId);
//
//        assertThat(result).isEmpty();
//        verify(resumeReactionRepository).findByResume_ResumeId(resumeId);
//        verify(resumeReactionMapper, never()).toResponseDto(any());
//    }
//}