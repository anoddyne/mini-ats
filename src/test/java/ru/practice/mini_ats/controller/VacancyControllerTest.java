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
import ru.practice.mini_ats.controllers.VacancyController;
import ru.practice.mini_ats.dto.Vacancy.VacancyRequestDTO;
import ru.practice.mini_ats.dto.Vacancy.VacancyResponseDTO;
import ru.practice.mini_ats.models.enums.EmploymentType;
import ru.practice.mini_ats.models.enums.VacancyStatus;
import ru.practice.mini_ats.services.VacancyService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
                    Map.of(
                            "Java", 5,
                            "Spring Boot", 4,
                            "PostgreSQL", 3,
                            "Docker", 2
                    ),
                    3,
                    1,
                    "Company1"
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
                    Map.of(
                            "С++", 5,
                            "PostgreSQL", 3,
                            "Docker", 2
                    ),
                    3,
                    2,
                    "Company2"
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
                Map.of(
                        "Java", 5,
                        "Spring Boot", 4,
                        "PostgreSQL", 3,
                        "Docker", 2
                ),
                3,
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
                .andExpect(jsonPath("$.requiredSkills").isMap())
                .andExpect(jsonPath("$.requiredSkills.Java").value(5))
                .andExpect(jsonPath("$.requiredSkills['Spring Boot']").value(4))
                .andExpect(jsonPath("$.requiredSkills.PostgreSQL").value(3))
                .andExpect(jsonPath("$.requiredSkills.Docker").value(2))
                .andExpect(jsonPath("$.experienceLevel").value(3))
                .andExpect(jsonPath("$.companyId").value(1))
                .andExpect(jsonPath("$.companyName").value("Company1"));

        verify(vacancyService, times(1)).createVacancy(any(VacancyRequestDTO.class));
    }

    @Test
    void getListVacancies_OpenStatusTest() throws Exception {
        when(vacancyService.getByStatus(VacancyStatus.OPEN)).thenReturn(List.of(vacancies.getFirst()));

        mockMvc.perform(get("/api/v1/vacancies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].vacancyId").value(1))
                .andExpect(jsonPath("$[0].title").value("Middle Java developer"))
                .andExpect(jsonPath("$[0].description").value("Нужен Java разработчик 2+ года опыта"))
                .andExpect(jsonPath("$[0].salaryFrom").value(150000))
                .andExpect(jsonPath("$[0].salaryTo").value(200000))
                .andExpect(jsonPath("$[0].location").value("Москва"))
                .andExpect(jsonPath("$[0].employmentType").value("REMOTE"))
                .andExpect(jsonPath("$[0].status").value("OPEN"))
                .andExpect(jsonPath("$[0].requiredSkills").isMap())
                .andExpect(jsonPath("$[0].requiredSkills.Java").value(5))
                .andExpect(jsonPath("$[0].requiredSkills['Spring Boot']").value(4))
                .andExpect(jsonPath("$[0].requiredSkills.PostgreSQL").value(3))
                .andExpect(jsonPath("$[0].requiredSkills.Docker").value(2))
                .andExpect(jsonPath("$[0].experienceLevel").value(3))
                .andExpect(jsonPath("$[0].companyId").value(1))
                .andExpect(jsonPath("$[0].companyName").value("Company1"));


        verify(vacancyService, times(1)).getByStatus(VacancyStatus.OPEN);
    }

    @Test
    void getListVacancies_WithClosedStatusTest() throws Exception {
        when(vacancyService.getByStatus(VacancyStatus.CLOSED)).thenReturn(List.of(vacancies.get(1)));

        mockMvc.perform(get("/api/v1/vacancies")
                        .param("status", "CLOSED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].vacancyId").value(2))
                .andExpect(jsonPath("$[0].title").value("Middle C++ developer"))
                .andExpect(jsonPath("$[0].description").value("Нужен C++ разработчик 2+ года опыта"))
                .andExpect(jsonPath("$[0].salaryFrom").value(150000))
                .andExpect(jsonPath("$[0].salaryTo").value(200000))
                .andExpect(jsonPath("$[0].location").value("Москва"))
                .andExpect(jsonPath("$[0].employmentType").value("REMOTE"))
                .andExpect(jsonPath("$[0].status").value("CLOSED"))
                .andExpect(jsonPath("$[0].requiredSkills").isMap())
                .andExpect(jsonPath("$[0].requiredSkills['С++']").value(5))
                .andExpect(jsonPath("$[0].requiredSkills.PostgreSQL").value(3))
                .andExpect(jsonPath("$[0].requiredSkills.Docker").value(2))
                .andExpect(jsonPath("$[0].experienceLevel").value(3))
                .andExpect(jsonPath("$[0].companyId").value(2))
                .andExpect(jsonPath("$[0].companyName").value("Company2"));

        verify(vacancyService, times(1)).getByStatus(VacancyStatus.CLOSED);
    }

    @Test
    void getListVacancies_EmptyListTest() throws Exception {
        when(vacancyService.getByStatus(VacancyStatus.OPEN)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/vacancies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));

        verify(vacancyService, times(1)).getByStatus(VacancyStatus.OPEN);
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
                .andExpect(jsonPath("$.description").value("Нужен Java разработчик 2+ года опыта"))
                .andExpect(jsonPath("$.salaryFrom").value(150000))
                .andExpect(jsonPath("$.salaryTo").value(200000))
                .andExpect(jsonPath("$.location").value("Москва"))
                .andExpect(jsonPath("$.employmentType").value("REMOTE"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.requiredSkills").isMap())
                .andExpect(jsonPath("$.requiredSkills.Java").value(5))
                .andExpect(jsonPath("$.requiredSkills['Spring Boot']").value(4))
                .andExpect(jsonPath("$.requiredSkills.PostgreSQL").value(3))
                .andExpect(jsonPath("$.requiredSkills.Docker").value(2))
                .andExpect(jsonPath("$.experienceLevel").value(3))
                .andExpect(jsonPath("$.companyId").value(1))
                .andExpect(jsonPath("$.companyName").value("Company1"));

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
                Map.of(
                        "Java", 5,
                        "Spring Boot", 4,
                        "PostgreSQL", 3,
                        "Docker", 2
                ),
                5,
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
                Map.of(
                        "Java", 5,
                        "Spring Boot", 4,
                        "PostgreSQL", 3,
                        "Docker", 2
                ),
                5,
                1,
                "Company1"
        );

        when(vacancyService.updateVacancy(eq(id), any(VacancyRequestDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/vacancies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vacancyId").value(1))
                .andExpect(jsonPath("$.title").value("Middle Java developer"))
                .andExpect(jsonPath("$.description").value("Нужен Java разработчик 5+ года опыта"))
                .andExpect(jsonPath("$.salaryFrom").value(150000))
                .andExpect(jsonPath("$.salaryTo").value(200000))
                .andExpect(jsonPath("$.location").value("Москва"))
                .andExpect(jsonPath("$.employmentType").value("REMOTE"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.requiredSkills").isMap())
                .andExpect(jsonPath("$.requiredSkills.Java").value(5))
                .andExpect(jsonPath("$.requiredSkills['Spring Boot']").value(4))
                .andExpect(jsonPath("$.requiredSkills.PostgreSQL").value(3))
                .andExpect(jsonPath("$.requiredSkills.Docker").value(2))
                .andExpect(jsonPath("$.experienceLevel").value(5))
                .andExpect(jsonPath("$.companyId").value(1))
                .andExpect(jsonPath("$.companyName").value("Company1"));

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
}