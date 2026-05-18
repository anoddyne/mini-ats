package ru.practice.mini_ats.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;
import ru.practice.mini_ats.controllers.ResumeController;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.services.ResumeService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
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

    private static final ResumeResponseDTO responseDTO = new ResumeResponseDTO(
            100,
            "https://storage.example.com/resumes/john.doe_resume.pdf",
            "resume.pdf",
            1  // userId
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resumeController).build();
        ObjectMapper objectMapper = new ObjectMapper();
    }

    @Test
    void createResumeTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "PDF content".getBytes()
        );

        when(resumeService.createResume(any(MultipartFile.class))).thenReturn(responseDTO);

        mockMvc.perform(multipart("/api/v1/resume")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resumeId").value(100))
                .andExpect(jsonPath("$.resumeFileUrl").value("https://storage.example.com/resumes/john.doe_resume.pdf"))
                .andExpect(jsonPath("$.fileName").value("resume.pdf"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(resumeService, times(1)).createResume(any(MultipartFile.class));
    }

    @Test
    void getResumeFileTest() throws Exception {
        byte[] content = "PDF content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        when(resumeService.getResumeByUserId()).thenReturn(inputStream);

        mockMvc.perform(get("/api/v1/resume/download")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resume.pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(content));

        verify(resumeService, times(1)).getResumeByUserId();
    }

    @Test
    void getResumeFile_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(resumeService.getResumeByUserId()).thenReturn(null);

        mockMvc.perform(get("/api/v1/resume/download"))
                .andExpect(status().isNotFound());

        verify(resumeService, times(1)).getResumeByUserId();
    }

    @Test
    void getResumeFile_WhenExceptionThrown_ShouldReturnNotFound() throws Exception {
        when(resumeService.getResumeByUserId()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/resume/download"))
                .andExpect(status().isNotFound());

        verify(resumeService, times(1)).getResumeByUserId();
    }

    @Test
    void deleteResumeTest() throws Exception {
        doNothing().when(resumeService).deleteResume();

        mockMvc.perform(delete("/api/v1/resume"))
                .andExpect(status().isNoContent());

        verify(resumeService, times(1)).deleteResume();
    }

    @Test
    void getResumeInfo_WhenExists_ShouldReturnDto() throws Exception {
        when(resumeService.getCurrentUserResume()).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/resume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumeId").value(100))
                .andExpect(jsonPath("$.resumeFileUrl").value("https://storage.example.com/resumes/john.doe_resume.pdf"))
                .andExpect(jsonPath("$.fileName").value("resume.pdf"))
                .andExpect(jsonPath("$.userId").value(1));

        verify(resumeService, times(1)).getCurrentUserResume();
    }

    @Test
    void getResumeInfo_WhenNotExists_ShouldReturnNoContent() throws Exception {
        when(resumeService.getCurrentUserResume()).thenReturn(null);

        mockMvc.perform(get("/api/v1/resume"))
                .andExpect(status().isNoContent());

        verify(resumeService, times(1)).getCurrentUserResume();
    }
}