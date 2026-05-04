package ru.practice.mini_ats.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.mini_ats.dto.Company.CompanyRequestDTO;
import ru.practice.mini_ats.dto.Company.CompanyResponseDTO;
import ru.practice.mini_ats.mapper.CompanyMapper;
import ru.practice.mini_ats.models.Company;
import ru.practice.mini_ats.repositories.CompanyRepository;
import ru.practice.mini_ats.services.CompanyService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyService companyService;

    private static Company getCompany() {
        Company company = new Company();
        company.setCompanyId(1);
        company.setName("Test Company");
        company.setDescription("Description");
        company.setLogoUrl("http://logo.url");
        return company;
    }

    private static CompanyRequestDTO getRequestDto() {
        return new CompanyRequestDTO("Test Company", "Description", "http://logo.url");
    }

    private static CompanyResponseDTO getResponseDto() {
        return new CompanyResponseDTO(1, "Test Company", "Description", "http://logo.url");
    }

    @Test
    void createCompany_ShouldReturnResponseDto() {
        CompanyRequestDTO requestDto = getRequestDto();
        Company companyToSave = new Company();
        Company savedCompany = getCompany();
        CompanyResponseDTO responseDto = getResponseDto();

        when(companyMapper.toEntity(requestDto)).thenReturn(companyToSave);
        when(companyRepository.save(companyToSave)).thenReturn(savedCompany);
        when(companyMapper.toResponseDto(savedCompany)).thenReturn(responseDto);

        CompanyResponseDTO result = companyService.createCompany(requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(companyMapper).toEntity(requestDto);
        verify(companyRepository).save(companyToSave);
        verify(companyMapper).toResponseDto(savedCompany);
    }

    @Test
    void getAllCompanies_ShouldReturnListOfResponseDto() {
        List<Company> companies = List.of(getCompany());
        List<CompanyResponseDTO> expectedDtos = List.of(getResponseDto());

        when(companyRepository.findAll()).thenReturn(companies);
        when(companyMapper.toResponseDto(companies.get(0))).thenReturn(expectedDtos.get(0));

        List<CompanyResponseDTO> result = companyService.getAllCompanies();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(expectedDtos.get(0));
        verify(companyRepository).findAll();
        verify(companyMapper).toResponseDto(companies.get(0));
    }

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

    @Test
    void updateCompany_WhenExists_ShouldUpdateAndReturnResponseDto() {
        Integer companyId = 1;
        CompanyRequestDTO updateDto = new CompanyRequestDTO("Updated Name", "Updated desc", null);
        Company existingCompany = getCompany();
        Company updatedCompany = getCompany();
        updatedCompany.setName("Updated Name");
        updatedCompany.setDescription("Updated desc");
        updatedCompany.setLogoUrl(null);
        CompanyResponseDTO expectedResponse = new CompanyResponseDTO(1, "Updated Name", "Updated desc", null);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(existingCompany));
        doNothing().when(companyMapper).updateEntityFromDto(updateDto, existingCompany);
        when(companyRepository.save(existingCompany)).thenReturn(updatedCompany);
        when(companyMapper.toResponseDto(updatedCompany)).thenReturn(expectedResponse);

        CompanyResponseDTO result = companyService.updateCompany(companyId, updateDto);

        assertThat(result).isEqualTo(expectedResponse);
        verify(companyRepository).findById(companyId);
        verify(companyMapper).updateEntityFromDto(updateDto, existingCompany);
        verify(companyRepository).save(existingCompany);
        verify(companyMapper).toResponseDto(updatedCompany);
    }

    @Test
    void updateCompany_WhenNotExists_ShouldThrowEntityNotFoundException() {
        Integer companyId = 999;
        CompanyRequestDTO updateDto = getRequestDto();

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.updateCompany(companyId, updateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Компания с id 999 не найдена");

        verify(companyRepository).findById(companyId);
        verifyNoMoreInteractions(companyRepository);
        verify(companyMapper, never()).updateEntityFromDto(any(), any());
    }
}