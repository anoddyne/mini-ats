package ru.practice.mini_ats.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.mini_ats.dto.Vacancy.VacancyRequestDTO;
import ru.practice.mini_ats.dto.Vacancy.VacancyResponseDTO;
import ru.practice.mini_ats.mapper.VacancyMapper;
import ru.practice.mini_ats.models.Company;
import ru.practice.mini_ats.models.Vacancy;
import ru.practice.mini_ats.models.enums.VacancyStatus;
import ru.practice.mini_ats.repositories.CompanyRepository;
import ru.practice.mini_ats.repositories.VacancyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final CompanyRepository companyRepository;

    @Transactional
    public VacancyResponseDTO createVacancy(VacancyRequestDTO dto) {
        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new EntityNotFoundException("Указанная компания не существует"));
        Vacancy vacancy = vacancyMapper.toEntity(dto);
        vacancy.setCompany(company);
        return vacancyMapper.toResponseDto(vacancyRepository.save(vacancy));
    }

    @Transactional(readOnly = true)
    public List<VacancyResponseDTO> getByStatus(VacancyStatus status) {
        return vacancyRepository.findByStatus(status).stream().map(vacancyMapper::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public VacancyResponseDTO getVacancyById(Integer id) {
        Vacancy vacancy = vacancyRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Вакансия с id " + id + " не найдена"));
        return vacancyMapper.toResponseDto(vacancy);
    }

    @Transactional
    public VacancyResponseDTO updateVacancy(Integer id, VacancyRequestDTO dto) {
        Vacancy vacancy = vacancyRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Вакансия с id " + id + " не найдена"));
        vacancyMapper.updateEntityFromDto(dto, vacancy);
        return vacancyMapper.toResponseDto(vacancyRepository.save(vacancy));
    }

    @Transactional
    public void closeVacancy(Integer id) {
        Vacancy vacancy = vacancyRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Вакансия с id " + id + " не найдена"));
        vacancy.setStatus(VacancyStatus.CLOSED);
        vacancyRepository.save(vacancy);
    }

    @Transactional
    public void deleteVacancy(Integer id) {
        if (!vacancyRepository.existsById(id)) {
            throw new EntityNotFoundException("Вакансия с id " + id + " не найдена");
        }
        vacancyRepository.deleteById(id);
    }
}
