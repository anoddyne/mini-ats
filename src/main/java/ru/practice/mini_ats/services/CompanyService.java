package ru.practice.mini_ats.services;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.practice.mini_ats.dto.Company.CompanyRequestDTO;
import ru.practice.mini_ats.dto.Company.CompanyResponseDTO;
import ru.practice.mini_ats.mapper.CompanyMapper;
import ru.practice.mini_ats.models.Company;
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.repositories.CompanyRepository;
import ru.practice.mini_ats.repositories.UserRepository;
import ru.practice.mini_ats.security.SecurityUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final FileService fileService;
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final UserRepository userRepository;

    @Transactional
    public CompanyResponseDTO createCompany(CompanyRequestDTO dto, MultipartFile logo) {
        String login = SecurityUtils.getCurrentUserLogin();
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        String fileName = fileService.uploadFile(logo, login, fileService.getCompanyBucketName());
        String fileUrl = fileService.getPublicFileUrl(fileService.getCompanyBucketName(), fileName);

        Company company = companyMapper.toEntity(dto);
        company.setLogoUrl(fileUrl);
        company.setFileName(fileName);
        company.getRecruiters().add(user);

        return companyMapper.toResponseDto(companyRepository.save(company));
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

    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> getMyCompanies() {
        String login = SecurityUtils.getCurrentUserLogin();
        return companyRepository.findAllByRecruiterLogin(login)
                .stream()
                .map(companyMapper::toResponseDto)
                .toList();
    }
}
