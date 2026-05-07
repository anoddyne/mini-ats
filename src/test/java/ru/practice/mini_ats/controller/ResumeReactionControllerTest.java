package ru.practice.mini_ats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practice.mini_ats.controllers.ResumeReactionController;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionRequestDTO;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionResponseDTO;
import ru.practice.mini_ats.services.ResumeReactionService;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ResumeReactionControllerTest {

    @Mock
    private ResumeReactionService resumeReactionService;

    @InjectMocks
    private ResumeReactionController resumeReactionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final ResumeReactionResponseDTO sampleResponse = new ResumeReactionResponseDTO(
            100,
            "Отличная вакансия, хочу работать!",
            LocalDate.now(),
            200,
            "Senior Java Developer",
            42,
            "Иван Петров"
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resumeReactionController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void applyToVacancyTest() throws Exception {
        ResumeReactionRequestDTO request = new ResumeReactionRequestDTO(
                "Заинтересован в позиции",
                200,
                42
        );

        when(resumeReactionService.applyToVacancy(any(ResumeReactionRequestDTO.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/reactions/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resumeReactionId").value(100))
                .andExpect(jsonPath("$.coverLetter").value("Отличная вакансия, хочу работать!"))
                .andExpect(jsonPath("$.appliedAt").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.vacancyId").value(200))
                .andExpect(jsonPath("$.vacancyTitle").value("Senior Java Developer"))
                .andExpect(jsonPath("$.resumeId").value(42))
                .andExpect(jsonPath("$.candidateFullName").value("Иван Петров"));

        verify(resumeReactionService, times(1)).applyToVacancy(any(ResumeReactionRequestDTO.class));
    }

    @Test
    void getResumeReactionsByVacancyIdTest() throws Exception {
        Integer vacancyId = 200;
        List<ResumeReactionResponseDTO> reactions = List.of(sampleResponse);
        when(resumeReactionService.getReactionsForVacancy(vacancyId)).thenReturn(reactions);

        mockMvc.perform(get("/api/v1/reactions/vacancies/{vacancyId}", vacancyId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].resumeReactionId").value(100))
                .andExpect(jsonPath("$[0].vacancyTitle").value("Senior Java Developer"))
                .andExpect(jsonPath("$[0].candidateFullName").value("Иван Петров"));

        verify(resumeReactionService, times(1)).getReactionsForVacancy(vacancyId);
    }

    @Test
    void getResumeReactionsByResumeIdTest() throws Exception {
        Integer resumeId = 42;
        List<ResumeReactionResponseDTO> reactions = List.of(sampleResponse);
        when(resumeReactionService.getMyReactions(resumeId)).thenReturn(reactions);

        mockMvc.perform(get("/api/v1/reactions/resume/{resumeId}", resumeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].resumeId").value(42))
                .andExpect(jsonPath("$[0].coverLetter").value("Отличная вакансия, хочу работать!"));

        verify(resumeReactionService, times(1)).getMyReactions(resumeId);
    }

    @Test
    void applyToVacancy_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        ResumeReactionRequestDTO invalidRequest = new ResumeReactionRequestDTO(
                "Cover letter",
                null,   // vacancyId = null
                42
        );

        mockMvc.perform(post("/api/v1/reactions/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(resumeReactionService, never()).applyToVacancy(any());
    }
}