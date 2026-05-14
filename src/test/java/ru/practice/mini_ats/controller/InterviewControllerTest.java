//package ru.practice.mini_ats.controller;
//
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import ru.practice.mini_ats.controllers.InterviewController;
//import ru.practice.mini_ats.dto.Interview.InterviewFeedbackDTO;
//import ru.practice.mini_ats.dto.Interview.InterviewRequestDTO;
//import ru.practice.mini_ats.dto.Interview.InterviewResponseDTO;
//import ru.practice.mini_ats.models.enums.InterviewStatus;
//import ru.practice.mini_ats.models.enums.InterviewType;
//import ru.practice.mini_ats.services.InterviewService;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//public class InterviewControllerTest {
//
//    @Mock
//    private InterviewService interviewService;
//
//    @InjectMocks
//    private InterviewController interviewController;
//
//    private MockMvc mockMvc;
//    private ObjectMapper objectMapper;
//
//    private static final InterviewResponseDTO sampleResponse = new InterviewResponseDTO(
//            1,
//            LocalDate.of(2025, 12, 20),
//            InterviewType.TECHNICAL,
//            InterviewStatus.SCHEDULED,
//            null,
//            101,
//            "Senior Java Developer",
//            "Иван Петров",
//            "ООО Рога и Копыта"
//    );
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(interviewController).build();
//        objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule()); // поддержка LocalDate
//    }
//
//    @Test
//    void scheduleInterviewTest() throws Exception {
//        LocalDate futureDate = LocalDate.now().plusDays(10);
//
//        InterviewRequestDTO request = new InterviewRequestDTO(
//                futureDate,
//                InterviewType.HR,
//                101
//        );
//
//        when(interviewService.scheduleInterview(any(InterviewRequestDTO.class))).thenReturn(sampleResponse);
//
//        mockMvc.perform(post("/api/v1/interviews/schedule")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.interviewId").value(1))
//                .andExpect(jsonPath("$.date").value("2025-12-20"))
//                .andExpect(jsonPath("$.type").value("TECHNICAL"))
//                .andExpect(jsonPath("$.status").value("SCHEDULED"))
//                .andExpect(jsonPath("$.resumeReactionId").value(101))
//                .andExpect(jsonPath("$.vacancyTitle").value("Senior Java Developer"))
//                .andExpect(jsonPath("$.candidateFullName").value("Иван Петров"))
//                .andExpect(jsonPath("$.companyName").value("ООО Рога и Копыта"));
//
//        verify(interviewService, times(1)).scheduleInterview(any(InterviewRequestDTO.class));
//    }
//
//    @Test
//    void addFeedbackToInterviewTest() throws Exception {
//        Integer interviewId = 1;
//        InterviewFeedbackDTO feedbackDTO = new InterviewFeedbackDTO(
//                "Кандидат показал отличные знания",
//                InterviewStatus.COMPLETED
//        );
//
//        InterviewResponseDTO updatedResponse = new InterviewResponseDTO(
//                1,
//                LocalDate.of(2025, 12, 20),
//                InterviewType.TECHNICAL,
//                InterviewStatus.COMPLETED,
//                "Кандидат показал отличные знания",
//                101,
//                "Senior Java Developer",
//                "Иван Петров",
//                "ООО Рога и Копыта"
//        );
//
//        when(interviewService.addFeedback(eq(interviewId), any(InterviewFeedbackDTO.class))).thenReturn(updatedResponse);
//
//        mockMvc.perform(patch("/api/v1/interviews/{id}/feedback", interviewId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(feedbackDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.interviewId").value(1))
//                .andExpect(jsonPath("$.status").value("COMPLETED"))
//                .andExpect(jsonPath("$.feedback").value("Кандидат показал отличные знания"));
//
//        verify(interviewService, times(1)).addFeedback(eq(interviewId), any(InterviewFeedbackDTO.class));
//    }
//
//    @Test
//    void getListInterviewsByCompanyIdTest() throws Exception {
//        Integer companyId = 10;
//        List<InterviewResponseDTO> interviews = List.of(sampleResponse);
//        when(interviewService.getInterviewsByCompany(companyId)).thenReturn(interviews);
//
//        mockMvc.perform(get("/api/v1/interviews/companies/{companyId}", companyId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].interviewId").value(1))
//                .andExpect(jsonPath("$[0].companyName").value("ООО Рога и Копыта"));
//
//        verify(interviewService, times(1)).getInterviewsByCompany(companyId);
//    }
//
//    @Test
//    void deleteInterviewTest() throws Exception {
//        Integer interviewId = 5;
//        doNothing().when(interviewService).deleteInterview(interviewId);
//
//        mockMvc.perform(delete("/api/v1/interviews/{id}", interviewId))
//                .andExpect(status().isNoContent());
//
//        verify(interviewService, times(1)).deleteInterview(interviewId);
//    }
//    @Test
//    void scheduleInterview_InvalidDate_ShouldReturnBadRequest() throws Exception {
//        InterviewRequestDTO invalidRequest = new InterviewRequestDTO(
//                LocalDate.now().minusDays(1),   // дата в прошлом
//                InterviewType.HR,
//                101
//        );
//
//        mockMvc.perform(post("/api/v1/interviews/schedule")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidRequest)))
//                .andExpect(status().isBadRequest());
//
//        verify(interviewService, never()).scheduleInterview(any());
//    }
//}