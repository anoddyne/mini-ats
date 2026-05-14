//package ru.practice.mini_ats.service;
//
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ru.practice.mini_ats.dto.Interview.InterviewFeedbackDTO;
//import ru.practice.mini_ats.dto.Interview.InterviewRequestDTO;
//import ru.practice.mini_ats.dto.Interview.InterviewResponseDTO;
//import ru.practice.mini_ats.mapper.InterviewMapper;
//import ru.practice.mini_ats.models.Interview;
//import ru.practice.mini_ats.models.ResumeReaction;
//import ru.practice.mini_ats.models.enums.InterviewStatus;
//import ru.practice.mini_ats.models.enums.InterviewType;
//import ru.practice.mini_ats.repositories.InterviewRepository;
//import ru.practice.mini_ats.repositories.ResumeReactionRepository;
//import ru.practice.mini_ats.services.InterviewService;
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
//class InterviewServiceTest {
//
//    @Mock
//    private InterviewRepository interviewRepository;
//
//    @Mock
//    private ResumeReactionRepository resumeReactionRepository;
//
//    @Mock
//    private InterviewMapper interviewMapper;
//
//    @InjectMocks
//    private InterviewService interviewService;
//
//    private static ResumeReaction getResumeReaction() {
//        ResumeReaction reaction = new ResumeReaction();
//        reaction.setResumeReactionId(100);
//        return reaction;
//    }
//
//    private static Interview getInterview() {
//        Interview interview = new Interview();
//        interview.setInterviewId(1);
//        interview.setDate(LocalDate.of(2026, 5, 15));
//        interview.setType(InterviewType.TECHNICAL);
//        interview.setStatus(InterviewStatus.SCHEDULED);
//        interview.setFeedback(null);
//        interview.setResumeReaction(getResumeReaction());
//        return interview;
//    }
//
//    private static InterviewRequestDTO getRequestDto() {
//        return new InterviewRequestDTO(
//                LocalDate.of(2026, 5, 15),
//                InterviewType.TECHNICAL,
//                100 // resumeReactionId
//        );
//    }
//
//    private static InterviewFeedbackDTO getFeedbackDto() {
//        return new InterviewFeedbackDTO("Good candidate", InterviewStatus.COMPLETED);
//    }
//
//    private static InterviewResponseDTO getResponseDto() {
//        return new InterviewResponseDTO(
//                1,
//                LocalDate.of(2026, 5, 15),
//                InterviewType.TECHNICAL,
//                InterviewStatus.SCHEDULED,
//                null,
//                100,
//                "Java Developer",
//                "John Doe",
//                "Tech Corp"
//        );
//    }
//
//    @Test
//    void scheduleInterview_WhenResumeReactionExists_ShouldSaveAndReturnResponseDto() {
//        InterviewRequestDTO requestDto = getRequestDto();
//        ResumeReaction reaction = getResumeReaction();
//        Interview interviewToSave = new Interview();
//        Interview savedInterview = getInterview();
//        InterviewResponseDTO expectedResponse = getResponseDto();
//
//        when(resumeReactionRepository.findById(100)).thenReturn(Optional.of(reaction));
//        when(interviewMapper.toEntity(requestDto)).thenReturn(interviewToSave);
//        when(interviewRepository.save(interviewToSave)).thenReturn(savedInterview);
//        when(interviewMapper.toResponseDto(savedInterview)).thenReturn(expectedResponse);
//
//        InterviewResponseDTO result = interviewService.scheduleInterview(requestDto);
//
//        assertThat(result).isEqualTo(expectedResponse);
//        assertThat(interviewToSave.getStatus()).isEqualTo(InterviewStatus.SCHEDULED);
//        assertThat(interviewToSave.getResumeReaction()).isEqualTo(reaction);
//        verify(resumeReactionRepository).findById(100);
//        verify(interviewMapper).toEntity(requestDto);
//        verify(interviewRepository).save(interviewToSave);
//        verify(interviewMapper).toResponseDto(savedInterview);
//    }
//
//    @Test
//    void scheduleInterview_WhenResumeReactionNotFound_ShouldThrowEntityNotFoundException() {
//        InterviewRequestDTO requestDto = getRequestDto();
//
//        when(resumeReactionRepository.findById(100)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> interviewService.scheduleInterview(requestDto))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Отклик на вакансию не найден");
//
//        verify(resumeReactionRepository).findById(100);
//        verify(interviewMapper, never()).toEntity(any());
//        verify(interviewRepository, never()).save(any());
//    }
//
//    @Test
//    void addFeedback_WhenInterviewNotFound_ShouldThrowEntityNotFoundException() {
//        Integer interviewId = 999;
//        InterviewFeedbackDTO feedbackDto = getFeedbackDto();
//
//        when(interviewRepository.findById(interviewId)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> interviewService.addFeedback(interviewId, feedbackDto))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Интервью не найдено");
//
//        verify(interviewRepository).findById(interviewId);
//        verify(interviewRepository, never()).save(any());
//    }
//
//    @Test
//    void getInterviewsByCompany_WhenExist_ShouldReturnListOfResponseDto() {
//        Integer companyId = 1;
//        List<Interview> interviews = List.of(getInterview());
//        InterviewResponseDTO expectedDto = getResponseDto();
//
//        when(interviewRepository.findAllByResumeReactionVacancyCompanyCompanyId(companyId))
//                .thenReturn(interviews);
//        when(interviewMapper.toResponseDto(interviews.getFirst())).thenReturn(expectedDto);
//
//        List<InterviewResponseDTO> result = interviewService.getInterviewsByCompany(companyId);
//
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst()).isEqualTo(expectedDto);
//        verify(interviewRepository).findAllByResumeReactionVacancyCompanyCompanyId(companyId);
//        verify(interviewMapper).toResponseDto(interviews.getFirst());
//    }
//
//    @Test
//    void getInterviewsByCompany_WhenNoInterviews_ShouldReturnEmptyList() {
//        Integer companyId = 1;
//
//        when(interviewRepository.findAllByResumeReactionVacancyCompanyCompanyId(companyId))
//                .thenReturn(List.of());
//
//        List<InterviewResponseDTO> result = interviewService.getInterviewsByCompany(companyId);
//
//        assertThat(result).isEmpty();
//        verify(interviewRepository).findAllByResumeReactionVacancyCompanyCompanyId(companyId);
//        verify(interviewMapper, never()).toResponseDto(any());
//    }
//
//    @Test
//    void deleteInterview_WhenExists_ShouldDelete() {
//        Integer interviewId = 1;
//
//        when(interviewRepository.existsById(interviewId)).thenReturn(true);
//        doNothing().when(interviewRepository).deleteById(interviewId);
//
//        interviewService.deleteInterview(interviewId);
//
//        verify(interviewRepository).existsById(interviewId);
//        verify(interviewRepository).deleteById(interviewId);
//    }
//
//    @Test
//    void deleteInterview_WhenNotExists_ShouldThrowEntityNotFoundException() {
//        Integer interviewId = 999;
//
//        when(interviewRepository.existsById(interviewId)).thenReturn(false);
//
//        assertThatThrownBy(() -> interviewService.deleteInterview(interviewId))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Интервью не найдено");
//
//        verify(interviewRepository).existsById(interviewId);
//        verify(interviewRepository, never()).deleteById(any());
//    }
//}