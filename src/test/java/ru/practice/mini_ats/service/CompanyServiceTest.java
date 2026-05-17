package ru.practice.mini_ats.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.practice.mini_ats.dto.Company.CompanyRequestDTO;
import ru.practice.mini_ats.dto.Company.CompanyResponseDTO;
import ru.practice.mini_ats.mapper.CompanyMapper;
import ru.practice.mini_ats.models.Company;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.repositories.CompanyRepository;
import ru.practice.mini_ats.repositories.UserRepository;
import ru.practice.mini_ats.security.SecurityUtils;
import ru.practice.mini_ats.services.FileService;
import ru.practice.mini_ats.services.CompanyService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private FileService fileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile logoFile;

    @InjectMocks
    private CompanyService companyService;

    private static final String CURRENT_LOGIN = "recruiter";
    private static final int USER_ID = 1;
    private static final String BUCKET_NAME = "company-logos";
    private static final String LOGO_FILE_NAME = "company_logo.png";
    private static final String LOGO_URL = "https://storage.example.com/company-logos/recruiter_company_logo.png";

    private User getUser() {
        User user = new User();
        user.setUserId(USER_ID);
        user.setLogin(CURRENT_LOGIN);
        return user;
    }

    private Company getCompany() {
        Company company = new Company();
        company.setCompanyId(1);
        company.setName("Test Company");
        company.setDescription("Description");
        company.setLogoUrl(LOGO_URL);
        company.setFileName(LOGO_FILE_NAME);
        return company;
    }

    private CompanyRequestDTO getRequestDto() {
        return new CompanyRequestDTO("Test Company", "Description");
    }

    private CompanyResponseDTO getResponseDto() {
        return new CompanyResponseDTO(1, "Test Company", "Description", LOGO_URL, LOGO_FILE_NAME);
    }

    // ==================== CREATE COMPANY ====================

    @Test
    void createCompany_ShouldUploadLogoAndSaveCompany() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            User user = getUser();
            CompanyRequestDTO requestDto = getRequestDto();
            Company companyToSave = new Company();
            Company savedCompany = getCompany();
            CompanyResponseDTO expectedResponse = getResponseDto();

            when(fileService.getCompanyBucketName()).thenReturn(BUCKET_NAME);
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(fileService.uploadFile(logoFile, CURRENT_LOGIN, BUCKET_NAME)).thenReturn(LOGO_FILE_NAME);
            when(fileService.getPublicFileUrl(BUCKET_NAME, LOGO_FILE_NAME)).thenReturn(LOGO_URL);
            when(companyMapper.toEntity(requestDto)).thenReturn(companyToSave);
            when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);
            when(companyMapper.toResponseDto(savedCompany)).thenReturn(expectedResponse);

            CompanyResponseDTO result = companyService.createCompany(requestDto, logoFile);

            assertThat(result).isEqualTo(expectedResponse);
            assertThat(companyToSave.getLogoUrl()).isEqualTo(LOGO_URL);
            assertThat(companyToSave.getFileName()).isEqualTo(LOGO_FILE_NAME);
            assertThat(companyToSave.getRecruiters()).contains(user);
            verify(fileService).uploadFile(logoFile, CURRENT_LOGIN, BUCKET_NAME);
            verify(fileService).getPublicFileUrl(BUCKET_NAME, LOGO_FILE_NAME);
            verify(companyRepository).save(companyToSave);
            verify(companyMapper).toResponseDto(savedCompany);
        }
    }

    @Test
    void createCompany_WhenUserNotFound_ShouldThrowEntityNotFoundException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.createCompany(getRequestDto(), logoFile))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Пользователь не найден");

            verify(fileService, never()).uploadFile(any(), any(), any());
            verify(companyRepository, never()).save(any());
        }
    }

    // ==================== GET ALL COMPANIES ====================

    @Test
    void getAllCompanies_ShouldReturnListOfResponseDto() {
        List<Company> companies = List.of(getCompany());
        List<CompanyResponseDTO> expectedDtos = List.of(getResponseDto());

        when(companyRepository.findAll()).thenReturn(companies);
        when(companyMapper.toResponseDto(companies.getFirst())).thenReturn(expectedDtos.getFirst());

        List<CompanyResponseDTO> result = companyService.getAllCompanies();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(expectedDtos.getFirst());
        verify(companyRepository).findAll();
        verify(companyMapper).toResponseDto(companies.getFirst());
    }

    // ==================== GET COMPANY BY ID ====================

    @Test
    void getCompanyById_WhenExists_ShouldReturnResponseDto() {
        Company company = getCompany();
        CompanyResponseDTO responseDto = getResponseDto();

        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
        when(companyMapper.toResponseDto(company)).thenReturn(responseDto);

        CompanyResponseDTO result = companyService.getCompanyById(1);

        assertThat(result).isEqualTo(responseDto);
        verify(companyRepository).findById(1);
        verify(companyMapper).toResponseDto(company);
    }

    @Test
    void getCompanyById_WhenNotExists_ShouldThrowEntityNotFoundException() {
        when(companyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.getCompanyById(999))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Компания с id 999 не найдена");

        verify(companyRepository).findById(999);
        verifyNoInteractions(companyMapper);
    }

    // ==================== DELETE COMPANY ====================

    @Test
    void deleteCompany_WhenExists_ShouldDelete() {
        when(companyRepository.existsById(1)).thenReturn(true);
        doNothing().when(companyRepository).deleteById(1);

        companyService.deleteCompany(1);

        verify(companyRepository).existsById(1);
        verify(companyRepository).deleteById(1);
    }

    @Test
    void deleteCompany_WhenNotExists_ShouldThrowEntityNotFoundException() {
        when(companyRepository.existsById(999)).thenReturn(false);

        assertThatThrownBy(() -> companyService.deleteCompany(999))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Не удалось удалить: компания с id999 не существует");

        verify(companyRepository).existsById(999);
        verify(companyRepository, never()).deleteById(any());
    }

    // ==================== UPDATE COMPANY ====================

    @Test
    void updateCompany_WhenExistsAndLogoProvided_ShouldUpdateNameDescAndReplaceLogo() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            Integer companyId = 1;
            CompanyRequestDTO updateDto = new CompanyRequestDTO("Updated Name", "Updated desc");
            MultipartFile newLogo = mock(MultipartFile.class);
            Company existingCompany = getCompany();
            Company updatedCompany = getCompany();
            updatedCompany.setName("Updated Name");
            updatedCompany.setDescription("Updated desc");
            updatedCompany.setFileName("new_logo.png");
            updatedCompany.setLogoUrl("https://new.url/logo.png");

            CompanyResponseDTO expectedResponse = new CompanyResponseDTO(1, "Updated Name", "Updated desc",
                    "https://new.url/logo.png", "new_logo.png");

            User user = getUser();
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(companyRepository.findById(companyId)).thenReturn(Optional.of(existingCompany));
            when(fileService.getCompanyBucketName()).thenReturn(BUCKET_NAME);
            when(newLogo.isEmpty()).thenReturn(false);
            when(fileService.uploadFile(newLogo, CURRENT_LOGIN, BUCKET_NAME)).thenReturn("new_logo.png");
            when(fileService.getPublicFileUrl(BUCKET_NAME, "new_logo.png")).thenReturn("https://new.url/logo.png");
            doNothing().when(fileService).deleteFile(BUCKET_NAME, existingCompany.getFileName());
            when(companyRepository.save(any(Company.class))).thenReturn(updatedCompany);
            when(companyMapper.toResponseDto(updatedCompany)).thenReturn(expectedResponse);

            CompanyResponseDTO result = companyService.updateCompany(updateDto, newLogo, companyId);

            assertThat(result).isEqualTo(expectedResponse);
            assertThat(existingCompany.getName()).isEqualTo("Updated Name");
            assertThat(existingCompany.getDescription()).isEqualTo("Updated desc");
            verify(fileService).deleteFile(BUCKET_NAME, LOGO_FILE_NAME);
            verify(fileService).uploadFile(newLogo, CURRENT_LOGIN, BUCKET_NAME);
            verify(companyRepository).save(existingCompany);
        }
    }

    @Test
    void updateCompany_WhenExistsAndNoLogoProvided_ShouldUpdateOnlyNameDesc() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            Integer companyId = 1;
            CompanyRequestDTO updateDto = new CompanyRequestDTO("Updated Name", "Updated desc");
            Company existingCompany = getCompany();
            Company updatedCompany = getCompany();
            updatedCompany.setName("Updated Name");
            updatedCompany.setDescription("Updated desc");

            CompanyResponseDTO expectedResponse = new CompanyResponseDTO(1, "Updated Name", "Updated desc",
                    LOGO_URL, LOGO_FILE_NAME);

            User user = getUser();
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(companyRepository.findById(companyId)).thenReturn(Optional.of(existingCompany));
            when(companyRepository.save(any(Company.class))).thenReturn(updatedCompany);
            when(companyMapper.toResponseDto(updatedCompany)).thenReturn(expectedResponse);

            CompanyResponseDTO result = companyService.updateCompany(updateDto, null, companyId);

            assertThat(result).isEqualTo(expectedResponse);
            assertThat(existingCompany.getName()).isEqualTo("Updated Name");
            assertThat(existingCompany.getDescription()).isEqualTo("Updated desc");
            verify(fileService, never()).deleteFile(anyString(), anyString());
            verify(fileService, never()).uploadFile(any(), any(), any());
            verify(companyRepository).save(existingCompany);
        }
    }

    @Test
    void updateCompany_WhenNotExists_ShouldThrowEntityNotFoundException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            Integer companyId = 999;
            CompanyRequestDTO updateDto = getRequestDto();
            User user = getUser();

            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.of(user));
            when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.updateCompany(updateDto, null, companyId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Компания с id 999 не найдена");

            verify(companyRepository).findById(companyId);
            verify(companyRepository, never()).save(any());
            verify(fileService, never()).deleteFile(anyString(), anyString());
        }
    }

    @Test
    void updateCompany_WhenUserNotFound_ShouldThrowEntityNotFoundException() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);
            when(userRepository.findByLogin(CURRENT_LOGIN)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.updateCompany(getRequestDto(), null, 1))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Пользователь не найден");
        }
    }

    // ==================== GET MY COMPANIES ====================

    @Test
    void getMyCompanies_ShouldReturnCompaniesOfCurrentUser() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(CURRENT_LOGIN);

            List<Company> companies = List.of(getCompany());
            List<CompanyResponseDTO> expectedDtos = List.of(getResponseDto());

            when(companyRepository.findAllByRecruiterLogin(CURRENT_LOGIN)).thenReturn(companies);
            when(companyMapper.toResponseDto(companies.getFirst())).thenReturn(expectedDtos.getFirst());

            List<CompanyResponseDTO> result = companyService.getMyCompanies();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst()).isEqualTo(expectedDtos.getFirst());
            verify(companyRepository).findAllByRecruiterLogin(CURRENT_LOGIN);
            verify(companyMapper).toResponseDto(companies.getFirst());
        }
    }
}