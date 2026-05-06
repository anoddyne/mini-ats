package ru.practice.mini_ats.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practice.mini_ats.controllers.ResumeController;
import ru.practice.mini_ats.dto.Resume.ResumeRequestDTO;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.services.ResumeService;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ResumeControllerTest {

    @Mock
    private ResumeService resumeService;

    @InjectMocks
    private ResumeController resumeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final ResumeResponseDTO responseDTO = new ResumeResponseDTO(
            100,
            "Опытный Java разработчик",
            "Высшее образование, МГУ",
            180000,
            "https://example.com/resume.pdf",
            Map.of("Java", 5, "Spring", 4),
            Map.of("Previous job", "Senior Dev", "Years", 5),
            42,
            "Иван Иванов"
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resumeController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createResumeTest() throws Exception {
        ResumeRequestDTO request = new ResumeRequestDTO(
                "Краткое описание",
                "МГУ им. Ломоносова",
                150000,
                "https://example.com/myresume.pdf",
                Map.of("Java", 4, "SQL", 3),
                Map.of("Company A", "2 года", "Company B", "1 год")
        );

        when(resumeService.createResume(any(ResumeRequestDTO.class), eq(1)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/resume")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resumeId").value(100))
                .andExpect(jsonPath("$.summary").value("Опытный Java разработчик"))
                .andExpect(jsonPath("$.education").value("Высшее образование, МГУ"))
                .andExpect(jsonPath("$.desiredSalary").value(180000))
                .andExpect(jsonPath("$.resumeFileUrl").value("https://example.com/resume.pdf"))
                .andExpect(jsonPath("$.skills").isMap())
                .andExpect(jsonPath("$.skills.Java").value(5))
                .andExpect(jsonPath("$.skills.Spring").value(4))
                .andExpect(jsonPath("$.experience").isMap())
                .andExpect(jsonPath("$.experience['Previous job']").value("Senior Dev"))
                .andExpect(jsonPath("$.experience.Years").value(5))
                .andExpect(jsonPath("$.userId").value(42))
                .andExpect(jsonPath("$.userFullName").value("Иван Иванов"));

        verify(resumeService, times(1)).createResume(any(ResumeRequestDTO.class), eq(1));
    }

    @Test
    void getResumeByIdTest() throws Exception {
        Integer id = 100;
        when(resumeService.getByUserId(id)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/resume/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumeId").value(100))
                .andExpect(jsonPath("$.summary").value("Опытный Java разработчик"))
                .andExpect(jsonPath("$.userId").value(42))
                .andExpect(jsonPath("$.userFullName").value("Иван Иванов"));

        verify(resumeService, times(1)).getByUserId(id);
    }

    @Test
    void updateResumeTest() throws Exception {
        Integer id = 100;
        ResumeRequestDTO updateRequest = new ResumeRequestDTO(
                "Обновленное резюме",
                "Бакалавр",
                200000,
                "https://example.com/newresume.pdf",
                Map.of("Python", 4),
                Map.of("NewJob", "6 месяцев")
        );
        ResumeResponseDTO updatedResponse = new ResumeResponseDTO(
                100,
                "Обновленное резюме",
                "Бакалавр",
                200000,
                "https://example.com/newresume.pdf",
                Map.of("Python", 4),
                Map.of("NewJob", "6 месяцев"),
                42,
                "Иван Иванов"
        );

        when(resumeService.updateResume(eq(id), any(ResumeRequestDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/resume/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumeId").value(100))
                .andExpect(jsonPath("$.summary").value("Обновленное резюме"))
                .andExpect(jsonPath("$.desiredSalary").value(200000));

        verify(resumeService, times(1)).updateResume(eq(id), any(ResumeRequestDTO.class));
    }

    @Test
    void deleteResumeTest() throws Exception {
        Integer id = 100;
        doNothing().when(resumeService).deleteResume(id);

        mockMvc.perform(delete("/api/v1/resume/{id}", id))
                .andExpect(status().isNoContent());

        verify(resumeService, times(1)).deleteResume(id);
    }
}