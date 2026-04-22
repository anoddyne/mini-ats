package ru.practice.mini_ats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practice.mini_ats.dto.Resume.ResumeRequestDTO;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.models.Resume;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    ResumeResponseDTO toResponseDto(Resume resume);

    @Mapping(target = "resumeId", ignore = true)
    @Mapping(target = "user", ignore = true)
    Resume toEntity(ResumeRequestDTO dto);
}
