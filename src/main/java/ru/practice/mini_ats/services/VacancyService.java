package ru.practice.mini_ats.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.mini_ats.dto.Vacancy.VacancyRequestDTO;
import ru.practice.mini_ats.dto.Vacancy.VacancyResponseDTO;
import ru.practice.mini_ats.mapper.VacancyMapper;
import ru.practice.mini_ats.models.Company;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.Vacancy;
import ru.practice.mini_ats.models.enums.VacancyStatus;
import ru.practice.mini_ats.repositories.CompanyRepository;
import ru.practice.mini_ats.repositories.UserRepository;
import ru.practice.mini_ats.repositories.VacancyRepository;
import ru.practice.mini_ats.security.SecurityUtils;

import java.nio.file.AccessDeniedException;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final CompanyRepository companyRepository;
private final UserRepository userRepository;

    @Transactional
    public VacancyResponseDTO createVacancy(VacancyRequestDTO dto) throws AccessDeniedException {
        String login = SecurityUtils.getCurrentUserLogin();
        User recruiter = userRepository.findByLogin(login).orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Company company = companyRepository.findById(dto.companyId())
                .orElseThrow(() -> new EntityNotFoundException("Указанная компания не существует"));

        boolean isMember = company.getRecruiters().contains(recruiter);

        if (!isMember) {
            throw new AccessDeniedException("Вы не являетесь рекрутером этой компании и не можете создавать вакансии от её имени");
        }
        Vacancy vacancy = vacancyMapper.toEntity(dto);
        vacancy.setCompany(company);
        return vacancyMapper.toResponseDto(vacancyRepository.save(vacancy));
    }

    @Deprecated
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

    @Transactional(readOnly = true)
    public Page<VacancyResponseDTO> getVacanciesByStatus(Pageable pageable, VacancyStatus status) {
        return vacancyRepository.findAllByStatus(status, pageable)
                .map(vacancyMapper::toResponseDto);
    }
}
