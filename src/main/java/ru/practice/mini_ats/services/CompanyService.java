package ru.practice.mini_ats.services;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.mini_ats.dto.Company.CompanyRequestDTO;
import ru.practice.mini_ats.dto.Company.CompanyResponseDTO;
import ru.practice.mini_ats.mapper.CompanyMapper;
import ru.practice.mini_ats.models.Company;
import ru.practice.mini_ats.repositories.CompanyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Transactional
    public CompanyResponseDTO createCompany(CompanyRequestDTO dto) {
        Company company = companyMapper.toEntity(dto);
        Company savedCompany = companyRepository.save(company);
        return companyMapper.toResponseDto(savedCompany);
    }

    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyResponseDTO getCompanyById(Integer id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Компания с id " + id + " не найдена"));
        return companyMapper.toResponseDto(company);
    }

    @Transactional
    public void deleteCompany(Integer id) {
        if (!companyRepository.existsById(id)) {
            throw new EntityNotFoundException("Не удалось удалить: компания с id" + id + " не существует");
        }
        companyRepository.deleteById(id);
    }

    @Transactional
    public CompanyResponseDTO updateCompany(Integer id, CompanyRequestDTO dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Компания с id " + id + " не найдена"));
        companyMapper.updateEntityFromDto(dto, company);
        Company updatedCompany = companyRepository.save(company);
        return companyMapper.toResponseDto(updatedCompany);
    }
}
