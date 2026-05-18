package ru.practice.mini_ats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practice.mini_ats.controllers.VacancyController;
import ru.practice.mini_ats.dto.Vacancy.VacancyRequestDTO;
import ru.practice.mini_ats.dto.Vacancy.VacancyResponseDTO;
import ru.practice.mini_ats.models.enums.EmploymentType;
import ru.practice.mini_ats.models.enums.ExperienceLevel;
import ru.practice.mini_ats.models.enums.VacancyStatus;
import ru.practice.mini_ats.services.VacancyService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class VacancyControllerTest {

    @Mock
    private VacancyService vacancyService;

    @InjectMocks
    private VacancyController vacancyController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final List<VacancyResponseDTO> vacancies = List.of(
            new VacancyResponseDTO(
                    1,
                    "Middle Java developer",
                    "Нужен Java разработчик 2+ года опыта",
                    150000,
                    200000,
                    "Москва",
                    EmploymentType.REMOTE,
                    VacancyStatus.OPEN,
                    "Java, Spring Boot, PostgreSQL, Docker",
                    ExperienceLevel.MIDDLE,
                    1,
                    "Company1",
                    3
            ),
            new VacancyResponseDTO(
                    2,
                    "Middle C++ developer",
                    "Нужен C++ разработчик 2+ года опыта",
                    150000,
                    200000,
                    "Москва",
                    EmploymentType.REMOTE,
                    VacancyStatus.CLOSED,
                    "C++, PostgreSQL, Docker",
                    ExperienceLevel.MIDDLE,
                    2,
                    "Company2",
                    2
            )
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vacancyController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createVacancyTest() throws Exception {
        VacancyRequestDTO request = new VacancyRequestDTO(
                "Middle Java Developer",
                "Нужен Java разработчик 2+ года опыта",
                150000,
                200000,
                "Москва",
                EmploymentType.REMOTE,
                VacancyStatus.OPEN,
                "Java, Spring Boot, PostgreSQL, Docker",
                ExperienceLevel.MIDDLE,
                1
        );
        VacancyResponseDTO responseDTO = vacancies.getFirst();

        when(vacancyService.createVacancy(any(VacancyRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/vacancies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vacancyId").value(1))
                .andExpect(jsonPath("$.title").value("Middle Java developer"))
                .andExpect(jsonPath("$.description").value("Нужен Java разработчик 2+ года опыта"))
                .andExpect(jsonPath("$.salaryFrom").value(150000))
                .andExpect(jsonPath("$.salaryTo").value(200000))
                .andExpect(jsonPath("$.location").value("Москва"))
                .andExpect(jsonPath("$.employmentType").value("REMOTE"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.requiredSkills").value("Java, Spring Boot, PostgreSQL, Docker"))
                .andExpect(jsonPath("$.experienceLevel").value("MIDDLE"))
                .andExpect(jsonPath("$.companyId").value(1))
                .andExpect(jsonPath("$.companyName").value("Company1"))
                .andExpect(jsonPath("$.applicationsCount").value(3));

        verify(vacancyService, times(1)).createVacancy(any(VacancyRequestDTO.class));
    }

    @Test
    void getVacancyByIdTest() throws Exception {
        Integer id = 1;
        VacancyResponseDTO vacancy = vacancies.getFirst();

        when(vacancyService.getVacancyById(id)).thenReturn(vacancy);

        mockMvc.perform(get("/api/v1/vacancies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vacancyId").value(1))
                .andExpect(jsonPath("$.title").value("Middle Java developer"))
                .andExpect(jsonPath("$.requiredSkills").value("Java, Spring Boot, PostgreSQL, Docker"))
                .andExpect(jsonPath("$.experienceLevel").value("MIDDLE"));

        verify(vacancyService, times(1)).getVacancyById(id);
    }

    @Test
    void updateVacancyTest() throws Exception {
        Integer id = 1;

        VacancyRequestDTO request = new VacancyRequestDTO(
                "Middle Java Developer",
                "Нужен Java разработчик 5+ года опыта",
                150000,
                200000,
                "Москва",
                EmploymentType.REMOTE,
                VacancyStatus.OPEN,
                "Java, Spring Boot, PostgreSQL, Docker",
                ExperienceLevel.SENIOR,
                1
        );

        VacancyResponseDTO updated = new VacancyResponseDTO(
                1,
                "Middle Java developer",
                "Нужен Java разработчик 5+ года опыта",
                150000,
                200000,
                "Москва",
                EmploymentType.REMOTE,
                VacancyStatus.OPEN,
                "Java, Spring Boot, PostgreSQL, Docker",
                ExperienceLevel.SENIOR,
                1,
                "Company1",
                5
        );

        when(vacancyService.updateVacancy(eq(id), any(VacancyRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/vacancies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vacancyId").value(1))
                .andExpect(jsonPath("$.description").value("Нужен Java разработчик 5+ года опыта"))
                .andExpect(jsonPath("$.experienceLevel").value("SENIOR"));

        verify(vacancyService, times(1)).updateVacancy(eq(id), any(VacancyRequestDTO.class));
    }

    @Test
    void closeVacancyTest() throws Exception {
        Integer id = 1;
        doNothing().when(vacancyService).closeVacancy(id);

        mockMvc.perform(patch("/api/v1/vacancies/{id}/close", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(vacancyService, times(1)).closeVacancy(id);
    }

    @Test
    void deleteVacancyTest() throws Exception {
        Integer id = 1;
        doNothing().when(vacancyService).deleteVacancy(id);

        mockMvc.perform(delete("/api/v1/vacancies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(vacancyService, times(1)).deleteVacancy(id);
    }
}