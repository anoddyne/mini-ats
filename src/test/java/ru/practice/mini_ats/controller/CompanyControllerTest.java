//package ru.practice.mini_ats.controller;
//
//import org.hamcrest.Matchers;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import ru.practice.mini_ats.controllers.CompanyController;
//import ru.practice.mini_ats.dto.Company.CompanyRequestDTO;
//import ru.practice.mini_ats.dto.Company.CompanyResponseDTO;
//import ru.practice.mini_ats.services.CompanyService;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//public class CompanyControllerTest {
//
//    @Mock
//    private CompanyService companyService;
//
//    @InjectMocks
//    private CompanyController companyController;
//
//    private MockMvc mockMvc;
//    private ObjectMapper objectMapper;
//
//    private static final List<CompanyResponseDTO> companies = List.of(
//            new CompanyResponseDTO(1, "Company A", "Description A","http://localhost:3000"),
//            new CompanyResponseDTO(2, "Company B", "Description B","http://localhost:3001")
//    );
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(companyController).build();
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    void createCompanyWithValidRequestTest() throws Exception {
//        CompanyRequestDTO requestDTO = new CompanyRequestDTO("New Company", "New Description","http://localhost:3001");
//        CompanyResponseDTO responseDTO = new CompanyResponseDTO(1, "New Company", "New Description","http://localhost:3001");
//
//        when(companyService.createCompany(any(CompanyRequestDTO.class))).thenReturn(responseDTO);
//
//        mockMvc.perform(post("/api/v1/companies")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.companyId").value(1))
//                .andExpect(jsonPath("$.name").value("New Company"))
//                .andExpect(jsonPath("$.description").value("New Description"));
//
//        verify(companyService, times(1)).createCompany(any(CompanyRequestDTO.class));
//    }
//
//    @Test
//    void getNotEmptyListCompaniesTest() throws Exception {
//        when(companyService.getAllCompanies()).thenReturn(companies);
//
//        mockMvc.perform(get("/api/v1/companies")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", Matchers.hasSize(2)))
//
//                .andExpect(jsonPath("$[0].companyId").value(1))
//                .andExpect(jsonPath("$[0].name").value("Company A"))
//                .andExpect(jsonPath("$[0].description").value("Description A"))
//
//                .andExpect(jsonPath("$[1].companyId").value(2))
//                .andExpect(jsonPath("$[1].name").value("Company B"))
//                .andExpect(jsonPath("$[1].description").value("Description B"));
//
//        verify(companyService, times(1)).getAllCompanies();
//    }
//
//    @Test
//    void getEmptyListCompaniesTest() throws Exception {
//        when(companyService.getAllCompanies()).thenReturn(Collections.emptyList());
//
//        mockMvc.perform(get("/api/v1/companies")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", Matchers.hasSize(0)));
//        verify(companyService, times(1)).getAllCompanies();
//    }
//
//    @Test
//    void getCompanyByIdTest() throws Exception {
//        Integer id = 1;
//        CompanyResponseDTO company = companies.getFirst();
//
//        when(companyService.getCompanyById(id)).thenReturn(company);
//
//        mockMvc.perform(get("/api/v1/companies/{id}", id)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.companyId").value(1))
//                .andExpect(jsonPath("$.name").value("Company A"))
//                .andExpect(jsonPath("$.description").value("Description A"));
//
//        verify(companyService, times(1)).getCompanyById(id);
//    }
//
//    @Test
//    void updateCompanyTest() throws Exception {
//        Integer id = 1;
//        CompanyRequestDTO updateRequest = new CompanyRequestDTO("Company A", "Updated Description","http://localhost:3000");
//        CompanyResponseDTO updatedResponse = new CompanyResponseDTO(1, "Updated Company", "Updated Description","http://localhost:3000");
//
//        when(companyService.updateCompany(eq(id), any(CompanyRequestDTO.class))).thenReturn(updatedResponse);
//
//        mockMvc.perform(put("/api/v1/companies/{id}", id)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.companyId").value(1))
//                .andExpect(jsonPath("$.name").value("Updated Company"))
//                .andExpect(jsonPath("$.description").value("Updated Description"));
//
//        verify(companyService, times(1)).updateCompany(eq(id), any(CompanyRequestDTO.class));
//    }
//
//    @Test
//    void deleteCompanyTest() throws Exception {
//        Integer id = 1;
//        doNothing().when(companyService).deleteCompany(id);
//
//        mockMvc.perform(delete("/api/v1/companies/{id}", id))
//                .andExpect(status().isNoContent());
//
//        verify(companyService, times(1)).deleteCompany(id);
//    }
//}