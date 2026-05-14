//package ru.practice.mini_ats.service;
//
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import ru.practice.mini_ats.dto.Vacancy.VacancyRequestDTO;
//import ru.practice.mini_ats.dto.Vacancy.VacancyResponseDTO;
//import ru.practice.mini_ats.mapper.VacancyMapper;
//import ru.practice.mini_ats.models.Company;
//import ru.practice.mini_ats.models.Vacancy;
//import ru.practice.mini_ats.models.enums.EmploymentType;
//import ru.practice.mini_ats.models.enums.VacancyStatus;
//import ru.practice.mini_ats.repositories.CompanyRepository;
//import ru.practice.mini_ats.repositories.VacancyRepository;
//import ru.practice.mini_ats.services.VacancyService;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class VacancyServiceTest {
//
//    @Mock
//    private VacancyRepository vacancyRepository;
//
//    @Mock
//    private VacancyMapper vacancyMapper;
//
//    @Mock
//    private CompanyRepository companyRepository;
//
//    @InjectMocks
//    private VacancyService vacancyService;
//
//    private static Company getCompany() {
//        Company company = new Company();
//        company.setCompanyId(1);
//        company.setName("Test Company");
//        return company;
//    }
//
//    private static Vacancy getVacancy() {
//        Vacancy vacancy = new Vacancy();
//        vacancy.setVacancyId(1);
//        vacancy.setTitle("Java Developer");
//        vacancy.setDescription("Develop microservices");
//        vacancy.setSalaryFrom(200000);
//        vacancy.setSalaryTo(300000);
//        vacancy.setLocation("Moscow");
//        vacancy.setEmploymentType(EmploymentType.REMOTE);
//        vacancy.setStatus(VacancyStatus.DRAFT);
//        vacancy.setRequiredSkills(Map.of("Java", 5));
//        vacancy.setExperienceLevel(3);
//        vacancy.setCompany(getCompany());
//        return vacancy;
//    }
//
//    private static VacancyRequestDTO getRequestDto() {
//        return new VacancyRequestDTO(
//                "Java Developer",
//                "Develop microservices",
//                200000,
//                300000,
//                "Moscow",
//                EmploymentType.REMOTE,
//                VacancyStatus.DRAFT,
//                Map.of("Java", 5),
//                3,
//                1 // companyId
//        );
//    }
//
//    private static VacancyResponseDTO getResponseDto() {
//        return new VacancyResponseDTO(
//                1,
//                "Java Developer",
//                "Develop microservices",
//                200000,
//                300000,
//                "Moscow",
//                EmploymentType.REMOTE,
//                VacancyStatus.DRAFT,
//                Map.of("Java", 5),
//                3,
//                1,
//                "Test Company"
//        );
//    }
//
//    @Test
//    void createVacancy_WhenCompanyExists_ShouldReturnResponseDto() {
//        VacancyRequestDTO requestDto = getRequestDto();
//        Company company = getCompany();
//        Vacancy vacancyToSave = new Vacancy();
//        Vacancy savedVacancy = getVacancy();
//        VacancyResponseDTO expectedResponse = getResponseDto();
//
//        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
//        when(vacancyMapper.toEntity(requestDto)).thenReturn(vacancyToSave);
//        when(vacancyRepository.save(vacancyToSave)).thenReturn(savedVacancy);
//        when(vacancyMapper.toResponseDto(savedVacancy)).thenReturn(expectedResponse);
//
//        VacancyResponseDTO result = vacancyService.createVacancy(requestDto);
//
//        assertThat(result).isEqualTo(expectedResponse);
//        assertThat(vacancyToSave.getCompany()).isEqualTo(company);
//        verify(companyRepository).findById(1);
//        verify(vacancyMapper).toEntity(requestDto);
//        verify(vacancyRepository).save(vacancyToSave);
//        verify(vacancyMapper).toResponseDto(savedVacancy);
//    }
//
//    @Test
//    void createVacancy_WhenCompanyNotExists_ShouldThrowEntityNotFoundException() {
//        VacancyRequestDTO requestDto = getRequestDto();
//
//        when(companyRepository.findById(1)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> vacancyService.createVacancy(requestDto))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Указанная компания не существует");
//
//        verify(companyRepository).findById(1);
//        verify(vacancyMapper, never()).toEntity(any());
//        verify(vacancyRepository, never()).save(any());
//    }
//
//    @Test
//    void getByStatus_ShouldReturnListOfResponseDto() {
//        VacancyStatus status = VacancyStatus.DRAFT;
//        List<Vacancy> vacancies = List.of(getVacancy());
//        VacancyResponseDTO expectedDto = getResponseDto();
//
//        when(vacancyRepository.findByStatus(status)).thenReturn(vacancies);
//        when(vacancyMapper.toResponseDto(vacancies.getFirst())).thenReturn(expectedDto);
//
//        List<VacancyResponseDTO> result = vacancyService.getByStatus(status);
//
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst()).isEqualTo(expectedDto);
//        verify(vacancyRepository).findByStatus(status);
//        verify(vacancyMapper).toResponseDto(vacancies.getFirst());
//    }
//
//    @Test
//    void getByStatus_WhenNoVacancies_ShouldReturnEmptyList() {
//        VacancyStatus status = VacancyStatus.DRAFT;
//
//        when(vacancyRepository.findByStatus(status)).thenReturn(List.of());
//
//        List<VacancyResponseDTO> result = vacancyService.getByStatus(status);
//
//        assertThat(result).isEmpty();
//        verify(vacancyRepository).findByStatus(status);
//        verify(vacancyMapper, never()).toResponseDto(any());
//    }
//
//    @Test
//    void getVacancyById_WhenExists_ShouldReturnResponseDto() {
//        Vacancy vacancy = getVacancy();
//        VacancyResponseDTO expectedDto = getResponseDto();
//
//        when(vacancyRepository.findById(1)).thenReturn(Optional.of(vacancy));
//        when(vacancyMapper.toResponseDto(vacancy)).thenReturn(expectedDto);
//
//        VacancyResponseDTO result = vacancyService.getVacancyById(1);
//
//        assertThat(result).isEqualTo(expectedDto);
//        verify(vacancyRepository).findById(1);
//        verify(vacancyMapper).toResponseDto(vacancy);
//    }
//
//    @Test
//    void getVacancyById_WhenNotExists_ShouldThrowEntityNotFoundException() {
//        when(vacancyRepository.findById(999)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> vacancyService.getVacancyById(999))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Вакансия с id 999 не найдена");
//
//        verify(vacancyRepository).findById(999);
//        verify(vacancyMapper, never()).toResponseDto(any());
//    }
//
//    @Test
//    void updateVacancy_WhenExists_ShouldUpdateAndReturnResponseDto() {
//        Integer vacancyId = 1;
//        VacancyRequestDTO updateDto = getRequestDto();
//        Vacancy existingVacancy = getVacancy();
//        Vacancy updatedVacancy = getVacancy();
//        VacancyResponseDTO expectedResponse = getResponseDto();
//
//        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(existingVacancy));
//        doNothing().when(vacancyMapper).updateEntityFromDto(updateDto, existingVacancy);
//        when(vacancyRepository.save(existingVacancy)).thenReturn(updatedVacancy);
//        when(vacancyMapper.toResponseDto(updatedVacancy)).thenReturn(expectedResponse);
//
//        VacancyResponseDTO result = vacancyService.updateVacancy(vacancyId, updateDto);
//
//        assertThat(result).isEqualTo(expectedResponse);
//        verify(vacancyRepository).findById(vacancyId);
//        verify(vacancyMapper).updateEntityFromDto(updateDto, existingVacancy);
//        verify(vacancyRepository).save(existingVacancy);
//        verify(vacancyMapper).toResponseDto(updatedVacancy);
//    }
//
//    @Test
//    void updateVacancy_WhenNotExists_ShouldThrowEntityNotFoundException() {
//        Integer vacancyId = 999;
//        VacancyRequestDTO updateDto = getRequestDto();
//
//        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> vacancyService.updateVacancy(vacancyId, updateDto))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Вакансия с id 999 не найдена");
//
//        verify(vacancyRepository).findById(vacancyId);
//        verify(vacancyMapper, never()).updateEntityFromDto(any(), any());
//        verify(vacancyRepository, never()).save(any());
//    }
//
//    @Test
//    void closeVacancy_WhenExists_ShouldSetStatusClosedAndSave() {
//        Vacancy vacancy = getVacancy();
//        when(vacancyRepository.findById(1)).thenReturn(Optional.of(vacancy));
//        when(vacancyRepository.save(vacancy)).thenReturn(vacancy);
//
//        vacancyService.closeVacancy(1);
//
//        assertThat(vacancy.getStatus()).isEqualTo(VacancyStatus.CLOSED);
//        verify(vacancyRepository).findById(1);
//        verify(vacancyRepository).save(vacancy);
//    }
//
//    @Test
//    void closeVacancy_WhenNotExists_ShouldThrowEntityNotFoundException() {
//        when(vacancyRepository.findById(999)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> vacancyService.closeVacancy(999))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Вакансия с id 999 не найдена");
//
//        verify(vacancyRepository).findById(999);
//        verify(vacancyRepository, never()).save(any());
//    }
//
//    @Test
//    void deleteVacancy_WhenExists_ShouldDelete() {
//        when(vacancyRepository.existsById(1)).thenReturn(true);
//        doNothing().when(vacancyRepository).deleteById(1);
//
//        vacancyService.deleteVacancy(1);
//
//        verify(vacancyRepository).existsById(1);
//        verify(vacancyRepository).deleteById(1);
//    }
//
//    @Test
//    void deleteVacancy_WhenNotExists_ShouldThrowEntityNotFoundException() {
//        when(vacancyRepository.existsById(999)).thenReturn(false);
//
//        assertThatThrownBy(() -> vacancyService.deleteVacancy(999))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Вакансия с id 999 не найдена");
//
//        verify(vacancyRepository).existsById(999);
//        verify(vacancyRepository, never()).deleteById(any());
//    }
//}