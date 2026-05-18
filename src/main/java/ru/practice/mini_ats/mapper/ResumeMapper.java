package ru.practice.mini_ats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.models.Resume;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    @Mapping(source = "user.userId", target = "userId")
    ResumeResponseDTO toResponseDto(Resume resume);
}